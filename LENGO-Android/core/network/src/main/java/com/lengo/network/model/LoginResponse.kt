package com.lengo.network.model


import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "activity_id")
    val activity_id: Long?,
    @Json(name = "coins")
    val coins: Int?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "error")
    val error: String?,
    @Json(name = "highscore")
    val highscore: Int?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "points")
    val points: Int?,
    @Json(name = "userdata")
    val userdata: Userdata?,
    @Json(name = "userid")
    val userid: Int?,
    @Json(name = "own_lng")
    val own_lng: String?,
    @Json(name = "sel_lng")
    val sel_lng: String?,
    @Json(name = "locale_msg_key")
    val locale_msg_key: String?,
) {
    data class Userdata(
        @Json(name = "date_stats")
        val date_stats: List<DateState>?,
        @Json(name = "int_values")
        val int_values: List<IntValues>?,
        @Json(name = "loaded_packs")
        val loaded_packs: List<LoadedPacks>?,
        @Json(name = "settings")
        val settings: Settings?,
        @Json(name = "subscriptions")
        val subscriptions: List<Subscription?>?
    ) {
        data class Settings(
            @Json(name = "audio")
            val audio: Boolean?,
            @Json(name = "automaticallycontinue")
            val automaticallycontinue: Boolean?,
            @Json(name = "games")
            val games: Boolean?,
            @Json(name = "listening")
            val listening: Boolean?,
            @Json(name = "memorize")
            val memorize: Boolean?,
            @Json(name = "pushlearning")
            val pushlearning: Boolean?,
            @Json(name = "quiz")
            val quiz: Boolean?,
            @Json(name = "speaking")
            val speaking: Boolean?,
            @Json(name = "test")
            val test: Boolean?,
            @Json(name = "voice")
            val voice: Boolean?
        )

        data class Subscription(
            @Json(name = "active")
            val active: Boolean?,
            @Json(name = "last_verification")
            val lastVerification: String?,
            @Json(name = "product_id")
            val productId: String?,
            @Json(name = "valid_till")
            val validTill: String?
        )

        data class LoadedPacks(
            @Json(name = "func")
            val func: String?,
            @Json(name = "lastretrieval")
            val lastretrieval: Long?,
            @Json(name = "lng")
            val lng: String?,
            @Json(name = "owner")
            val owner: Int?,
            @Json(name = "pck")
            val pck: Int?,
            @Json(name = "subscribed")
            val subscribed: Boolean?,
        )

        data class IntValues(
            @Json(name = "func")
            val func: String?,
            @Json(name = "intvalue")
            val intvalue: Int?,
            @Json(name = "lastretrieval")
            val lastretrieval: Long?,
            @Json(name = "lec")
            val lec: Int?,
            @Json(name = "lng")
            val lng: String?,
            @Json(name = "obj")
            val obj: Int?,
            @Json(name = "owner")
            val owner: Int?,
            @Json(name = "pck")
            val pck: Int?
        )

        data class DateState(
            @Json(name = "date")
            val date: String?,
            @Json(name = "edited_gram")
            val edited_gram: Int?,
            @Json(name = "edited_vocab")
            val edited_vocab: Int?,
            @Json(name = "right_edited_gram")
            val right_edited_gram: Int?,
            @Json(name = "right_edited_vocab")
            val right_edited_vocab: Int?,
            @Json(name = "seconds")
            val seconds: Int?,
            @Json(name = "lng")
            val lng: String?
        )
    }
}