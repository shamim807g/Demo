package com.lengo.database.appdatabase.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.lengo.database.appdatabase.LengoDatabase
import com.lengo.database.converter.LengoTypeConverter
import com.lengo.database.appdatabase.MIGRATION_1_2
import com.lengo.database.appdatabase.MIGRATION_3_4
import com.lengo.database.appdatabase.MIGRATION_4_5
import com.lengo.database.appdatabase.doa.DateStatsDoa
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.TransactionRunnerDao
import com.lengo.database.appdatabase.doa.UserDoa
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context,gson: Gson): LengoDatabase {
        val roomConverter = LengoTypeConverter(gson)
        return Room
            .databaseBuilder(context, LengoDatabase::class.java, LengoDatabase.DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addTypeConverter(roomConverter)
            //.allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun providePacksDoa(db: LengoDatabase): PacksDao {
        return db.packsDoa()
    }

    @Singleton
    @Provides
    fun provideUserDoa(db: LengoDatabase): UserDoa {
        return db.userDoa()
    }

    @Singleton
    @Provides
    fun provideDateStatsDoa(db: LengoDatabase): DateStatsDoa {
        return db.dateStatsDoa()
    }

    @Singleton
    @Provides
    fun provideTransactionDoa(db: LengoDatabase): TransactionRunnerDao {
        return db.transactionRunnerDao()
    }

}