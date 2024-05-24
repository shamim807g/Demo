package com.lengo.network.model

import com.squareup.moshi.Json

data class UpdateUserResponse(
    @Json(name = "activity_id")
    val activityId: Int?,
    @Json(name = "error")
    val error: Any?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "userid")
    val userid: Int?,
)