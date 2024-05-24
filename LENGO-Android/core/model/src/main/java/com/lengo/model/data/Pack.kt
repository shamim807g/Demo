package com.lengo.model.data

import androidx.compose.runtime.Immutable

@Immutable
data class PackId(
    val name: String = "",
    val pck: Long,
    val owner: Long,
    val type: String,
    val lang: String
)


@Immutable
data class Pack(
    val pck: Long,
    val owner: Long,
    val title: String,
    val packNameMap: Map<String, String>,
    val type: String,
    val coins: Int,
    val emoji: String,
    val lang: String,
    val version: Int,
    val subscribed: Boolean,
    val submitted: Boolean = false,
    val lections: List<Lection> = emptyList(),
    val badge: BADGE = BADGE.NONE
)