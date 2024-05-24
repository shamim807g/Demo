package com.lengo.model.data

import javax.annotation.concurrent.Immutable

@Immutable
data class OnboardingFlags(
    val isLangSheetShown: Boolean,
    val isSubSheetShown: Boolean
)