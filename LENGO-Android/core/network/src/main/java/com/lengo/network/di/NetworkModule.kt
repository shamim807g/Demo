package com.lengo.network.di

import android.content.Context
import android.content.res.AssetManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.lengo.common.BASE_URL
import com.lengo.network.ApiService
import com.lengo.network.BasicAuthInterceptor
import com.lengo.network.BuildConfig
import com.lengo.network.GoogleTTSAPI
import com.lengo.network.MoshiFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                },
        )
        .build()

    @Named("httpClient")
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        return OkHttpClient.Builder()
            //.cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor(
                if (BuildConfig.DEBUG) {
                    BasicAuthInterceptor("Pierino", "!5TuSU<")
                } else {
                    BasicAuthInterceptor("DaVinci", "P#>\$.FkysY=2^A>G")
                }
            )
            .addInterceptor(logging)
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideAssetManager(
        @ApplicationContext context: Context
    ): AssetManager {
        return context.assets
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("httpClient") okHttpClient: OkHttpClient,
        moshi: Moshi,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleTTSRetrofit(
        @Named("defaultClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): GoogleTTSAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://texttospeech.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(GoogleTTSAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return MoshiFactory.create()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Named("defaultClient")
    @Provides
    @Singleton
    fun provideDefaultOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }
}