package com.lengo.network.model

import com.squareup.moshi.Json

data class RegisterResponse(
    @Json(name = "activity_id")
    val activityId: Int?,
    @Json(name = "error")
    val error: Any?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "updated")
    val updated: Updated?,
    @Json(name = "userid")
    val userid: Int?,
    @Json(name = "locale_msg_key")
    val locale_msg_key: String?,
) {
    data class Updated(
        @Json(name = "date_stats")
        val dateStats: Boolean?,
        @Json(name = "int_values")
        val intValues: Boolean?,
        @Json(name = "loaded_packs")
        val loadedPacks: Boolean?,
        @Json(name = "settings")
        val settings: Boolean?,
        @Json(name = "subscriptions")
        val subscriptions: Boolean?
    )
}