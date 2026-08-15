package com.quincaillerie.gestion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    secondary = OrangeDark,
    background = GreyBackground,
    surface = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    error = RedAlert
)

@Composable
fun QuincaillerieAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
