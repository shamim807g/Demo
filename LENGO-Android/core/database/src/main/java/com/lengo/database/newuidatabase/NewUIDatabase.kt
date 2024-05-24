package com.lengo.database.newuidatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lengo.database.converter.LengoTypeConverter
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity


@Database(
    entities = [PacksUIEntity::class, LectionUIEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(value = [LengoTypeConverter::class])
abstract class NewUIDatabase : RoomDatabase() {

    abstract fun uiPackLecDoa(): UIPackLecDoa

    companion object {
        val DATABASE_NAME: String = "lengo_ui_db"
    }

}