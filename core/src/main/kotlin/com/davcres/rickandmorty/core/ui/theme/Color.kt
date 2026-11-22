package com.davcres.rickandmorty.core.ui.theme

import androidx.compose.ui.graphics.Color

object Color {
    object Common {
        val white = Color(0xFFFFFFFF)
    }

    object Button {
        val ButtonPrimary = Color(0xFF00FF29)
        val ButtonPrimaryText = Color(0xFF003806)
        val ButtonPrimaryDisabled = Color(0xFFE2E8F0)
        val ButtonPrimaryDisabledText = Color(0xFF94A3B8)

        val ButtonSecondary = Color(0xFF00B5CC)
        val ButtonSecondaryDisabled = Color(0xFF94A3B8)

        val ButtonTertiary = Color(0xFF00B5CC)
        val ButtonTertiaryDisabled = Color(0xFF94A3B8)

        val ButtonTogglePrimarySelected = Color(0xFF00FF29)
        val ButtonTogglePrimarySelectedDisabled = Color(0xFFE2E8F0)
        val ButtonTogglePrimarySelectedDisabledText = Color(0xFF94A3B8)
        val ButtonTogglePrimaryUnselected = Color(0xFF00FF29)
        val ButtonTogglePrimaryUnselectedDisabled = Color(0xFF94A3B8)

        val ButtonToggleSecondarySelected = Color(0xFF00B5CC)
        val ButtonToggleSecondarySelectedDisabled = Color(0xFFE2E8F0)
        val ButtonToggleSecondarySelectedDisabledText = Color(0xFF94A3B8)
        val ButtonToggleSecondaryUnselected = Color(0xFF00B5CC)
        val ButtonToggleSecondaryUnselectedDisabled = Color(0xFF94A3B8)
    }
}

/**
 * Colores representativos para el estado de los personajes (Alive, Dead, unknown).
 */
object StatusColors {
    val Alive = Color(0xFF00E676)
    val Dead = Color(0xFFFF5252)
    val Unknown = Color(0xFF9E9E9E)
}

/**
 * Colores de marca basados en el universo de Rick and Morty:
 * verde neón portal (#00FF29), azul portal/Rick (#00B5CC) y acento dorado Morty (#F2C94C).
 */
object BrandColors {
    // Verde neón portal (#00FF29) para acentos, detalles, lupa y bordes activos
    val Primary = Color(0xFF00FF29)
    val OnPrimary = Color(0xFF003806)

    // Contenedores primarios
    val PrimaryContainer = Color(0xFFCEFAD1)
    val OnPrimaryContainer = Color(0xFF003905)
    val PrimaryContainerDark = Color(0xFF0D2E14)
    val OnPrimaryContainerDark = Color(0xFF57FF73)

    // Secundario: Azul portal / Rick's sci-fi cyan (#00B5CC)
    val Secondary = Color(0xFF00B5CC)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFC7F5FA)
    val OnSecondaryContainer = Color(0xFF00363E)
    val SecondaryContainerDark = Color(0xFF004F59)
    val OnSecondaryContainerDark = Color(0xFF70E6F6)

    // Terciario: Amarillo Morty / Portal gold (#F2C94C)
    val Tertiary = Color(0xFFF2C94C)
    val OnTertiary = Color(0xFF382A00)
    val TertiaryContainer = Color(0xFFFFEAA5)
    val OnTertiaryContainer = Color(0xFF382A00)
    val TertiaryContainerDark = Color(0xFF4E3C00)
    val OnTertiaryContainerDark = Color(0xFFFFE27D)

    // Errores
    val Error = Color(0xFFFF5252)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF410002)
    val ErrorContainerDark = Color(0xFF4B1113)
    val OnErrorContainerDark = Color(0xFFFFB4AB)

    // Neutrales / Outline
    val OutlineLight = Color(0xFFCBD5E1)
    val OutlineDark = Color(0xFF38404E)
    val NeutralVariant = Color(0xFF64748B)
}

/**
 * Colores de fondo y superficie inspirados en el diseño cósmico de Rick & Morty.
 */
object SurfaceColors {
    // Modo claro: gris moderno, fresco y limpio
    val BackgroundLight = Color(0xFFF4F6F9)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceVariantLight = Color(0xFFE8ECF2)
    val OnBackgroundLight = Color(0xFF111827)
    val OnSurfaceLight = Color(0xFF1A202C)
    val OnSurfaceVariantLight = Color(0xFF5A6577)

    // Modo oscuro: gris pizarra cósmico (estilo web oficial Rick & Morty API #24282F)
    val BackgroundDark = Color(0xFF14171C)
    val SurfaceDark = Color(0xFF1E232B)
    val SurfaceVariantDark = Color(0xFF272D37)
    val OnBackgroundDark = Color(0xFFF0F3F6)
    val OnSurfaceDark = Color(0xFFE6E9EE)
    val OnSurfaceVariantDark = Color(0xFF9DA6B5)
}
