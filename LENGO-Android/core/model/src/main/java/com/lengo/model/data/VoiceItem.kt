package com.lengo.model.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.collections.immutable.ImmutableList

@Stable
data class VoiceItem(
    val personName: String,
    val voiceName: String,
    val tags: ImmutableList<Any>,
    val isSelected: MutableState<Boolean> = mutableStateOf(false),
    val langCode: String = ""
)