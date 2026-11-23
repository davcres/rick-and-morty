package com.davcres.rickandmorty.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.davcres.rickandmorty.core.common.models.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = BrandColors.Primary,
    onPrimary = BrandColors.OnPrimary,
    primaryContainer = BrandColors.PrimaryContainer,
    onPrimaryContainer = BrandColors.OnPrimaryContainer,

    secondary = BrandColors.Secondary,
    onSecondary = BrandColors.OnSecondary,
    secondaryContainer = BrandColors.SecondaryContainer,
    onSecondaryContainer = BrandColors.OnSecondaryContainer,

    tertiary = BrandColors.Tertiary,
    onTertiary = BrandColors.OnTertiary,
    tertiaryContainer = BrandColors.TertiaryContainer,
    onTertiaryContainer = BrandColors.OnTertiaryContainer,

    error = BrandColors.Error,
    onError = BrandColors.OnError,
    errorContainer = BrandColors.ErrorContainer,
    onErrorContainer = BrandColors.OnErrorContainer,

    background = SurfaceColors.BackgroundLight,
    onBackground = SurfaceColors.OnBackgroundLight,
    surface = SurfaceColors.SurfaceLight,
    onSurface = SurfaceColors.OnSurfaceLight,
    surfaceVariant = SurfaceColors.SurfaceVariantLight,
    onSurfaceVariant = SurfaceColors.OnSurfaceVariantLight,

    outline = BrandColors.OutlineLight
)

/**
 * Paleta oscura correspondiente con estética cósmica / Rick & Morty.
 */
private val DarkColorScheme = darkColorScheme(
    primary = BrandColors.Primary,
    onPrimary = BrandColors.OnPrimary,
    primaryContainer = BrandColors.PrimaryContainerDark,
    onPrimaryContainer = BrandColors.OnPrimaryContainerDark,

    secondary = BrandColors.Secondary,
    onSecondary = BrandColors.OnSecondary,
    secondaryContainer = BrandColors.SecondaryContainerDark,
    onSecondaryContainer = BrandColors.OnSecondaryContainerDark,

    tertiary = BrandColors.Tertiary,
    onTertiary = BrandColors.OnTertiary,
    tertiaryContainer = BrandColors.TertiaryContainerDark,
    onTertiaryContainer = BrandColors.OnTertiaryContainerDark,

    error = BrandColors.Error,
    onError = BrandColors.OnError,
    errorContainer = BrandColors.ErrorContainerDark,
    onErrorContainer = BrandColors.OnErrorContainerDark,

    background = SurfaceColors.BackgroundDark,
    onBackground = SurfaceColors.OnBackgroundDark,
    surface = SurfaceColors.SurfaceDark,
    onSurface = SurfaceColors.OnSurfaceDark,
    surfaceVariant = SurfaceColors.SurfaceVariantDark,
    onSurfaceVariant = SurfaceColors.OnSurfaceVariantDark,

    outline = BrandColors.OutlineDark
)

@Composable
fun RickAndMortyTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
