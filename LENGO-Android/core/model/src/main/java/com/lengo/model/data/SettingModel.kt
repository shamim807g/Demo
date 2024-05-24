package com.lengo.model.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javax.annotation.concurrent.Immutable

@Immutable
data class SettingModel(
    val memorizeTask: Boolean = false,
    val quizTask: Boolean = true,
    val listeningTask: Boolean = false,
    val speakingTask: Boolean = false,
    val testTask: Boolean = false,
    val audioEnable: Boolean = true,
    val pronounceEnable: Boolean = true,
    val darkThemeEnable: Boolean? = null,
    val isUnlockCardVisible: Boolean = true,
    val isSync: Boolean = true
) {

    val allSettingareFalse: Boolean
        get() = !memorizeTask && !testTask
                && !quizTask && !speakingTask && !listeningTask

}


fun SettingModel.toJsonString(): String {
    return try {
        val jsonObject = JsonObject()
        jsonObject.addProperty("memorizeTask", this.memorizeTask)
        jsonObject.addProperty("quizTask", this.quizTask)
        jsonObject.addProperty("testTask", this.testTask)
        jsonObject.addProperty("listeningTask", this.listeningTask)
        jsonObject.addProperty("speakingTask", this.speakingTask)
        jsonObject.addProperty("audioEnable", this.audioEnable)
        jsonObject.addProperty("pronounceEnable", this.pronounceEnable)
        jsonObject.addProperty("darkThemeEnable", this.darkThemeEnable)
        jsonObject.addProperty("isUnlockCardVisible", this.isUnlockCardVisible)
        jsonObject.addProperty("isSync", this.isSync)
        jsonObject.toString()
    } catch (ex: Exception) {
        ""
    }
}

fun String.toSettingModel(): SettingModel {
    if (this.isNullOrEmpty()) {
        return SettingModel(darkThemeEnable = false)
    }
    return try {
        val mainObj = JsonParser.parseString(this).asJsonObject
        val memorizeTask = mainObj.getSafeBoolean("memorizeTask", false)
        val quizTask = mainObj.getSafeBoolean("quizTask", true)
        val testTask = mainObj.getSafeBoolean("testTask", false)
        val listeningTask = mainObj.getSafeBoolean("listeningTask", false)
        val speakingTask = mainObj.getSafeBoolean("speakingTask", false)
        val audioEnable = mainObj.getSafeBoolean("audioEnable", true)
        val pronounceEnable = mainObj.getSafeBoolean("pronounceEnable", true)
        val darkThemeEnable = mainObj.getSafeBoolean("darkThemeEnable", false)
        val isUnlockCardVisible = mainObj.getSafeBoolean("isUnlockCardVisible", true)
        val isSync = mainObj.getSafeBoolean("isSync", false)
        SettingModel(
            memorizeTask = memorizeTask,
            quizTask = quizTask,
            testTask = testTask,
            listeningTask = listeningTask,
            speakingTask = speakingTask,
            audioEnable = audioEnable,
            pronounceEnable = pronounceEnable,
            darkThemeEnable = darkThemeEnable,
            isUnlockCardVisible = isUnlockCardVisible,
            isSync = isSync
        )
    } catch (ex: Exception) {
        SettingModel()
    }

}


fun JsonObject.getSafeBoolean(key: String, default: Boolean): Boolean {
    return try {
        if (this.has(key))
            this.get(key).asBoolean else {
            default
        }
    } catch (ex: Exception) {
        default
    }
}