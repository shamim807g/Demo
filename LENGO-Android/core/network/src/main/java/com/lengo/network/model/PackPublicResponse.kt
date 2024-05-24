package com.lengo.network.model

import com.squareup.moshi.Json

data class PackPublicResponse(
    @Json(name = "accepted")
    val accepted: Boolean,
    @Json(name = "error")
    val error: String?,
    @Json(name = "msg")
    val msg: String
)