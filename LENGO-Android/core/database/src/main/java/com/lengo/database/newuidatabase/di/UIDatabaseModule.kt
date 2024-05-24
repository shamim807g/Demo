package com.lengo.database.newuidatabase.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.lengo.common.di.ApplicationScope
import com.lengo.database.converter.LengoTypeConverter
import com.lengo.database.newuidatabase.NewUIDatabase
import com.lengo.database.newuidatabase.UI_MIGRATION_1_2
import com.lengo.database.newuidatabase.UI_MIGRATION_2_3
import com.lengo.database.newuidatabase.UI_MIGRATION_3_4
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UIDatabaseModule {
    @Singleton
    @Provides
    fun provideUIDb(@ApplicationContext context: Context,
                      @ApplicationScope coroutineScope: CoroutineScope,
                      gson: Gson
    ): NewUIDatabase {
        val roomConverter = LengoTypeConverter(gson)
        return Room
            .databaseBuilder(context, NewUIDatabase::class.java, NewUIDatabase.DATABASE_NAME)
            .addMigrations(UI_MIGRATION_1_2)
            .addMigrations(UI_MIGRATION_2_3)
            .addMigrations(UI_MIGRATION_3_4)
            .addTypeConverter(roomConverter)
            .build()
    }

    @Singleton
    @Provides
    fun providePacksLectionDoa(db: NewUIDatabase): UIPackLecDoa {
        return db.uiPackLecDoa()
    }

}