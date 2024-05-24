package com.lengo.data.datasource

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.data.repository.VoiceRepository
import com.lengo.model.data.Lang
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import logcat.logcat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
data class TextToSpeechManager @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    val lengoPreference: LengoPreference,
    val voiceRepository: VoiceRepository
) {
    private val TAG = "TextToSpeech"

    var textToSpeech: TextToSpeech? = null
    var selLocale: Locale = Locale(DEFAULT_SEL_LANG)
    var selLang: Lang? = null
    var onSpeechComplete: (() -> Unit)? = null
    var isRefreshTTS: Boolean = false


    fun intTextToSpeech(onInitComplete: (() -> Unit)? = null) {
        textToSpeech = TextToSpeech(context) { ttsStatus ->
            if (ttsStatus == TextToSpeech.SUCCESS) {
                textToSpeech?.apply {
                    this.language = selLocale
                    val status = isLanguageAvailable(selLocale)
                    if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                        voiceRepository.updateOffLineVoiceList(selLang?.accent,null)
                    } else {
                        voiceRepository.updateOffLineVoiceList(selLang?.accent,voice)
                    }
                    playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "testing")
                    onInitComplete?.invoke()
                }
            } else {
                Log.d("intTextToSpeech ERROR", "Loading Error")
            }
        }


        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
                if(utteranceId != "testing") {
                    onSpeechComplete?.invoke()
                }
            }

            override fun onError(utteranceId: String?) {
                Log.d(TAG, "intTextToSpeech: onError: $utteranceId")
            }

        })
    }

    fun setLocale(selLang: Lang) {
        logcat("VOICE") { "setLocale ${selLang.accent}"}
        Log.d("intTextToSpeech setLocale", "${selLang.iso639_3}")
        this.selLang = selLang
        selLocale = Locale(selLang.iso639_3)
        intTextToSpeech()
    }


    fun stopTTS() {
        textToSpeech?.shutdown()
    }


    fun speak(
        word: String,
        isAddedToQueue: Boolean = false,
        pronounceEnable: Boolean = true,
        onSpeechComplete: (() -> Unit)? = null,
        onLanNotAvailble: (() -> Unit)? = null
    ) {
        this.onSpeechComplete = onSpeechComplete
        if (onSpeechComplete != null && !pronounceEnable) {
            this.onSpeechComplete?.invoke()
            return
        }

        if(isRefreshTTS) {
            intTextToSpeech {
                isRefreshTTS = false
                speak(word, isAddedToQueue, onLanNotAvailble)
            }
        } else {
            speak(word, isAddedToQueue, onLanNotAvailble)
        }
    }

    private fun speak(
        word: String,
        isAddedToQueue: Boolean,
        onLanNotAvailble: (() -> Unit)?
    ) {
        textToSpeech?.speak(
            word,
            if (isAddedToQueue) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_FLUSH,
            getTTSparam(), word
        )
        val status = textToSpeech?.isLanguageAvailable(selLocale)
        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
            isRefreshTTS = true
            onLanNotAvailble?.invoke()
        }
    }

    private fun getTTSparam(): Bundle {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val currentVolume = am?.getStreamVolume(STREAM_MUSIC)
        val maxVolume = am?.getStreamMaxVolume(STREAM_MUSIC)
        val params = Bundle()
        if (currentVolume != null && maxVolume != null) {
            logcat { "volume = ${currentVolume / maxVolume.toFloat()}" }
            params.putFloat(
                TextToSpeech.Engine.KEY_PARAM_VOLUME,
                currentVolume / maxVolume.toFloat()
            )
        }
        return params
    }


}