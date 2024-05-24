package com.lengo.database.newuidatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.lengo.model.data.BADGE


@Entity(tableName = "packs_ui",primaryKeys = ["pck", "type","owner", "lang"])
data class PacksUIEntity(
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "pck")
    val pck: Long,
    @ColumnInfo(name = "owner")
    val owner: Long,
    @ColumnInfo(name = "pack_title")
    val pack_title: String,
    @ColumnInfo(name = "packNameMap")
    val packNameMap: Map<String,String>,
    @ColumnInfo(name = "coins")
    val coins: Long,
    @ColumnInfo(name = "emoji")
    val emoji: String,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "badge")
    val badge: BADGE,
    @ColumnInfo(name = "version", defaultValue = "-1")
    val version: Long,
    @ColumnInfo(name = "subscribed", defaultValue = "0")
    val subscribed: Long,
)

@Entity(tableName = "lections_ui",primaryKeys = ["lec", "pck", "owner", "type", "lang"])
data class LectionUIEntity(
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "pck")
    val pck: Long,
    @ColumnInfo(name = "owner")
    val owner: Long,
    @ColumnInfo(name = "lec")
    val lec: Long,
    @ColumnInfo(name = "lec_title")
    val lec_title: String,
    @ColumnInfo(name = "lec_nameMap")
    val lec_nameMap: Map<String,String>,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "example")
    val example: List<String>? = null,
    @ColumnInfo(name = "explanation")
    val explanation: Map<String,String>? = null,
    @ColumnInfo(name = "errorDrawable")
    val errorDrawable: Int,
    @ColumnInfo(name = "lec_image", defaultValue = "'placeholder'")
    val lec_image: String = "placeholder",
)





