package com.lengo.data.datasource

import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import logcat.asLog
import logcat.logcat

class InAppUpdates(private val activity: ComponentActivity) {
    private val appUpdateManager = AppUpdateManagerFactory.create(activity)

    fun init() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            try {
                android.util.Log.d("UpdateHelper", "Update Availability: ${appUpdateInfo.updateAvailability()}")
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        111) // I don't care
                }
            } catch (ex: Exception) {
                logcat { ex.asLog() }
            }
        }
    }

}