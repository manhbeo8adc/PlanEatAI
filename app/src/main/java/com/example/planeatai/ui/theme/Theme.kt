package com.example.planeatai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import com.example.planeatai.R

val Pink50 = Color(0xFFFDF2F8)
val Pink100 = Color(0xFFFCE7F3)
val Pink200 = Color(0xFFFBCFE8)
val Pink300 = Color(0xFFF9A8D4)
val Pink400 = Color(0xFFF472B6)
val Pink500 = Color(0xFFEC4899)
val Pink600 = Color(0xFFDB2777)
val Pink700 = Color(0xFFBE185D)
val Pink800 = Color(0xFF9D174D)
val Pink900 = Color(0xFF831843)

private val LightColorScheme = lightColorScheme(
    primary = Pink400,
    onPrimary = Color.White,
    primaryContainer = Pink50,
    onPrimaryContainer = Pink900,
    secondary = Pink200,
    onSecondary = Pink900,
    background = Pink50,
    surface = Color.White,
    surfaceVariant = Pink100
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E9),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D)
)

val Quicksand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Quicksand),
    displayMedium = TextStyle(fontFamily = Quicksand),
    displaySmall = TextStyle(fontFamily = Quicksand),
    headlineLarge = TextStyle(fontFamily = Quicksand),
    headlineMedium = TextStyle(fontFamily = Quicksand),
    headlineSmall = TextStyle(fontFamily = Quicksand),
    titleLarge = TextStyle(fontFamily = Quicksand),
    titleMedium = TextStyle(fontFamily = Quicksand),
    titleSmall = TextStyle(fontFamily = Quicksand),
    bodyLarge = TextStyle(fontFamily = Quicksand),
    bodyMedium = TextStyle(fontFamily = Quicksand),
    bodySmall = TextStyle(fontFamily = Quicksand),
    labelLarge = TextStyle(fontFamily = Quicksand),
    labelMedium = TextStyle(fontFamily = Quicksand),
    labelSmall = TextStyle(fontFamily = Quicksand)
)

@Composable
fun PlanEatAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}