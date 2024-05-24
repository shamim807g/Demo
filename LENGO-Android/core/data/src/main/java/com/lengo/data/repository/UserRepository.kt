package com.lengo.data.repository

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.gson.JsonObject
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.di.ApplicationScope
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.datasource.UserJsonDataProvider
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.UserEntity
import com.lengo.model.data.LectionId
import com.lengo.model.data.SettingModel
import com.lengo.model.data.network.NRRemoteConfig
import com.lengo.network.ApiService
import com.lengo.network.model.LoginResponse
import com.lengo.network.model.RegisterResponse
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

enum class LoginEnableStatus {
    LOADING, ENABLE, DISABLE
}

@Singleton
class UserRepository @Inject constructor(
    private val userDoa: UserDoa,
    private val packDoa: PacksDao,
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(LengoDispatchers.Main) val mainDispatcher: CoroutineDispatcher,
    @ApplicationScope val appScope: CoroutineScope,
    private val apiService: ApiService,
    private val lengoDataSource: LengoDataSource,
    private val lengoPreference: LengoPreference,
    private val userJsonDataProvider: UserJsonDataProvider,
    private val textToSpeechSpeaker: TextToSpeechSpeaker,
) {

    var isLoginOrRegisterEnable: MutableStateFlow<LoginEnableStatus> = MutableStateFlow(LoginEnableStatus.LOADING)
    var isLoginOrRegisterComplete: MutableStateFlow<Int> = MutableStateFlow(0)


    val showCoupons: Flow<Boolean> = lengoPreference.getCouponsShown()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)
        .shareIn(appScope, SharingStarted.Lazily, 1)


    val observeSettingModel: SharedFlow<SettingModel> = lengoPreference
        .observeSettingModel()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)
        .shareIn(appScope, SharingStarted.Eagerly, 1)


    val observeHighScore: SharedFlow<Long> = userDoa.observeCurrentHighScore().map { it ?: 0 }
        .flowOn(ioDispatcher)
        .shareIn(appScope, SharingStarted.Lazily, 1)

    val observeUserEntitySelAndDevice: SharedFlow<UserDoa.UserLang> = userDoa.getUserLang()
        .map { it ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG) }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)
        .shareIn(appScope, SharingStarted.Lazily, 1)

    val observeUserData: SharedFlow<UserEntity> =
        userDoa.observeUserData()
            .filterNotNull()
            .distinctUntilChanged()
            .shareIn(appScope, SharingStarted.Lazily, 1)

    val observeCoin: SharedFlow<Int> =
        userDoa.totalCoinsObserver().map { it ?: 0 }
            .distinctUntilChanged()
            .catch { emit(0) }
            .flowOn(ioDispatcher)
            .shareIn(appScope, SharingStarted.Lazily, 1)


    suspend fun submitPackRating(lectionId: LectionId, rating: Float, review: String) {
        try {
            val sessionId = lengoPreference.getSessionId()
            if (sessionId != null) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("func", lectionId.type)
                jsonObject.addProperty("owner", lectionId.owner)
                jsonObject.addProperty("pck", lectionId.packId)
                jsonObject.addProperty("rating", rating.toInt())
                jsonObject.addProperty("review", review)
                jsonObject.addProperty("session_id", sessionId)
                val resut = apiService.submitRating(jsonObject)
                logcat("RATING API") { "submitPackRating Complete ${resut}" }
            }
        } catch (ex: Exception) {
            logcat("RATING API") { ex.asLog() }
        }
    }

    suspend fun initSession(own: String, sel: String, onConfigComplete: (NRRemoteConfig?) -> Unit) {
        logcat("SESSION API") { "initSessiong" }
        withContext(ioDispatcher) {
            // val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
            try {
                val jsonObject = JsonObject()
//                jsonObject.addProperty(
//                    "app_version_name",
//                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
//                )
//                jsonObject.addProperty(
//                    "bundle_id",
//                    "${BuildConfig.APPLICATION_ID})"
//                )
                jsonObject.addProperty("device", getDeviceId())
                jsonObject.addProperty(
                    "device_name",
                    "${Build.DEVICE} ${Build.MANUFACTURER} ${Build.MODEL}"
                )
                jsonObject.addProperty(
                    "os",
                    "ANDROID ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})"
                )
                jsonObject.addProperty("own_lng", own)
                jsonObject.addProperty("sel_lng", sel)
                print(jsonObject)
                val result = apiService.initSession(jsonObject)
                if (result != null) {
                    logcat("SESSION API") { "${result?.msg}" }
                    logcat("SESSION API") { "${result?.toString()}" }
                    textToSpeechSpeaker.isGoogleApiEnable =
                        result.config.android_use_wavenet_tts.toBoolean()
                    lengoPreference.setSessionId(result.session_id)
                    lengoPreference.saveSessionRecommedRes(result.recommended_resources)
                    lengoPreference.setCouponsShown(result.config.couponsAvailableInLng, own)
                    result.config.source?.let { sor ->
                        lengoPreference.setSessionSource(sor)
                    }
                }
                withContext(mainDispatcher) {
                    onConfigComplete(result)
                }

            } catch (ex: Exception) {
                logcat("SESSION API") { ex.asLog() }
                withContext(mainDispatcher) {
                    onConfigComplete(null)
                }
            }
        }
    }

    suspend fun referralSession(resourceId: Int) {
        withContext(ioDispatcher) {
            try {
                val sessionId = lengoPreference.getSessionId()
                if (sessionId != null) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("resource_id", resourceId)
                    jsonObject.addProperty("session_id", sessionId)
                    val resut = apiService.referralSession(jsonObject)
                    logcat("SESSION API") { "referralSession Complete ${resut}" }
                }
            } catch (ex: Exception) {
                logcat("SESSION API") { ex.asLog() }
            }
        }
    }

    suspend fun eventSession(eventName: String) {
        withContext(ioDispatcher) {
            try {
                val sessionId = lengoPreference.getSessionId()
                if (sessionId != null) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("event_name", eventName)
                    jsonObject.addProperty("session_id", sessionId)
                    val resut = apiService.eventSession(jsonObject)
                    logcat("SESSION API") { "eventSession Complete ${resut}" }
                }
            } catch (ex: Exception) {
                logcat("SESSION API") { ex.asLog() }
            }
        }
    }

    fun getDeviceId(): String? {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun login(
        userNameOrEmail: String,
        password: String?,
        onLoginComplete: (LoginResponse?) -> Unit
    ) {
        withContext(ioDispatcher) {
            try {
                val jsonObject = JsonObject()
                jsonObject.addProperty("name_or_email", userNameOrEmail)
                jsonObject.addProperty("password", password)
                val result = apiService.login(jsonObject)
                withContext(mainDispatcher) {
                    onLoginComplete(result)
                }
            } catch (ex: Exception) {
                logcat("LOGIN API") { ex.asLog() }
                withContext(mainDispatcher) {
                    onLoginComplete(null)
                }
            }
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        onRegisterComplete: (RegisterResponse?) -> Unit
    ) {
        withContext(ioDispatcher) {
            try {
                val jsonObject = userJsonDataProvider.provideData(email, name, password)
                logcat { "Register data = $jsonObject" }
                val result = apiService.registerUser(jsonObject)
                withContext(mainDispatcher) {
                    onRegisterComplete(result)
                }
            } catch (ex: Exception) {
                logcat("Register API") { ex.asLog() }
                withContext(mainDispatcher) {
                    onRegisterComplete(null)
                }
            }
        }
    }

    suspend fun deleteUser(onDeletedUser: (error: String?) -> Unit) {
        withContext(ioDispatcher) {
            val currentUser = userDoa.currentUser()
            currentUser?.let { user ->
                val root = JsonObject()
                root.addProperty("password", user.password)
                root.addProperty("userid", user.userid)
                val response = apiService.deleteUser(root)
                if (response.userid != null) {
                    userDoa.insertUser(
                        UserEntity(
                            id = currentUser.id,
                            userid = -1,
                            sel = currentUser.sel,
                            own = currentUser.own,
                            pushed = false
                        )
                    )
                    packDoa.removeAllData()
                    onDeletedUser(null)
                } else {
                    onDeletedUser(response.msg ?: "Error deleting user")
                }
            }
        }
    }

    suspend fun updateUser(
        email: String,
        name: String,
        newPassword: String,
        onUpdateComplete: (error: String?) -> Unit
    ) {
        withContext(ioDispatcher) {
            val currentUser = userDoa.currentUser()
            currentUser?.let { user ->
                val root = JsonObject()
                root.addProperty("email", email)
                root.addProperty("name", name)
                root.addProperty("password", user.password)
                if(newPassword.isNotEmpty()) {
                    root.addProperty("password_new", newPassword)
                }
                root.addProperty("userid", user.userid)
                val response = apiService.updateUserData(root)
                if (response.userid != null) {
                    user.email = email
                    if(newPassword.isNotEmpty()) {
                        user.password = newPassword
                    }
                    user.name = name
                    user.pushed = false
                    userDoa.insertUser(user)
                    onUpdateComplete(null)
                } else {
                    onUpdateComplete(response.msg ?: "Error updating user")
                }
            }

        }
    }


    suspend fun logout(onLogoutComplete: () -> Unit) {
        withContext(ioDispatcher) {
            val currentUser = userDoa.currentUser()
            currentUser?.let {
                userDoa.insertUser(
                    UserEntity(
                        id = currentUser.id,
                        userid = -1,
                        sel = currentUser.sel,
                        own = currentUser.own,
                        pushed = false
                    )
                )
                packDoa.removeAllData()
                onLogoutComplete()
            }
        }
    }

    suspend fun pauseOrContinueSession(isPause: Boolean) {
        withContext(ioDispatcher) {
            try {
                val sessionId = lengoPreference.getSessionId()
                if (sessionId != null) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("session_id", sessionId)
                    if (isPause) {
                        val result = apiService.pauseSession(jsonObject)
                        logcat("SESSION API") { "pauseSession Complete" }
                    } else {
                        val result = apiService.continueSession(jsonObject)
                        logcat("SESSION API") { "continueSession Complete" }
                    }
                }
            } catch (ex: Exception) {
                logcat("SESSION API") { ex.asLog() }
            }
        }
    }

    suspend fun updateOwnLanguage(deviceLang: String) {
        withContext(ioDispatcher) {
            val currentUser = userDoa.currentUser()
            if (currentUser == null) {
                userDoa.insertUser(UserEntity(userid = -1, own = deviceLang,pushed = false))
            } else {
                if (currentUser.own != deviceLang) {
                    userDoa.insertUser(currentUser.copy(own = deviceLang, pushed = false))
                } else {
                    Log.d("updateOwnLanguage", "updateOwnLanguage:")
                }
            }
        }
    }

    suspend fun updateSelectedLang(selectedLang: String) {
        withContext(ioDispatcher) {
            val currentUser = userDoa.currentUser()
            if (currentUser == null) {
                userDoa.insertUser(UserEntity(userid = -1, sel = selectedLang,own = DEFAULT_OWN_LANG,pushed = false))
            } else {
                if (currentUser.sel != selectedLang) {
                    userDoa.insertUser(currentUser.copy(sel = selectedLang,pushed = false))
                } else {
                    Log.d("updateSelectedLang", "updateSelectedLang:")
                }
            }
        }
    }

    suspend fun addCoins(coins: Int) {
        withContext(ioDispatcher) {
            userDoa.addCoins(coins)
        }
    }

    suspend fun isCouponValid(code: String): Boolean {
        return lengoPreference.isCouponValid(code)
    }

    suspend fun makeCouponCodeInvalid(code: String) {
        lengoPreference.addToSubmittedCoupons(code)
    }

    suspend fun getUserEntitySelAndDevice(): UserDoa.UserLang {
        return userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
    }

    suspend fun getCurrentUserEntity(): UserEntity? {
        return userDoa.currentUser()
    }

}

