package com.lengo.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

object MoshiFactory {
    fun create(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            //.addLast(KotlinJsonAdapterFactory())
            .build()
    }
}