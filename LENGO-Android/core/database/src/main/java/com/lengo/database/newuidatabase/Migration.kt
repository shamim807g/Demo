package com.lengo.database.newuidatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val UI_MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packs_ui ADD COLUMN version INTEGER NOT NULL DEFAULT -1")
    }
}

val UI_MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packs_ui ADD COLUMN subscribed INTEGER NOT NULL DEFAULT 0")
    }
}

val UI_MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
                CREATE TABLE IF NOT EXISTS lections_ui_new ( 
                type TEXT NOT NULL,
                pck INTEGER NOT NULL,
                owner INTEGER NOT NULL,
                lec INTEGER NOT NULL,
                lec_title TEXT NOT NULL,
                lec_nameMap TEXT NOT NULL,
                lang TEXT NOT NULL,
                example TEXT,
                explanation TEXT,
                errorDrawable INTEGER NOT NULL,
                lec_image TEXT NOT NULL DEFAULT 'placeholder',
                PRIMARY KEY(lec, pck, owner, type, lang))
                """.trimIndent())

        database.execSQL("INSERT INTO lections_ui_new (type, pck, owner,lec, lec_title, lec_nameMap, lang, example, explanation, errorDrawable, lec_image) SELECT type, pck, owner,lec, lec_title, lec_nameMap, lang, example, explanation, errorDrawable, lec_image FROM lections_ui")
        database.execSQL("drop table lections_ui")
        database.execSQL("ALTER TABLE lections_ui_new RENAME TO lections_ui")

    }
}