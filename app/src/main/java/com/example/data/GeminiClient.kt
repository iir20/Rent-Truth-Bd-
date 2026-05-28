package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API key is missing. Please enter your Gemini API key in the AI Studio Secrets panel."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        val jsonPayload = buildPayload(prompt, systemInstruction)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonPayload.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    return@withContext "Error backend: Code ${response.code} - ${response.message}\n$bodyString"
                }

                val parsed = parseResponse(bodyString)
                if (parsed.isEmpty()) {
                    "No diagnostic text returned from Gemini API."
                } else {
                    parsed
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "API call failed", e)
            "Error: ${e.localizedMessage ?: "Failed to connect to AI server. Please verify your connection."}"
        }
    }

    private fun buildPayload(prompt: String, systemInstruction: String?): String {
        val escapedPrompt = escapeJsonString(prompt)
        val systemBlock = if (systemInstruction != null) {
            """,
            "systemInstruction": {
                "parts": [
                    { "text": "${escapeJsonString(systemInstruction)}" }
                ]
            }"""
        } else ""

        return """
            {
                "contents": [
                    {
                        "parts": [
                            { "text": "$escapedPrompt" }
                        ]
                    }
                ]$systemBlock
            }
        """.trimIndent()
    }

    private fun escapeJsonString(str: String): String {
        val sb = StringBuilder()
        for (i in str.indices) {
            val ch = str[i]
            when (ch) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> {
                    if (ch.code < 32) {
                        val ss = "0000" + Integer.toHexString(ch.code)
                        sb.append("\\u" + ss.substring(ss.length - 4))
                    } else {
                        sb.append(ch)
                    }
                }
            }
        }
        return sb.toString()
    }

    private fun parseResponse(rawJson: String): String {
        return try {
            val searchToken = "\"text\":"
            var index = rawJson.indexOf(searchToken)
            if (index == -1) return "Unable to decode text from model."

            index += searchToken.length
            while (index < rawJson.length && rawJson[index] != '"') {
                index++
            }
            if (index >= rawJson.length) return ""
            index++ // Step past first double-quote

            val sb = java.lang.StringBuilder()
            var escaped = false
            while (index < rawJson.length) {
                val char = rawJson[index]
                if (escaped) {
                    when (char) {
                        'n' -> sb.append('\n')
                        't' -> sb.append('\t')
                        'r' -> sb.append('\r')
                        '\\' -> sb.append('\\')
                        '"' -> sb.append('"')
                        'u' -> {
                            // Read 4 hex characters
                            if (index + 4 < rawJson.length) {
                                val hex = rawJson.substring(index + 1, index + 5)
                                try {
                                    val codePoint = hex.toInt(16)
                                    sb.append(codePoint.toChar())
                                } catch (e: Exception) {
                                    sb.append("\\u").append(hex)
                                }
                                index += 4
                            } else {
                                sb.append("\\u")
                            }
                        }
                        else -> sb.append(char)
                    }
                    escaped = false
                } else if (char == '\\') {
                    escaped = true
                } else if (char == '"') {
                    break // end of string literal
                } else {
                    sb.append(char)
                }
                index++
            }
            sb.toString()
        } catch (e: Exception) {
            "Parsing error."
        }
    }
}
