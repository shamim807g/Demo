package com.lengo.network.model

import com.squareup.moshi.Json

data class UserPackResponse(
    @Json(name = "error")
    val error: String?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "packs_json")
    val packs_json: PacksJson,
) {
    data class PacksJson(
        @Json(name = "metadata")
        val metadata: List<Metadata>?,
        @Json(name = "objects")
        val objects: List<Object>
    ) {

        data class Metadata(
            @Json(name = "available_sel_lng")
            val availableSelLng: List<String>,
            @Json(name = "coins")
            val coins: Int,
            @Json(name = "emoji")
            val emoji: String,
            @Json(name = "func")
            val func: String,
            @Json(name = "id")
            val id: Long,
            @Json(name = "lections")
            val lections: List<Lection>?,
            @Json(name = "name")
            val name: Map<String, String>,
            @Json(name = "owner")
            val owner: Long,
            @Json(name = "version")
            val version: Int,
            @Json(name = "submitted")
            val submitted: Boolean,
        ) {
            data class Lection(
                @Json(name = "id")
                val id: Long,
                @Json(name = "name")
                val name: Map<String, String>,
                @Json(name = "explanation")
                val explanation: Map<String, Map<String, String>>?,
                @Json(name = "examples")
                val examples: Map<String, List<Example>>?,
                @Json(name = "videoLink")
                val videoLink: Map<String, Map<String, String>>?
            )

            data class Example(
                val example: String?,
                val id: Long?
            )
        }

        data class Object(
            @Json(name = "func")
            val func: String,
            @Json(name = "lec")
            val lec: Long,
            @Json(name = "obj")
            val obj: Long,
            @Json(name = "owner")
            val owner: Long,
            @Json(name = "pck")
            val pck: Long,
            @Json(name = "value")
            val value: Map<String, Any>? //Map<String,Any>
        )
    }
}