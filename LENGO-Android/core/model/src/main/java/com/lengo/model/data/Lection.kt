package com.lengo.model.data

import androidx.compose.runtime.Immutable

val PLACEHOLDER = "placeholder"
@Immutable
data class Lection(
    val type: String,
    val pck: Long,
    val owner: Long,
    val lec: Long,
    val title: String,
    val nameMap: Map<String, String>,
    val lang: String,
    val image: String = PLACEHOLDER,
    val objects: List<Objects> = emptyList(),
    val example: List<String>? = null,
    val explanation: Map<String, String>? = null,
    val errorDrawable: Int = -1,
) {
    override fun toString(): String {
        return "${title}${lang}${lec}${owner}${pck}${type}"
    }
}

data class LectionId(
    val lectionName: String,
    val packId: Long,
    val packName: String,
    val lectionID: Long,
    val owner: Long,
    val type: String,
    val lang: String,
)