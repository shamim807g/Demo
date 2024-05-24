package com.lengo.model.data

import androidx.compose.runtime.Immutable

@Immutable
data class ObjParam(
    val objId: Long,
    val pck: Long,
    val lec: Long,
    val type: String,
    val owner: Long
)