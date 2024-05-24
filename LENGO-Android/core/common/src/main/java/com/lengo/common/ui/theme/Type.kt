package com.lengo.common.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lengo.common.R

// Set of Material typography styles to start with

val Roboto = FontFamily(
    Font(R.font.roboto_black, FontWeight.W700),
    Font(R.font.roboto_medium, FontWeight.W600),
    Font(R.font.roboto_regular, FontWeight.W400),
)


@Composable
fun LengoSearchField() =
    MaterialTheme.typography.body1.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
        )
    )

@Composable
fun LengoHeading4() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
        )
    )

@Composable
fun LengoHeading5() =
    MaterialTheme.typography.h5.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
        )
    )


@Composable
fun LengoHeadingh6() =
    MaterialTheme.typography.h6.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
        )
    )

@Composable
fun LengoHeading2() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
            fontSize = 28.sp
        )
    )

@Composable
fun LengoBold20() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
            fontSize = 20.sp
        )
    )

@Composable
fun LengoSemiBold18h4() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp
        )
    )

@Composable
fun LengoSemiBold16h4() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
    )

@Composable
fun LengoRegular18h4() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
            fontSize = 18.sp
        )
    )


@Composable
fun LengoOptionButton() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 22.sp
        )
    )

@Composable
fun LengoBold18h4() =
    MaterialTheme.typography.h4.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
            fontSize = 18.sp
        )
    )

@Composable
fun LengoSubHeading() =
    MaterialTheme.typography.h6.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        )
    )

@Composable
fun LengoSubHeading2() =
    MaterialTheme.typography.h5.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        )
    )

@Composable
fun LengoSubHeading3() =
    MaterialTheme.typography.h5.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 19.sp,
        )
    )

@Composable
fun LengoCaption() =
    MaterialTheme.typography.caption.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
        )
    )

@Composable
fun ChipText() =
    MaterialTheme.typography.body2.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
        )
    )

@Composable
fun WordCardText() =
    MaterialTheme.typography.body2.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
        )
    )

@Composable
fun SeeALlText() =
    MaterialTheme.typography.body1.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
            fontSize = 18.sp
        )
    )

@Composable
fun LengoTopBar() =
    MaterialTheme.typography.h6.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
        )
    )

@Composable
fun BodyText() =
    MaterialTheme.typography.body1.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
        )
    )

@Composable
fun LengoNormal14body2() =
    MaterialTheme.typography.body2.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
    )


@Composable
fun LengoButtonText() =
    MaterialTheme.typography.button.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp
        )
    )

@Composable
fun LengoSemiBold16() =
    MaterialTheme.typography.button.merge(
        TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
    )


val Typography = Typography(
    h2 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
    ),
    h1 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h4 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    body1 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),
    caption = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    )
)