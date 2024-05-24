package com.lengo.database.jsonDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lengo.database.converter.LengoTypeConverter
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import com.lengo.database.jsonDatabase.model.JsonObj
import com.lengo.database.jsonDatabase.model.JsonPack
import kotlinx.coroutines.CoroutineScope
import logcat.logcat

@Database(
    entities = [JsonPack::class, JsonObj::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(value = [LengoTypeConverter::class])
abstract class JsonDatabase : RoomDatabase() {

     abstract fun jsonPacksDoa(): JsonPackDao

    private class JsonDatabaseCallback(
        private val scope: CoroutineScope,
        //private val dataSource: LengoDataSource
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            logcat("JSON DATABSE") {  "CREATED" }
            //populateDb(scope,dataSource)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            logcat("JSON DATABSE") {  "onDestructiveMigration" }
            //populateDb(scope,dataSource)
        }

//        private fun populateDb(scope: CoroutineScope, dataSource: LengoDataSource) {
//            INSTANCE?.let { jsonDB ->
//                scope.launch {
//                    logcat { "populateDb STARTED!!!!" }
//                    val model = dataSource.getAllPacksForJsonDb()
//                    model?.prebuild?.metadata?.let { jsonDB.jsonPacksDoa().insertAll(it) }
//                    model?.prebuild?.objects?.let { jsonDB.jsonPacksDoa().insertAllObj(it) }
//                    logcat { "populateDb ENDED!!!!" }
//                }
//            }
//        }
    }

    companion object {
        val DATABASE_NAME: String = "json_lengo_db"
//        @Volatile
//        private var INSTANCE: JsonDatabase? = null
//        fun getJsonDatabase(
//            context: Context,
//            coroutineScope: CoroutineScope, // 1
//        ): JsonDatabase {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) {
//                return tempInstance
//            }
//            synchronized(this) {
//                val gson = Gson()
//                val roomConverter = LengoTypeConverter(gson)
//                val instance = Room.databaseBuilder(context.applicationContext,
//                    JsonDatabase::class.java,
//                    DATABASE_NAME
//                )
//                    .addTypeConverter(roomConverter)
//                    .addCallback(JsonDatabaseCallback(coroutineScope))
//                    .fallbackToDestructiveMigration()
//                    .createFromAsset("json_lengo_db.db")
//                    .build()
//                coroutineScope.launch {
//                    instance.jsonPacksDoa().count()
//                }
//                INSTANCE = instance
//                return instance
//            }
//        }
//
//        fun getExistingJsonDatabase(
//        ): JsonDatabase? {
//            return INSTANCE
//        }
    }

}










