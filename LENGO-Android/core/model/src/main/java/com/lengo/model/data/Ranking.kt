package com.lengo.model.data

data class Ranking(
    val countryImage: Int,
    val name: String,
    val level: String,
    val points: Int,
    val rank: Int,
    val isCurrentUser: Boolean,
    val sel: String,
    val userId: Int,
    val isPro: Boolean
)