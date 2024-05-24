package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "lections",primaryKeys = ["lec","pck","owner","type","lng"])
data class LectionsEntity(
    @ColumnInfo(name = "lec")
    val lec: Long,
    @ColumnInfo(name = "lng")
    val lng: String,
    @ColumnInfo(name = "pck")
    val pck: Long,
    @ColumnInfo(name = "owner")
    var owner: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "title")
    var title: Map<String,String>,
    @ColumnInfo(name = "image")
    var image: String? = null,
    @ColumnInfo(name = "pushed")
    val pushed: Boolean = true,
    @ColumnInfo(name = "explanation")
    val explanation: Map<String,String>? = null,
    @ColumnInfo(name = "examples")
    val examples: List<String>? = null,
    @ColumnInfo(name = "deleted", defaultValue = "0")
    val deleted: Boolean = false,
)
