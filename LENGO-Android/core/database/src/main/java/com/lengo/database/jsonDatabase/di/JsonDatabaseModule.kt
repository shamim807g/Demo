package com.lengo.database.jsonDatabase.di


import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.lengo.common.di.ApplicationScope
import com.lengo.database.appdatabase.LengoDatabase
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.converter.LengoTypeConverter
import com.lengo.database.jsonDatabase.JsonDatabase
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonDatabaseModule {
    @Singleton
    @Provides
    fun provideJsonDb(@ApplicationContext context: Context,
                      @ApplicationScope coroutineScope: CoroutineScope,
                      gson: Gson): JsonDatabase {
        val roomConverter = LengoTypeConverter(gson)
        return Room.databaseBuilder(context.applicationContext,
            JsonDatabase::class.java,
            JsonDatabase.DATABASE_NAME
        )
            .addTypeConverter(roomConverter)
            .fallbackToDestructiveMigration()
            .createFromAsset("json_lengo_db.db")
            .build()
    }

    @Singleton
    @Provides
    fun provideJsonPacksDoa(db: JsonDatabase): JsonPackDao {
        return db.jsonPacksDoa()
    }

}