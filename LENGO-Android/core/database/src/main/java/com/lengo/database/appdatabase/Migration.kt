package com.lengo.database.appdatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

 val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE object ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE lections ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE packs ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
        database.execSQL("CREATE TABLE IF NOT EXISTS DateStats (`date` TEXT NOT NULL, `edited_gram` INTEGER NOT NULL, `edited_vocab` INTEGER NOT NULL, `lng` TEXT NOT NULL, `pushed` INTEGER NOT NULL, `right_edited_gram` INTEGER NOT NULL, `right_edited_vocab` INTEGER NOT NULL, `seconds` INTEGER NOT NULL, PRIMARY KEY(`date`, `lng`))")
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packs ADD COLUMN version INTEGER NOT NULL DEFAULT -1")
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE object SET iVal_pushed = 1")
        database.execSQL("UPDATE object SET pushed = 1")
        database.execSQL("UPDATE datestats SET pushed = 1")
        database.execSQL("ALTER TABLE lections ADD COLUMN pushed INTEGER NOT NULL DEFAULT 1")
    }
}