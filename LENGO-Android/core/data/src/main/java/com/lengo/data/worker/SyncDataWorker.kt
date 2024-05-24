package com.lengo.data.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.JsonObject
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.di.ApplicationScope
import com.lengo.data.R
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.datasource.UserJsonDataProvider
import com.lengo.data.mapper.toLectionEntity
import com.lengo.data.mapper.toObjectEntity
import com.lengo.data.mapper.toPackEntity
import com.lengo.data.mapper.toPacksEntity
import com.lengo.data.repository.UserRepository
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.appdatabase.model.UserEntity
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import com.lengo.network.ApiService
import com.lengo.network.model.LoginResponse
import com.lengo.preferences.LengoPreference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import logcat.asLog
import logcat.logcat

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userJsonDataProvider: UserJsonDataProvider,
    private val packsDao: PacksDao,
    private val userRepository: UserRepository,
    private val jsonPackDao: JsonPackDao,
    private val lengoPreference: LengoPreference,
    private val userDoa: UserDoa,
    private val apiService: ApiService,
    @ApplicationScope val appScope: CoroutineScope,
    private val lengoDataSource: LengoDataSource,
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "LENGO-WORKER"

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(NOTIFICATION_ID, createNotification(),FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification())
        }
    }

    private fun createNotification(): Notification {
            val notification = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            )
            .setContentTitle("Syncing Data...")
            .setTicker("Syncing Data...")
            .setSmallIcon(R.drawable.ic_splash)
            .setSilent(true)
            .setOngoing(true)
        // 3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notification, CHANNEL_ID)
        }
        return notification.build()
    }

    override suspend fun doWork(): Result {
        logcat(TAG) { "WORK START!!!!!" }
        val currentUser = userDoa.currentUser()
        if (currentUser != null && currentUser.userid.toInt() != -1) {
            logcat(TAG) { "USER IS LOGIN!!!!!" }

            var userLections = listOf<LectionsEntity>()
            val currentActivityId = if (currentUser.activity_id == null) 0 else currentUser.activity_id ?: 0L
            logcat(TAG) { "currentActivityId = ${currentActivityId}" }
            logcat(TAG) { "userid = ${currentUser.userid.toInt()}" }
            val result = requestUserInfo(currentActivityId, currentUser)
            if (result != null) {
                logcat(TAG) { "requestUserInfo = ${result}" }
                if(result.msg != "Your device is up to date") {
                 if ((result.activity_id ?: 0L) > currentActivityId) {
                    logcat(TAG) { "request Activity Id > currentActivityId" }
                    userJsonDataProvider.setDateState(result)
                    userJsonDataProvider.updateSetting(result)
                    userJsonDataProvider.setUserLoginData(result, null,false)
                    userLections = pullPackData(result)
                     logcat(TAG) { "pullPackData complete" }
                } else {
                     pushData(currentUser)
                 }
                } else {
                    logcat(TAG) { "DEVICE IS UP TO DATE!!" }
                    pushData(currentUser)
                }
            } else {
                logcat(TAG) { "updateUserInfo = null" }
            }

            logcat(TAG) { "userLections = ${userLections.toString()}" }
            if(!userLections.isEmpty()) {
                val outputDataBuilder = Data.Builder()
                val list = userLections.map { lec ->
                    "${lec.owner}-${lec.type}-${lec.pck}-${lec.lec}-${lec.lng}-${lec.title[currentUser.own]}"
                }
                list.forEach { lec -> logcat(TAG) { lec } }
                outputDataBuilder.putStringArray("userPackLec", list.toTypedArray())
                logcat(TAG) { "user created lections = ${list.toTypedArray().toString()}" }
                logcat(TAG) { "WORK COMPLETE!!!!!" }
                return Result.success(outputDataBuilder.build())
            }

        } else {
            logcat(TAG) { "USER IS NOT LOGIN!!!!" }
        }
        logcat(TAG) { "WORK COMPLETE!!!!!" }
        return Result.success()
    }

    private suspend fun requestUserInfo(
        currentActivityId: Long,
        currentUser: UserEntity
    ): LoginResponse? {
        val result = try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("activity_id", currentActivityId)
            jsonObject.addProperty("userid", currentUser.userid.toInt())
            apiService.requestUserInfo(jsonObject)
        } catch (e: Exception) {
            logcat(TAG) { e.asLog() }
            null
        }
        return result
    }

    private suspend fun pullPackData(result: LoginResponse): List<LectionsEntity> {
        val userCreatedLecEntities = mutableListOf<LectionsEntity>()
        val devicelng = DEFAULT_OWN_LANG
        val packs = userJsonDataProvider.getPackKeys(result)
        val newData = try {
            apiService.userpacks(packs)
        } catch (e: Exception) {
            logcat(TAG) { e.asLog() }
            null
        }
        if (newData != null) {
            logcat(TAG) { "newData = ${newData}" }
            val packsEntities = mutableListOf<PacksEntity>()
            val lecEntities = mutableListOf<LectionsEntity>()
            val objEntities = mutableListOf<ObjectEntity>()

            result.userdata?.loaded_packs?.forEach { loadPack ->
                if(loadPack.func == SYS_GRAMMER || loadPack.func == SYS_VOCAB) {
                    val jsonPack = jsonPackDao.getPacks(loadPack.pck?.toLong() ?: -1,loadPack.func ?: "",loadPack.owner?.toLong() ?: -1)
                    val lections = jsonPack?.toLectionEntity(loadPack.lng ?: "")
                    lections?.let { lecs -> lecEntities.addAll(lecs) }
                    val packEntity = jsonPack?.toPacksEntity(loadPack.lng ?: "")
                    packEntity?.let { packsEntities.add(it) }
                } else {
                    val packdata = newData.packs_json.metadata?.find {
                        it.func == loadPack.func && it.id == loadPack.pck?.toLong() && it.owner == loadPack.owner?.toLong()
                    }
                    packdata?.toLectionEntity(loadPack.lng ?: "")?.let {
                        lecEntities.addAll(it)
                        userCreatedLecEntities.addAll(it)
                    }
                    packdata?.toPackEntity(loadPack.lng ?: "")
                        ?.let { packsEntities.add(it) }
                }
            }
            result.userdata?.int_values?.forEach { obj ->
                if(obj.func == SYS_VOCAB || obj.func == SYS_GRAMMER) {
                    val jsonObj = jsonPackDao.getObj(obj.obj?.toLong()?: -1,obj.lec?.toLong()?: -1,obj.pck?.toLong()?: -1,obj.func ?: "",obj.owner?.toLong()?: -1)
                    val objEntity = jsonObj?.toObjectEntity(obj.lng ?: "",devicelng,obj.intvalue ?: -1)
                    objEntity?.let { objEntities.add(it) }
                } else {
                    val objData = newData.packs_json.objects.find {
                        it.func == obj.func
                                && it.obj == obj.obj?.toLong()
                                && it.owner == obj.owner?.toLong()
                                && it.lec == obj.lec?.toLong()
                                && it.pck == obj.pck?.toLong()
                    }
                    objData?.toObjectEntity(obj.lng ?: "", devicelng, obj.intvalue ?: -1)
                        ?.let { objEntities.add(it) }
                }
            }
            logcat(TAG) { "packsEntities = ${packsEntities}" }
            logcat(TAG) { "lecEntities = ${lecEntities}" }
            logcat(TAG) { "objEntities = ${objEntities}" }

            packsDao.insertPacks(packsEntities.toList())
            packsDao.insertLecttions(lecEntities.toList())
            packsDao.insertObject(objEntities.toList())
            logcat(TAG) { "INSERT COMPLETES" }
            val newisLoginOrRegisterComplete = userRepository.isLoginOrRegisterComplete.value++
            logcat(TAG) { "SENDING EVENT NUM FOR isLoginOrRegisterComplete ${newisLoginOrRegisterComplete}" }
            userRepository.isLoginOrRegisterComplete.value = newisLoginOrRegisterComplete
            logcat(TAG) { "isLoginOrRegisterComplete " }
        } else {
            logcat(TAG) { "newData = null" }
        }
        return userCreatedLecEntities
    }


    private suspend fun pushData(currentUser: UserEntity) {
        logcat(TAG) { "PUSH DATA START!!!!!" }
        val updateUserResult = try {
            val pushdata = userJsonDataProvider.fetchPushedData(currentUser.userid)
            logcat(TAG) { "all app data to push = ${pushdata.toString()}" }
            pushdata?.let { apiService.updateUserInfo(it) }
        } catch (e: Exception) {
            logcat(TAG) { e.asLog() }
            null
        }
        if(updateUserResult != null) {
            logcat(TAG) { "all app data to push Result = ${updateUserResult}" }
            val dbUser = userDoa.currentUser()
            updateUserResult.activity_id?.let {
                dbUser?.activity_id = it
            }
            dbUser?.let { userDoa.insertUser(it) }
        } else {
            logcat(TAG) { "updateUserResult = null" }
        }

        var pushResult = try {
            val metaData = userJsonDataProvider.preparePushUserData(currentUser.userid)
            logcat(TAG) { "user data to upload = ${metaData.toString()}" }
            metaData?.let { apiService.userPushPacks(metaData) }
        } catch (e: Exception) {
            logcat(TAG) { e.asLog() }
            null
        }
        if (pushResult != null) {
            logcat(TAG) { "user data to upload pushResult = ${pushResult}" }
            if(pushResult.has("activity_id")) {
                userDoa.currentUser()?.apply {
                    pushResult!!.get("activity_id").asInt.let {
                        this.activity_id = it.toLong()
                    }
                    userDoa.insertUser(this)
                }
            } else {
                pushResult = null
            }
        } else {
            logcat(TAG) { "pushResult = null" }
        }

        if(updateUserResult != null || pushResult != null) {
            logcat(TAG) { "Database pushed fields are mark as false" }
            packsDao.pushedAllData()
            val currectSettings = lengoPreference.syncSettingModel()
            lengoPreference.updateSetting(currectSettings.copy(isSync = true))
        }
        logcat(TAG) { "PUSH DATA END!!!!!" }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        notificationBuilder: NotificationCompat.Builder,
        id: String
    ) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id, "WorkManagerApp", NotificationManager.IMPORTANCE_LOW
        )
        channel.setSound(null, null)
        channel.description = "WorkManagerApp Notifications"
        channel.setShowBadge(false)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "workSync"
        const val NOTIFICATION_ID = 111
    }

}