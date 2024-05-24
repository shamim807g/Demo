package com.lengo.data.datasource

import android.content.Context
import android.util.Log
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.di.ApplicationScope
import com.lengo.data.repository.VoiceRepository
import com.lengo.data.repository.toVoiceItem
import com.lengo.model.data.Lang
import com.lengo.model.data.VoiceItem
import com.lengo.network.model.Voices
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class TextToSpeechSpeaker @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    @ApplicationScope val appScope: CoroutineScope,
    private val preference: LengoPreference,
    private val voiceRepository: VoiceRepository,
    private val textToSpeechManager: TextToSpeechManager,
    private val textToSpeechGoogleApiManager: TextToSpeechGoogleApiManager,
) {

    var isGoogleApiEnable = true
    private var selectedLang: Lang? = null
    private var selectedVoice: VoiceItem? = null
    var availableVoices: List<Voices> = emptyList()
    private val TAG = "tts"

    suspend fun setSelectedLang(selectedLang: Lang) {
        if(this.selectedLang == null || this.selectedLang!!.code != selectedLang.code || availableVoices.isEmpty()) {
            this.selectedLang = selectedLang
            logcat("tts") { "setSelectedLang ${selectedLang.accent}"}
            textToSpeechManager.setLocale(selectedLang)
            availableVoices = textToSpeechGoogleApiManager.getAllVoices(selectedLang.accent) ?: emptyList()
            val existingVoice = preference.getVoiceCode(selectedLang.accent)
            if(existingVoice != null) {
                logcat("tts") { "existingVoice != null ${existingVoice}"}
                selectedVoice = availableVoices.find { it.name == existingVoice }?.toVoiceItem()
            }

            if(existingVoice == null && availableVoices.isNotEmpty()) {
                logcat("tts") { "existingVoice == null ${availableVoices.size}"}
                availableVoices.firstOrNull()?.let { firstVoice ->
                    logcat("tts") { "setAccentVoiceCode ${selectedLang.accent} ${firstVoice}"}
                    preference.setAccentVoiceCode(selectedLang.accent,firstVoice.name)
                    selectedVoice = firstVoice.toVoiceItem()
                }
            }
            voiceRepository.updateVoiceList(selectedLang,availableVoices)
        }
    }


    fun speak(
        text: String,
        isPronounceEnable: Boolean = true,
        forcePlayWithVoiceCode: VoiceItem? = null,
        isAddedToQueue: Boolean = false,
        onSpeechComplete: (() -> Unit)? = null,
        onLangDownload: (() -> Unit)? = null
    ) {
        Log.d(
            TAG,
            "speak() called with: text = $text, isPronounceEnable = $isPronounceEnable, forcePlayWithVoiceCode = $forcePlayWithVoiceCode, isAddedToQueue = $isAddedToQueue, onSpeechComplete = $onSpeechComplete, onLangDownload = $onLangDownload"
        )

        appScope.launch {
            val voiceToPlay = forcePlayWithVoiceCode ?: selectedVoice
            Log.d(
                TAG,
                "voiceToPlay with: voiceName = ${voiceToPlay?.voiceName}, langCode = ${voiceToPlay?.langCode}, tags = ${voiceToPlay?.tags.toString()}, personName = ${voiceToPlay?.personName}"
            )
            if(voiceToPlay != null) {
                if(isOfflineVoice(voiceToPlay.voiceName)) {
                    textToSpeechManager.speak(
                        word = text,
                        isAddedToQueue = isAddedToQueue,
                        pronounceEnable = isPronounceEnable,
                        onSpeechComplete = onSpeechComplete,
                        onLanNotAvailble = onLangDownload
                    )
                    return@launch
                } else {
                    if(forcePlayWithVoiceCode == null && !isGoogleApiEnable) {
                        textToSpeechManager.speak(
                            word = text,
                            isAddedToQueue = isAddedToQueue,
                            pronounceEnable = isPronounceEnable,
                            onSpeechComplete = onSpeechComplete,
                            onLanNotAvailble = onLangDownload)
                    } else {
                        textToSpeechGoogleApiManager.playSound(
                            text,
                            voiceToPlay.langCode,
                            voiceToPlay.voiceName,
                            isPronounceEnable,
                            onError = {
                                textToSpeechManager.speak(
                                    word = text,
                                    isAddedToQueue = isAddedToQueue,
                                    pronounceEnable = isPronounceEnable,
                                    onSpeechComplete = onSpeechComplete,
                                    onLanNotAvailble = onLangDownload)
                            },
                            onComplete = onSpeechComplete
                        )
                    }

                    return@launch
                }
            } else {
                textToSpeechManager.speak(
                    word = text,
                    isAddedToQueue = isAddedToQueue,
                    pronounceEnable = isPronounceEnable,
                    onSpeechComplete = onSpeechComplete,
                    onLanNotAvailble = onLangDownload
                )
            }
        }
    }

    suspend fun saveVoice(lang: Lang,voiceCode: VoiceItem) {
        selectedVoice = voiceCode
        preference.setAccentVoiceCode(lang.accent,voiceCode.voiceName)
    }

    suspend fun refreshVoiceList(lang: Lang) {
        selectedLang?.accent?.let { voiceRepository.updateVoiceList(lang,availableVoices ) }
    }

    fun isOfflineVoice(playWithVoiceCode: String): Boolean {
        return playWithVoiceCode == "offline"
    }

    fun releaseTextToSpeech() {
        textToSpeechManager.stopTTS()
        textToSpeechGoogleApiManager.releasePlayer()
    }

}