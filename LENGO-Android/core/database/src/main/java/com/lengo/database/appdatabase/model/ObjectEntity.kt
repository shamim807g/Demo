package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "object", primaryKeys = ["lec", "obj", "pck", "owner", "type","lng"])
data class ObjectEntity(
    @ColumnInfo(name = "iVal")
    val iVal: Int = -1,
    @ColumnInfo(name = "iVal_pushed")
    val iVal_pushed: Boolean = true,
    @ColumnInfo(name = "last_retrieval")
    val last_retrieval: Long = 0,
    @ColumnInfo(name = "lec")
    val lec: Long,
    @ColumnInfo(name = "lng")
    val lng: String,
    @ColumnInfo(name = "obj")
    val obj: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "owner")
    val owner: Long = -1,
    @ColumnInfo(name = "pck")
    val pck: Long,
    @ColumnInfo(name = "pushed")
    val pushed: Boolean = true,
    @ColumnInfo(name = "own")
    val own: Map<String, List<String>>?,
    @ColumnInfo(name = "sel")
    val sel: List<String>?,
    @ColumnInfo(name = "deleted", defaultValue = "0")
    val deleted: Boolean = false,

)