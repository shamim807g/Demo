package com.lengo.common.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.lengo.model.data.Lang

val LocalDarkModeEnable = compositionLocalOf<Boolean> { error("No active user found!") }

private val DarkColorPalette = darkColors(
    primary = primaryColor,
    primaryVariant = ButtonBlue,
    secondaryVariant = DarkGrey,
    secondary = Grey,
    background = Color.Black,
    surface = DarkSurface,
    onSurface = Color.Black,
    onBackground = Color.White,
    error = Red,
)

private val LightColorPalette = lightColors(
    primary = primaryColor,
    primaryVariant = ButtonBlue,
    secondary = Grey,
    secondaryVariant = LightGrey2,
    background = Color.White,
    surface = lightGrey,
    onSurface = Color.White,
    onBackground = Color.Black,
    error = Red,
)

@Composable
fun LENGOTheme(
    userSelectedLNG: Lang? = null,
    darkTheme: Boolean = true, content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    Theme(
        colors = if (userSelectedLNG != null) colors
            .copy(
                primary = userSelectedLNG.colors.primary,
                primaryVariant = userSelectedLNG.colors.secondary
            ) else colors,
        content = content
    )
}

@Composable
fun Theme(
    colors: Colors = MaterialTheme.colors,
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

}


