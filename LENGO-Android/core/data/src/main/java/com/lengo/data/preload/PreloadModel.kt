package com.lengo.data.preload


import com.lengo.database.jsonDatabase.model.JsonObj
import com.lengo.database.jsonDatabase.model.JsonPack
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.annotation.concurrent.Immutable



@JsonClass(generateAdapter = true)
data class MainModel(
    val error: Any?,
    val msg: String?,
    val prebuild: PackAndObjList
) {
    @JsonClass(generateAdapter = true)
    data class PackAndObjList(
        val metadata: List<JsonPack>,
        val objects: List<JsonObj>
    )
}

@JsonClass(generateAdapter = true)
data class PreloadModel(
    @Json(name = "error")
    val error: Any?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "prebuild")
    val prebuild: Prebuild
) {
    @JsonClass(generateAdapter = true)
    data class Prebuild(
        @Json(name = "metadata")
        val metadata: List<Metadata>,
        @Json(name = "objects")
        val objects: List<Object>
    ) {
        @JsonClass(generateAdapter = true)
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
            val lections: List<Lection>,
            @Json(name = "name")
            val name: Map<String,String>,
            @Json(name = "owner")
            val owner: Long,
            @Json(name = "version")
            val version: Int
        ) {
            @JsonClass(generateAdapter = true)
            data class Lection(
                @Json(name = "id")
                val id: Long,
                @Json(name = "name")
                val name: Map<String,String>,
                @Json(name = "explanation")
                val explanation: Map<String,Map<String,String>>?,
                @Json(name = "examples")
                val examples: Map<String,List<Example>>?,
                @Json(name = "videoLink")
                val videoLink: Map<String,Map<String,String>>?
            )
            @JsonClass(generateAdapter = true)
            data class Example(
                val example: String?,
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
            val value: Map<String,Any>? //Map<String,Any>
        )
    }
}