package com.lengo.data.repository

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.mutableStateOf
import com.lengo.common.R
import com.lengo.common.di.ApplicationScope
import com.lengo.common.femaleNames
import com.lengo.common.isUserLangSubscribe
import com.lengo.common.maleNames
import com.lengo.common.neutralNames
import com.lengo.model.data.Lang
import com.lengo.model.data.VoiceItem
import com.lengo.network.model.Voices
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import logcat.logcat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


fun Voices.toVoiceItem(): VoiceItem {
    val tagList = persistentListOf<String>().builder().apply {
        if (this@toVoiceItem.pro) {
            add("Pro")
        }
        add(this@toVoiceItem.ssmlGender.lowercase())
        if (this@toVoiceItem.natural) {
            add("natural voice")
        }
        add(Locale(this@toVoiceItem.languageCodes[0]).displayLanguage)
    }.build()
    return VoiceItem(personName = this.displayName, voiceName = this.name, tags = tagList, langCode = this@toVoiceItem.languageCodes[0])
}

@Singleton
class VoiceRepository @Inject constructor(
    private @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    private val lengoPreference: LengoPreference,
) {

    var offlineVoice = MutableStateFlow<List<VoiceItem>>(emptyList())
    val voicesList = MutableStateFlow<List<VoiceItem>>(emptyList())
    var maleName: String? = null
    var femaleName: String? = null

    fun updateOffLineVoiceList(accent: String?, voice: Voice?) {
        offlineVoice.value = emptyList()
        val mutableOfflineVoiceList = mutableListOf<VoiceItem>()
        appScope.launch {
            val existingAccentVoice = accent?.let { lengoPreference.getVoiceCode(it) }
            voice?.let { voi ->
                var personName = ""
                val tagList = persistentListOf<Any>().builder().apply {
                    val genderStr = voi.features.find { it.contains("gender") }
                    genderStr?.split("=")?.getOrNull(1)?.let { gen ->
                        personName = if (gen == "male") {
                            add(R.string.male)
                            maleName ?: maleNames.random()
                        } else {
                            add(R.string.female)
                            femaleName ?: femaleNames.random()
                        }
                        if (gen == "male") {
                            maleName = personName
                        } else femaleName = personName
                    }
                    add(R.string.offline)
                    add(voi.locale.displayLanguage)
                }.build()
                mutableOfflineVoiceList.add(
                    VoiceItem(
                        voiceName = "offline",
                        personName = personName.ifEmpty { neutralNames.random() },
                        tags = tagList,
                        isSelected = mutableStateOf(existingAccentVoice == "offline")
                    )
                )
            }
            offlineVoice.value = mutableOfflineVoiceList.toList()
        }
    }

    suspend fun updateVoiceList(userLang: Lang, availableVoices: List<Voices>) {
        logcat("VOICE") { "updateVoiceList ${userLang.accent} ${availableVoices.size}" }
        voicesList.value = emptyList()
        val mutableVoiceList = mutableListOf<VoiceItem>()
        val isSubscribe = isUserLangSubscribe(userLang)
        val existingAccentVoice = lengoPreference.getVoiceCode(userLang.accent)
        logcat("VOICE") { "updateVoiceList existingAccentVoice = ${existingAccentVoice}" }
        availableVoices.forEach {
            val tagList = persistentListOf<Any>().builder().apply {
                if (it.pro && !isSubscribe) {
                    add(R.string.pro)
                }
                if(it.ssmlGender.lowercase() == "male") {
                    add(R.string.male)
                } else {
                    add(R.string.female)
                }
                if (it.natural) {
                    add(R.string.features_natural_voice)
                }
                val code = it.languageCodes[0].split("-")
                add(Locale(code[0],code[1]).displayCountry)
            }.build()
            logcat("VOICE") { "updateVoiceList availableVoices item = ${it.name}" }
            mutableVoiceList.add(
                VoiceItem(
                    voiceName = it.name,
                    personName = it.displayName,
                    tags = tagList,
                    isSelected = mutableStateOf(it.name == existingAccentVoice),
                    langCode = it.languageCodes[0]
                )
            )
        }
        voicesList.value = mutableVoiceList.toList()
    }
}