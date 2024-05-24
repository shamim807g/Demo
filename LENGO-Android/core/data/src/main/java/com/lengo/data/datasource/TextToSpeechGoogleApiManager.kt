package com.lengo.data.datasource

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.extension.MyMediaDataSource
import com.lengo.data.repository.VoiceRepository
import com.lengo.network.ApiService
import com.lengo.network.GoogleTTSAPI
import com.lengo.network.model.TTSRequest
import com.lengo.network.model.TTSVoicesRequest
import com.lengo.network.model.Voice
import com.lengo.network.model.Voices
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import logcat.asLog
import logcat.logcat
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
data class TextToSpeechGoogleApiManager @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val dispatcher: CoroutineDispatcher,
    @Dispatcher(LengoDispatchers.Main) val mainDispatchers: CoroutineDispatcher,
    val lengoPreference: LengoPreference,
    val googleAPI: GoogleTTSAPI,
    val apiService: ApiService,
    val fileDownloader: FileDownloader,
    val voiceRepository: VoiceRepository,
) {
    suspend fun playSound(
        text: String,
        accent: String,
        playWithVoiceCode: String? = null,
        isPronounceEnable: Boolean = true,
        onComplete: (() -> Unit)?,
        onError: () -> Unit
    ) {

        if (!isPronounceEnable) {
            logcat("tts") { "isPronounce not enable" }
            onError()
            return
        }

        withContext(dispatcher) {
            try {
                val selectedVoice = playWithVoiceCode
                if (selectedVoice != null) {
                    val ttsfilePath = "${getMd5(text + selectedVoice)}.mp3"
                    logcat("tts") { "PATH:$ttsfilePath" }
                    val path = isTTSFileExist(context, ttsfilePath)
                    if (path != null) {
                        logcat("tts") { "File already EXIST!" }
                        withContext(mainDispatchers) {
                            playAudio(null, path, onComplete)
                        }
                        return@withContext
                    }

                    logcat("tts") { "API STARTED!! for ${selectedVoice}" }
                    withTimeout(6000) {
                        val result = apiService.getTTSFile(
                            TTSRequest(
                                text = text,
                                voice = Voice(languageCode = accent, voicename = selectedVoice)
                            )
                        )
                        if (result?.url != null) {
                            val filePath = fileDownloader.downloadFile(
                                context, ttsfilePath, "tts",
                                result.url!!
                            )
                            withContext(mainDispatchers) {
                                playAudio(null, filePath, onComplete)
                            }
                        } else {
                            withContext(mainDispatchers) {
                                onError()
                            }
                            logcat("tts") { "API ENDED WITH EMPTY RESULT!!" }
                        }
                    }

                } else {
                    logcat("tts") { "Accent not avialable" }
                    withContext(mainDispatchers) {
                        onError()
                    }
                }

            } catch (ex: Exception) {
                withContext(mainDispatchers) {
                    onError()
                }
                logcat("tts") { "Exception ${ex.asLog()}" }
            }
        }
    }

    fun playAudio(
        mediaDataSource: MyMediaDataSource? = null,
        path: String? = null,
        onComplete: (() -> Unit)?
    ) {
        logcat("tts") { "playAudio" }
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    ).build()
            )
            if (mediaDataSource != null) {
                setDataSource(mediaDataSource)
            } else {
                setDataSource(path)
            }
            setOnCompletionListener {
                release()
                onComplete?.invoke()
                logcat("tts") { "setOnCompletionListener" }

            }
            prepareAsync()
            setOnPreparedListener {
                start()
                logcat("tts") { "setOnPreparedListener" }
            }
        }
    }

    fun isTTSFileExist(
        context: Context,
        ttsFileName: String,
    ): String? {
        return try {
            val ttsdDirectory = File("${context.cacheDir}", "tts")
            val ttsfile = File("$ttsdDirectory", ttsFileName)
            if (ttsfile.exists()) {
                ttsfile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllVoices(accent: String): List<Voices>? {
        return try {
            val result = apiService.getTxtVoices(TTSVoicesRequest(accent = accent))
            result?.voices ?: emptyList<Voices>()
        } catch (ex: Exception) {
            logcat("TextToSpeechGoogleApiManager getAllLanguages Exception:") { ex.asLog() }
            null
        }
    }

    fun releasePlayer() {
    }

    fun getMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}


fun main() {
    val md = MessageDigest.getInstance("MD5")
    val string = BigInteger(1, md.digest("holasss".toByteArray())).toString(16).padStart(32, '0')
    print(string)
}