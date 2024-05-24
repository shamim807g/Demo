package com.lengo.model.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

@Stable
data class Subscription(
    val productId: String,
    val lang: String,
    val monthlyOfferPrice: MutableState<String?> = mutableStateOf(null),
    val monthlyOfferToken: MutableState<String?> = mutableStateOf(null),
    val monthlyFreeTrialOfferToken: MutableState<String?> = mutableStateOf(null),
    val isMonthlyFreeTrailAvailable: MutableState<Boolean?> = mutableStateOf(null),
    val monthlyPriceAmountMicros: MutableState<Long?> = mutableStateOf(null),

    val yearlyOfferPrice: MutableState<String?> = mutableStateOf(null),
    val yearlyOfferToken: MutableState<String?> = mutableStateOf(null),
    val yearlyFreeTrialOfferToken: MutableState<String?> = mutableStateOf(null),
    val isYearlyFreeTrailAvailable: MutableState<Boolean?> = mutableStateOf(null),
    val yearlyPriceAmountMicros: MutableState<Long?> = mutableStateOf(null),
    val title: MutableState<String?> = mutableStateOf(null),
    val description: MutableState<String?> = mutableStateOf(null),
    val subscribed: MutableState<Boolean?> = mutableStateOf(null)
)