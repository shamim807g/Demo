package com.lengo.model.data

import androidx.compose.runtime.Stable

@Stable
data class UserEditedPack(
    val pack: Pack,
    val userCount: Int = 0,
    val totalCount: Int = 0
)