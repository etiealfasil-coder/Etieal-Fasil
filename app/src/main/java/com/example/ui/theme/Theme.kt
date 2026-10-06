package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioGold,
    onPrimary = Color(0xFF1E1300),
    primaryContainer = Color(0xFF452B00),
    onPrimaryContainer = StudioGoldLight,

    secondary = StudioCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = StudioCyanLight,

    tertiary = StudioTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color(0xFFA7F3D0),

    background = StudioDarkBg,
    onBackground = TextPrimaryDark,

    surface = StudioDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = StudioDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,

    error = StudioRed,
    onError = Color.White
)

private val StudioLightColorScheme = lightColorScheme(
    primary = StudioGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),

    secondary = Color(0xFF0891B2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),

    tertiary = StudioTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),

    background = Color(0xFFF8FAFC),
    onBackground = TextPrimaryLight,

    surface = Color.White,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,

    error = StudioRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to studio dark theme for cinema experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> StudioDarkColorScheme
        else -> StudioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
