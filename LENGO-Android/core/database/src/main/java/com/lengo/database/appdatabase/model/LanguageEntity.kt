package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language")
data class LanguageEntity(
    @PrimaryKey
    @ColumnInfo(name = "tkn")
    val tkn: String,
    @ColumnInfo(name = "accent")
    val accent: String,
    @ColumnInfo(name = "firstColor")
    val firstColor: String,
    @ColumnInfo(name = "IOS_appid")
    val IOS_appid: String,
    @ColumnInfo(name = "IOS_bundleid")
    val IOS_bundleid: String,
    @ColumnInfo(name = "iso639")
    val iso639: String,
    @ColumnInfo(name = "iso639_3")
    val iso639_3: String,
    @ColumnInfo(name = "secondColor")
    val secondColor: String,
    )