package com.lengo.database.appdatabase

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.lengo.database.appdatabase.doa.DateStatsDoa
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.TransactionRunnerDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.DateStatsEntity
import com.lengo.database.appdatabase.model.LanguageEntity
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.appdatabase.model.UserEntity
import com.lengo.database.converter.LengoTypeConverter

@Database(
    entities = [PacksEntity::class, LectionsEntity::class,
        ObjectEntity::class,
        UserEntity::class, LanguageEntity::class, DateStatsEntity::class],
    version = 5,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3,
            spec = LengoDatabase.LengoDatabaseAutoMigration::class)
    ],
    exportSchema = true
)
@TypeConverters(value = [LengoTypeConverter::class])
abstract class LengoDatabase : RoomDatabase() {

    @DeleteColumn.Entries(
        DeleteColumn(tableName = "user", columnName = "primary_device"),
        DeleteColumn(tableName = "user", columnName = "primary_os"),
    )
    class LengoDatabaseAutoMigration: AutoMigrationSpec {   }

    abstract fun packsDoa(): PacksDao
    abstract fun userDoa(): UserDoa
    abstract fun dateStatsDoa(): DateStatsDoa
    abstract fun transactionRunnerDao(): TransactionRunnerDao

    companion object {
        val DATABASE_NAME: String = "lengo_db"
    }

}