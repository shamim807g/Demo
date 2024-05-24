package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.lengo.common.DEFAULT_SEL_LANG

@Entity(tableName = "DateStats",primaryKeys = ["date","lng"])
data class DateStatsEntity(
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "edited_gram")
    val edited_gram: Long = 0L,
    @ColumnInfo(name = "edited_vocab")
    val edited_vocab: Long = 0L,
    @ColumnInfo(name = "lng")
    val lng: String = DEFAULT_SEL_LANG,
    @ColumnInfo(name = "pushed")
    val pushed: Boolean = true,
    @ColumnInfo(name = "right_edited_gram")
    val right_edited_gram: Long = 0L,
    @ColumnInfo(name = "right_edited_vocab")
    val right_edited_vocab: Long = 0L,
    @ColumnInfo(name = "seconds")
    val seconds: Long = 0L,

    )