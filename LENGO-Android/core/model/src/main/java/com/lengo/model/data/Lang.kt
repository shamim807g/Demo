package com.lengo.model.data

import androidx.compose.runtime.Immutable
import java.util.*

@Immutable
data class Lang(
    var locale: Locale,
    val code: String,
    val iso639_3: String,
    val drawable: Int,
    val colors: LNGColor,
    val accent: String
)

@Immutable
data class DeviceLang(
    var locale: Locale,
    val code: String,
    val drawable: Int
)





