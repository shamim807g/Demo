package com.lengo.model.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

@Stable
data class BillingInAppItem(
    val sku: String,
    val price: MutableState<String?> = mutableStateOf(null),
    val title: MutableState<String?> = mutableStateOf(null),
    val description: MutableState<String?> = mutableStateOf(null),
    val canPurchase: MutableState<Boolean> = mutableStateOf(true),
)