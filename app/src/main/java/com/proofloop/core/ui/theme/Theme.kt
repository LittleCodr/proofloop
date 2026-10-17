package com.proofloop.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ProofColorScheme = lightColorScheme(
    primary = ProofCobalt,
    onPrimary = ProofWhite,
    primaryContainer = ProofLavender,
    onPrimaryContainer = ProofInk,
    secondary = ProofAcidLime,
    onSecondary = ProofInk,
    secondaryContainer = ProofSurfaceMuted,
    onSecondaryContainer = ProofInk,
    tertiary = ProofCoral,
    onTertiary = ProofWhite,
    background = ProofPaper,
    onBackground = ProofInk,
    surface = ProofWhite,
    onSurface = ProofInk,
    surfaceVariant = ProofSurfaceMuted,
    onSurfaceVariant = ProofInk,
    outline = ProofBorder,
    error = ProofCoral,
    onError = ProofWhite
)

@Composable
fun ProofLoopTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ProofColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ProofPaper.toArgb()
                window.navigationBarColor = ProofPaper.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ProofTypography,
        content = content
    )
}
