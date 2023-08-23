package com.etb.filemanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.style.StyleManager


private val LightGreenColors = lightColorScheme(
    primary = md_theme_green_light_primary,
    onPrimary = md_theme_green_light_onPrimary,
    primaryContainer = md_theme_green_light_primaryContainer,
    onPrimaryContainer = md_theme_green_light_onPrimaryContainer,
    secondary = md_theme_green_light_secondary,
    onSecondary = md_theme_green_light_onSecondary,
    secondaryContainer = md_theme_green_light_secondaryContainer,
    onSecondaryContainer = md_theme_green_light_onSecondaryContainer,
    tertiary = md_theme_green_light_tertiary,
    onTertiary = md_theme_green_light_onTertiary,
    tertiaryContainer = md_theme_green_light_tertiaryContainer,
    onTertiaryContainer = md_theme_green_light_onTertiaryContainer,
    error = md_theme_green_light_error,
    errorContainer = md_theme_green_light_errorContainer,
    onError = md_theme_green_light_onError,
    onErrorContainer = md_theme_green_light_onErrorContainer,
    background = md_theme_green_light_background,
    onBackground = md_theme_green_light_onBackground,
    surface = md_theme_green_light_surface,
    onSurface = md_theme_green_light_onSurface,
    surfaceVariant = md_theme_green_light_surfaceVariant,
    onSurfaceVariant = md_theme_green_light_onSurfaceVariant,
    outline = md_theme_green_light_outline,
    inverseOnSurface = md_theme_green_light_inverseOnSurface,
    inverseSurface = md_theme_green_light_inverseSurface,
    inversePrimary = md_theme_green_light_inversePrimary,
    surfaceTint = md_theme_green_light_surfaceTint,
    outlineVariant = md_theme_green_light_outlineVariant,
    scrim = md_theme_green_light_scrim,
)

private val DarkGreenColors = darkColorScheme(
    primary = md_theme_green_dark_primary,
    onPrimary = md_theme_green_dark_onPrimary,
    primaryContainer = md_theme_green_dark_primaryContainer,
    onPrimaryContainer = md_theme_green_dark_onPrimaryContainer,
    secondary = md_theme_green_dark_secondary,
    onSecondary = md_theme_green_dark_onSecondary,
    secondaryContainer = md_theme_green_dark_secondaryContainer,
    onSecondaryContainer = md_theme_green_dark_onSecondaryContainer,
    tertiary = md_theme_green_dark_tertiary,
    onTertiary = md_theme_green_dark_onTertiary,
    tertiaryContainer = md_theme_green_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_green_dark_onTertiaryContainer,
    error = md_theme_green_dark_error,
    errorContainer = md_theme_green_dark_errorContainer,
    onError = md_theme_green_dark_onError,
    onErrorContainer = md_theme_green_dark_onErrorContainer,
    background = md_theme_green_dark_background,
    onBackground = md_theme_green_dark_onBackground,
    surface = md_theme_green_dark_surface,
    onSurface = md_theme_green_dark_onSurface,
    surfaceVariant = md_theme_green_dark_surfaceVariant,
    onSurfaceVariant = md_theme_green_dark_onSurfaceVariant,
    outline = md_theme_green_dark_outline,
    inverseOnSurface = md_theme_green_dark_inverseOnSurface,
    inverseSurface = md_theme_green_dark_inverseSurface,
    inversePrimary = md_theme_green_dark_inversePrimary,
    surfaceTint = md_theme_green_dark_surfaceTint,
    outlineVariant = md_theme_green_dark_outlineVariant,
    scrim = md_theme_green_dark_scrim,
)

private val LightBlueColors = lightColorScheme(
    primary = md_theme_blue_light_primary,
    onPrimary = md_theme_blue_light_onPrimary,
    primaryContainer = md_theme_blue_light_primaryContainer,
    onPrimaryContainer = md_theme_blue_light_onPrimaryContainer,
    secondary = md_theme_blue_light_secondary,
    onSecondary = md_theme_blue_light_onSecondary,
    secondaryContainer = md_theme_blue_light_secondaryContainer,
    onSecondaryContainer = md_theme_blue_light_onSecondaryContainer,
    tertiary = md_theme_blue_light_tertiary,
    onTertiary = md_theme_blue_light_onTertiary,
    tertiaryContainer = md_theme_blue_light_tertiaryContainer,
    onTertiaryContainer = md_theme_blue_light_onTertiaryContainer,
    error = md_theme_blue_light_error,
    errorContainer = md_theme_blue_light_errorContainer,
    onError = md_theme_blue_light_onError,
    onErrorContainer = md_theme_blue_light_onErrorContainer,
    background = md_theme_blue_light_background,
    onBackground = md_theme_blue_light_onBackground,
    surface = md_theme_blue_light_surface,
    onSurface = md_theme_blue_light_onSurface,
    surfaceVariant = md_theme_blue_light_surfaceVariant,
    onSurfaceVariant = md_theme_blue_light_onSurfaceVariant,
    outline = md_theme_blue_light_outline,
    inverseOnSurface = md_theme_blue_light_inverseOnSurface,
    inverseSurface = md_theme_blue_light_inverseSurface,
    inversePrimary = md_theme_blue_light_inversePrimary,
    surfaceTint = md_theme_blue_light_surfaceTint,
    outlineVariant = md_theme_blue_light_outlineVariant,
    scrim = md_theme_blue_light_scrim,
)

private val DarkBlueColors = darkColorScheme(
    primary = md_theme_blue_dark_primary,
    onPrimary = md_theme_blue_dark_onPrimary,
    primaryContainer = md_theme_blue_dark_primaryContainer,
    onPrimaryContainer = md_theme_blue_dark_onPrimaryContainer,
    secondary = md_theme_blue_dark_secondary,
    onSecondary = md_theme_blue_dark_onSecondary,
    secondaryContainer = md_theme_blue_dark_secondaryContainer,
    onSecondaryContainer = md_theme_blue_dark_onSecondaryContainer,
    tertiary = md_theme_blue_dark_tertiary,
    onTertiary = md_theme_blue_dark_onTertiary,
    tertiaryContainer = md_theme_blue_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_blue_dark_onTertiaryContainer,
    error = md_theme_blue_dark_error,
    errorContainer = md_theme_blue_dark_errorContainer,
    onError = md_theme_blue_dark_onError,
    onErrorContainer = md_theme_blue_dark_onErrorContainer,
    background = md_theme_blue_dark_background,
    onBackground = md_theme_blue_dark_onBackground,
    surface = md_theme_blue_dark_surface,
    onSurface = md_theme_blue_dark_onSurface,
    surfaceVariant = md_theme_blue_dark_surfaceVariant,
    onSurfaceVariant = md_theme_blue_dark_onSurfaceVariant,
    outline = md_theme_blue_dark_outline,
    inverseOnSurface = md_theme_blue_dark_inverseOnSurface,
    inverseSurface = md_theme_blue_dark_inverseSurface,
    inversePrimary = md_theme_blue_dark_inversePrimary,
    surfaceTint = md_theme_blue_dark_surfaceTint,
    outlineVariant = md_theme_blue_dark_outlineVariant,
    scrim = md_theme_blue_dark_scrim,
)

private val LightPinkColors = lightColorScheme(
    primary = md_theme_pink_light_primary,
    onPrimary = md_theme_pink_light_onPrimary,
    primaryContainer = md_theme_pink_light_primaryContainer,
    onPrimaryContainer = md_theme_pink_light_onPrimaryContainer,
    secondary = md_theme_pink_light_secondary,
    onSecondary = md_theme_pink_light_onSecondary,
    secondaryContainer = md_theme_pink_light_secondaryContainer,
    onSecondaryContainer = md_theme_pink_light_onSecondaryContainer,
    tertiary = md_theme_pink_light_tertiary,
    onTertiary = md_theme_pink_light_onTertiary,
    tertiaryContainer = md_theme_pink_light_tertiaryContainer,
    onTertiaryContainer = md_theme_pink_light_onTertiaryContainer,
    error = md_theme_pink_light_error,
    errorContainer = md_theme_pink_light_errorContainer,
    onError = md_theme_pink_light_onError,
    onErrorContainer = md_theme_pink_light_onErrorContainer,
    background = md_theme_pink_light_background,
    onBackground = md_theme_pink_light_onBackground,
    surface = md_theme_pink_light_surface,
    onSurface = md_theme_pink_light_onSurface,
    surfaceVariant = md_theme_pink_light_surfaceVariant,
    onSurfaceVariant = md_theme_pink_light_onSurfaceVariant,
    outline = md_theme_pink_light_outline,
    inverseOnSurface = md_theme_pink_light_inverseOnSurface,
    inverseSurface = md_theme_pink_light_inverseSurface,
    inversePrimary = md_theme_pink_light_inversePrimary,
    surfaceTint = md_theme_pink_light_surfaceTint,
    outlineVariant = md_theme_pink_light_outlineVariant,
    scrim = md_theme_pink_light_scrim,
)

private val DarkPinkColors = darkColorScheme(
    primary = md_theme_pink_dark_primary,
    onPrimary = md_theme_pink_dark_onPrimary,
    primaryContainer = md_theme_pink_dark_primaryContainer,
    onPrimaryContainer = md_theme_pink_dark_onPrimaryContainer,
    secondary = md_theme_pink_dark_secondary,
    onSecondary = md_theme_pink_dark_onSecondary,
    secondaryContainer = md_theme_pink_dark_secondaryContainer,
    onSecondaryContainer = md_theme_pink_dark_onSecondaryContainer,
    tertiary = md_theme_pink_dark_tertiary,
    onTertiary = md_theme_pink_dark_onTertiary,
    tertiaryContainer = md_theme_pink_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_pink_dark_onTertiaryContainer,
    error = md_theme_pink_dark_error,
    errorContainer = md_theme_pink_dark_errorContainer,
    onError = md_theme_pink_dark_onError,
    onErrorContainer = md_theme_pink_dark_onErrorContainer,
    background = md_theme_pink_dark_background,
    onBackground = md_theme_pink_dark_onBackground,
    surface = md_theme_pink_dark_surface,
    onSurface = md_theme_pink_dark_onSurface,
    surfaceVariant = md_theme_pink_dark_surfaceVariant,
    onSurfaceVariant = md_theme_pink_dark_onSurfaceVariant,
    outline = md_theme_pink_dark_outline,
    inverseOnSurface = md_theme_pink_dark_inverseOnSurface,
    inverseSurface = md_theme_pink_dark_inverseSurface,
    inversePrimary = md_theme_pink_dark_inversePrimary,
    surfaceTint = md_theme_pink_dark_surfaceTint,
    outlineVariant = md_theme_pink_dark_outlineVariant,
    scrim = md_theme_pink_dark_scrim,
)


private val LightRedColors = lightColorScheme(
    primary = md_theme_red_light_primary,
    onPrimary = md_theme_red_light_onPrimary,
    primaryContainer = md_theme_red_light_primaryContainer,
    onPrimaryContainer = md_theme_red_light_onPrimaryContainer,
    secondary = md_theme_red_light_secondary,
    onSecondary = md_theme_red_light_onSecondary,
    secondaryContainer = md_theme_red_light_secondaryContainer,
    onSecondaryContainer = md_theme_red_light_onSecondaryContainer,
    tertiary = md_theme_red_light_tertiary,
    onTertiary = md_theme_red_light_onTertiary,
    tertiaryContainer = md_theme_red_light_tertiaryContainer,
    onTertiaryContainer = md_theme_red_light_onTertiaryContainer,
    error = md_theme_red_light_error,
    errorContainer = md_theme_red_light_errorContainer,
    onError = md_theme_red_light_onError,
    onErrorContainer = md_theme_red_light_onErrorContainer,
    background = md_theme_red_light_background,
    onBackground = md_theme_red_light_onBackground,
    surface = md_theme_red_light_surface,
    onSurface = md_theme_red_light_onSurface,
    surfaceVariant = md_theme_red_light_surfaceVariant,
    onSurfaceVariant = md_theme_red_light_onSurfaceVariant,
    outline = md_theme_red_light_outline,
    inverseOnSurface = md_theme_red_light_inverseOnSurface,
    inverseSurface = md_theme_red_light_inverseSurface,
    inversePrimary = md_theme_red_light_inversePrimary,
    surfaceTint = md_theme_red_light_surfaceTint,
    outlineVariant = md_theme_red_light_outlineVariant,
    scrim = md_theme_red_light_scrim,
)

private val DarkRedColors = darkColorScheme(
    primary = md_theme_red_dark_primary,
    onPrimary = md_theme_red_dark_onPrimary,
    primaryContainer = md_theme_red_dark_primaryContainer,
    onPrimaryContainer = md_theme_red_dark_onPrimaryContainer,
    secondary = md_theme_red_dark_secondary,
    onSecondary = md_theme_red_dark_onSecondary,
    secondaryContainer = md_theme_red_dark_secondaryContainer,
    onSecondaryContainer = md_theme_red_dark_onSecondaryContainer,
    tertiary = md_theme_red_dark_tertiary,
    onTertiary = md_theme_red_dark_onTertiary,
    tertiaryContainer = md_theme_red_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_red_dark_onTertiaryContainer,
    error = md_theme_red_dark_error,
    errorContainer = md_theme_red_dark_errorContainer,
    onError = md_theme_red_dark_onError,
    onErrorContainer = md_theme_red_dark_onErrorContainer,
    background = md_theme_red_dark_background,
    onBackground = md_theme_red_dark_onBackground,
    surface = md_theme_red_dark_surface,
    onSurface = md_theme_red_dark_onSurface,
    surfaceVariant = md_theme_red_dark_surfaceVariant,
    onSurfaceVariant = md_theme_red_dark_onSurfaceVariant,
    outline = md_theme_red_dark_outline,
    inverseOnSurface = md_theme_red_dark_inverseOnSurface,
    inverseSurface = md_theme_red_dark_inverseSurface,
    inversePrimary = md_theme_red_dark_inversePrimary,
    surfaceTint = md_theme_red_dark_surfaceTint,
    outlineVariant = md_theme_red_dark_outlineVariant,
    scrim = md_theme_red_dark_scrim,
)


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)


@Composable
fun FileManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val optionStyle = StyleManager.OptionStyle.valueOf(Preferences.Appearance.appTheme)
    val followSystem = if (darkTheme) DarkColors else LightColors
    var dynamicColorScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    val view = LocalView.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicColorScheme = followSystem


    val colorScheme = when (optionStyle) {
        StyleManager.OptionStyle.FOLLOW_SYSTEM -> followSystem
        StyleManager.OptionStyle.LIGHT_THEME -> LightColors
        StyleManager.OptionStyle.DARK_THEME -> DarkColors
        StyleManager.OptionStyle.PINK_THEME -> LightPinkColors
        StyleManager.OptionStyle.GREEN_THEME_LIGHT -> LightGreenColors
        StyleManager.OptionStyle.GREEN_THEME_DARK -> DarkGreenColors
        StyleManager.OptionStyle.BLUE_THEME_LIGHT -> LightBlueColors
        StyleManager.OptionStyle.BLUE_THEME_DARK -> DarkBlueColors
        StyleManager.OptionStyle.RED_THEME_LIGHT -> LightRedColors
        StyleManager.OptionStyle.RED_THEME_DARK -> DarkRedColors
        StyleManager.OptionStyle.DYNAMIC_COLORS -> dynamicColorScheme
        StyleManager.OptionStyle.MATERIAL_DESIGN_TWO -> followSystem
    }
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}