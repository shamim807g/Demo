package com.lengo.model.data

import androidx.compose.runtime.Immutable

@Immutable
data class Achievements(
    val title: Int,
    val progress: Float,
    val total: Int,
    val count: Int,
    val earnPoints: Int
)