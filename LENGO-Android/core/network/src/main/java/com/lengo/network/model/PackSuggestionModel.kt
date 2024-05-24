package com.lengo.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.annotation.concurrent.Immutable

data class PackSuggestionRequest(@Json(name = "own_lng") val own_lng: String,@Json(name = "sel_lng") val sel_lng: String)

data class ObjSuggestionDownloadRequest(val pack_keys: List<PackKey>)

data class PackKey(
    val func: String?,
    val owner: Long,
    val pck: Long
)

data class ObjSuggestionDownloadResponse(
    val error: Any?,
    val msg: String?,
    val objects: List<PackSuggestionResponse.Prebuild.Object>?
)


data class PackSuggestionResponse(
    @Json(name = "error")
    val error: Any?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "metadata")
    val metadata: List<Prebuild.Metadata>?,
    //val prebuild: Prebuild?
) {
    data class Prebuild(
        @Json(name = "metadata")
        val metadata: List<Metadata>?,
        @Json(name = "objects")
        val objects: List<Object>?
    ) {
        data class Metadata(
            @Json(name = "available_sel_lng")
            val available_sel_lng: List<String>,
            @Json(name = "coins")
            val coins: Int,
            @Json(name = "emoji")
            val emoji: String,
            @Json(name = "func")
            val func: String,
            @Json(name = "id")
            val id: Long,
            @Json(name = "lections")
            val lections: List<Lection>,
            @Json(name = "name")
            val name: Map<String, String>,
            @Json(name = "owner")
            val owner: Long,
            @Json(name = "version")
            val version: Int
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

            @JsonClass(generateAdapter = true)
            data class Example(
                @Json(name = "example")
                val example: String?,
                @Json(name = "id")
                val id: Long?
            )
        }

        @JsonClass(generateAdapter = true)
        @Immutable
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