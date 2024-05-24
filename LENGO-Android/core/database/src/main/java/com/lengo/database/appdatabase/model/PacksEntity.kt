package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "packs",primaryKeys = ["pck","type","owner","lng"])
data class PacksEntity (
    @ColumnInfo(name = "coins")
    val coins: Int,
    @ColumnInfo(name = "editable")
    val editable: Boolean = false, //false
    @ColumnInfo(name = "emoji")
    var emoji: String? = null,
    @ColumnInfo(name = "key_pushed")
    val key_pushed: Boolean = true, //true
    @ColumnInfo(name = "last_retrieval")
    val last_retrieval: Long = 0, //0  when opne or get is clicked
    @ColumnInfo(name = "lng")
    val lng: String, //
    @ColumnInfo(name = "owner")
    var owner: Long,
    @ColumnInfo(name = "pck")
    var pck: Long,
    @ColumnInfo(name = "pushed")
    val pushed: Boolean = true, //true
    @ColumnInfo(name = "submitted")
    val submitted: Boolean = false, //false
    @ColumnInfo(name = "subscribed")
    val subscribed: Boolean = false, //false
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "title")
    var title: Map<String,String>,
    @ColumnInfo(name = "deleted", defaultValue = "0")
    val deleted: Boolean = false,
    @ColumnInfo(name = "version", defaultValue = "-1")
    val version: Int = -1,
    )


