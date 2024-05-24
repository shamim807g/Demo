package com.lengo.common.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val Grey = Color(0xFF8A8A8E)
val lightGrey = Color(0xFFF2F2F7)
val DarkSurface = Color(0xFF232323)
val DarkGrey = Color(0xFF313131)
val LightGrey2 = Color(0xFFCFCECE)
val Red = Color(0xFFE4554F)
val ButtonBlue = Color(0xFF78C5EF)
val Green = Color(0xFF22D94A)
val lightGreen = Color(0xFFB6F3C4)
val Alpha80White = Color(0xFFFFFFFF).copy(alpha = 0.8f)
val Alpha80Black = Color(0xFF000000).copy(alpha = 0.6f)

val CoinSilver = Color(0xFFADB1B2)
val CoinGold = Color(0xFFEFB229)
val CoinBronze = Color(0xFFC3764B)

val Orange = Color(0xFFfecb2e)


val ENPrimary = Color(0xFF5CA0CC)
val ENSecondary = Color(0xFF8BC4E4)

val ESPrimary = Color(0xFFF5912D)
val ESSecondary = Color(0xFFF16863)

val USPrimary = Color(0xFF00679B)
val USSecondary = Color(0xFF00679B)

val DEPrimary = Color(0xFFB03736)
val DESecondary = Color(0xFFC95E5F)

val CNPrimary = Color(0xFFFACF08)
val CNSecondary = Color(0xFFF37A02)

val ITPrimary = Color(0xFF55CF1E)
val ITSecondary = Color(0xFF009F83)

val PTPrimary = Color(0xFFE47878)
val PTSecondary = Color(0xFF5FC48B)

val SEPrimary = Color(0xFF23DCD3)
val SESecondary = Color(0xFFF8EF45)

val PLPrimary = Color(0xFFBC47E7)
val PLSecondary = Color(0xFFFC4C54)

val THPrimary = Color(0xFFB004FF)
val THSecondary = Color(0xFF3772FF)

val ARPrimary = Color(0xFFFFDE82)
val ARSecondary = Color(0xFFCAA74E)

val DAPrimary = Color(0xFF00BEC5)
val DASecondary = Color(0xFF00BEC5)

val ELPrimary = Color(0xFF0097DC)
val ELSecondary = Color(0xFF00E4C0)

val FIPrimary = Color(0xFF5CECBF)
val FISecondary = Color(0xFF00B38A)

val FRPrimary = Color(0xFF79B5BE)
val FRSecondary = Color(0xFF008491)

val JAPrimary = Color(0xFF574240)
val JASecondary = Color(0xFFBFA5A3)

val NLPrimary = Color(0xFF70e1f5)
val NLSecondary = Color(0xFFffd194)

val NOPrimary = Color(0xFF546DFF)
val NOSecondary = Color(0xFF003EC9)

val RUPrimary = Color(0xFF7B7485)
val RUSecondary = Color(0xFFA80003)

val UAPrimary = Color(0xFF606FC4)
val UASecondary = Color(0xFFC94798)

val TRPrimary = Color(0xFF445E8E)
val TRSecondary = Color(0xFF93ABE1)

val primaryColor = ENPrimary

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)


val placeHolderGradient =
    Brush.verticalGradient(listOf(Color(0xFFF2F3F5), Color(0xFFE2E7F2)))

val placeHolderDarkGradient =
    Brush.verticalGradient(listOf(Color(0xFF1e1e1e), Color(0xFF1e1e1e)))

@Composable
fun translucentBarAlpha(): Float = when {
    // We use a more opaque alpha in light theme
    MaterialTheme.colors.isLight -> 0.94f
    else -> 0.94f
}

@Composable
fun thumbColor(): Color = when {
    // We use a more opaque alpha in light theme
    MaterialTheme.colors.isLight -> lightGrey
    else -> Grey
}

@Immutable
data class LNGColor(val primary: Color,val secondary: Color)