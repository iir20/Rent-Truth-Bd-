package com.example.data

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value.isNullOrBlank()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromTenantDocList(value: List<TenantDoc>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, TenantDoc::class.java)
        val adapter = moshi.adapter<List<TenantDoc>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toTenantDocList(value: String?): List<TenantDoc>? {
        if (value.isNullOrBlank()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, TenantDoc::class.java)
        val adapter = moshi.adapter<List<TenantDoc>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let { UserRole.valueOf(it) }

    @TypeConverter
    fun fromSmartAgreementType(value: SmartAgreementType?): String? = value?.name

    @TypeConverter
    fun toSmartAgreementType(value: String?): SmartAgreementType? = value?.let { SmartAgreementType.valueOf(it) }
}
