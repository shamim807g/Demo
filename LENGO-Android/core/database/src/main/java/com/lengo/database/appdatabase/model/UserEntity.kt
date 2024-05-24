package com.lengo.database.appdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lengo.common.DEFAULT_SEL_LANG

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "userid")
    var userid: Long,
    @ColumnInfo(name = "activity_id")
    var activity_id: Long? = null,
    @ColumnInfo(name = "email")
    var email: String? = null,
    @ColumnInfo(name = "highscore")
    var highscore: Long = 0,
    @ColumnInfo(name = "name")
    var name: String? = null,
    @ColumnInfo(name = "password")
    var password: String? = null,
    @ColumnInfo(name = "points")
    var points: Long = 0,
    @ColumnInfo(name = "coins")
    var coins: Int = 0,
    @ColumnInfo(name = "pushed")
    var pushed: Boolean = true,
    @ColumnInfo(name = "region_code")
    var regionCode: String? = null,
    @ColumnInfo(name = "sel")
    var sel: String = DEFAULT_SEL_LANG,
    @ColumnInfo(name = "own")
    var own: String = "en",
)