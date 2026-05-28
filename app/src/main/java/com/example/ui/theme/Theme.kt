package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
      primary = PrimaryEmerald,
      onPrimary = Color.White,
      secondary = SoftEmerald,
      onSecondary = Color(0xFF001D36),
      tertiary = AccentTeal,
      background = Color(0xFF121418), // Dark Mode Minimal deep slate bg
      surface = Color(0xFF1A1C1E),    // Dark Mode Minimal card surf
      onBackground = Color(0xFFE2E2E6),
      onSurface = Color(0xFFE2E2E6),
      error = ErrorCrimson
  )

private val LightColorScheme =
  lightColorScheme(
      primary = PrimaryEmerald,       // Brand blue #0061A4
      onPrimary = Color.White,        // Crisp white text on primary button
      secondary = SoftEmerald,        // Light blue background container #D1E4FF
      onSecondary = Color(0xFF001D36), // Deep blue label contrast #001D36
      tertiary = AccentTeal,
      background = DeepNavyBg,        // Canvas light off-white #F7F9FC
      surface = CardSlate,            // Pristine white cards #FFFFFF
      onBackground = LightText,       // Charcoal text #1A1C1E
      onSurface = LightText,          // Charcoal text #1A1C1E
      error = ErrorCrimson,
      surfaceVariant = Color(0xFFE1E2E9),
      onSurfaceVariant = Color(0xFF44474E)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default so our custom Clean Minimalism palette is shown perfectly
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
