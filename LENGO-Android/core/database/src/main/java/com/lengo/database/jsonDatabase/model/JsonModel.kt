package com.lengo.database.jsonDatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Entity(tableName = "json_pack",primaryKeys = ["id","func","owner"])
data class JsonPack (
    @ColumnInfo(name = "available_sel_lng")
    val available_sel_lng: List<String>,
    @ColumnInfo(name = "coins")
    val coins: Int,
    @ColumnInfo(name = "emoji")
    val emoji: String? = null,
    @ColumnInfo(name = "func")
    val func: String,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "lections")
    val lections: List<Lection>,
    @ColumnInfo(name = "name")
    val name: Map<String,String>,
    @ColumnInfo(name = "owner")
    val owner: Long,
    @ColumnInfo(name = "version")
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
@Entity(tableName = "json_obj",primaryKeys = ["func","lec","obj","pck","owner"])
data class JsonObj (
    @ColumnInfo(name = "func")
    val func: String,
    @ColumnInfo(name = "lec")
    val lec: Long,
    @ColumnInfo(name = "obj")
    val obj: Long,
    @ColumnInfo(name = "owner")
    val owner: Long,
    @ColumnInfo(name = "pck")
    val pck: Long,
    @ColumnInfo(name = "value")
    val value: Map<String,Any>?
)