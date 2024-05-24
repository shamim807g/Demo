package com.lengo.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface GoogleTTSAPI {
    @POST("v1beta1/text:synthesize?key=AIzaSyD1MFFu57cVxgWXwvNFp1zqoGp59Mc0wtc")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getTextSynthesize(@Body body: JsonObject): JsonObject

    @GET("v1/voices?key=AIzaSyD1MFFu57cVxgWXwvNFp1zqoGp59Mc0wtc")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getVoicesAvailable(): JsonObject

}