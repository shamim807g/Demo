package com.lengo.network.model

import com.squareup.moshi.Json

data class PackPublic(
    @Json(name = "func")
    val func: String,
    @Json(name = "owner")
    val owner: Int,
    @Json(name = "pck")
    val pck: Int,
    @Json(name = "public")
    val public: Boolean
)