package com.lengo.network

import com.google.gson.JsonObject
import com.lengo.model.data.network.CouponReply
import com.lengo.model.data.network.NRRemoteConfig
import com.lengo.network.model.ImageMode
import com.lengo.network.model.LoginResponse
import com.lengo.network.model.ObjSuggestionDownloadRequest
import com.lengo.network.model.ObjSuggestionDownloadResponse
import com.lengo.network.model.PackPublic
import com.lengo.network.model.PackPublicResponse
import com.lengo.network.model.PackSuggestionRequest
import com.lengo.network.model.PackSuggestionResponse
import com.lengo.network.model.RegisterResponse
import com.lengo.network.model.TTSRequest
import com.lengo.network.model.TTSResponse
import com.lengo.network.model.TTSVoicesRequest
import com.lengo.network.model.TTSVoicesResponse
import com.lengo.network.model.UpdateUserResponse
import com.lengo.network.model.UserPackResponse
import com.lengo.network.model.request.RankingListRequest
import com.lengo.network.model.request.RankingTableResponse
import com.lengo.network.model.request.UserRankingListRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("utilities/metadata/keytoimg/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getLectionImg(@Body body: JsonObject): ImageMode

    @POST("utilities/objects/keytoimgs/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getTextToImages(@Body body: JsonObject): JsonObject

    @POST("utilities/objects/keytoimg/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getTextToImage(@Body body: JsonObject): JsonObject

    @POST("communication/mail_to_developer/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun mailToDeveloper(@Body body: JsonObject): JsonObject

    @POST("/content/ratings/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun submitRating(@Body body: JsonObject): JsonObject?

    @POST("/session/init/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun initSession(@Body body: JsonObject): NRRemoteConfig?

    @POST("/session/event/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun eventSession(@Body body: JsonObject): JsonObject?

    @POST("/session/referral/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun referralSession(@Body body: JsonObject): JsonObject?

    @POST("/session/pause/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun pauseSession(@Body body: JsonObject): JsonObject?

    @POST("/session/continue/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun continueSession(@Body body: JsonObject): JsonObject?

    @POST("/coupon/validate/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun validateCoupon(@Body body: JsonObject): CouponReply?

    @POST("utilities/metadata/keytoimg/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun createUser(@Body body: JsonObject): ImageMode

    @POST("user/login/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun login(@Body body: JsonObject): LoginResponse

    @POST("user/create/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun registerUser(@Body body: JsonObject): RegisterResponse

    @POST("user/change_details/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun updateUserData(@Body body: JsonObject): UpdateUserResponse

    @POST("user/delete_account/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun deleteUser(@Body body: JsonObject): UpdateUserResponse

    @POST("user/request_userinfo/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun requestUserInfo(@Body body: JsonObject): LoginResponse

    @POST("user/update_userinfo/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun updateUserInfo(@Body body: JsonObject): LoginResponse

    @POST("userdata/userpacks/pull/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun userpacks(@Body body: JsonObject): UserPackResponse

    @POST("userdata/userpacks/push/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun userPushPacks(@Body body: JsonObject): JsonObject?

    @POST("utilities/txttospeech_voices/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getTxtVoices(@Body body: TTSVoicesRequest): TTSVoicesResponse?

    @POST("utilities/txttospeech/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getTTSFile(@Body body: TTSRequest): TTSResponse?


    @POST("userdata/public_pack_suggestions/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getPackSuggestion(@Body body: PackSuggestionRequest): PackSuggestionResponse?

    @POST("userdata/download_objects/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun getDownloadSuggestion(@Body body: ObjSuggestionDownloadRequest): ObjSuggestionDownloadResponse?

    @POST("userdata/public_pack_review/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun updatePackPublicReview(@Body body: PackPublic): PackPublicResponse?

    @POST("userdata/ranking_list/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun rankingList(@Body body: RankingListRequest): JsonObject

    @POST("userdata/user_ranking/v1/")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun userRankingList(@Body body: UserRankingListRequest): JsonObject
}