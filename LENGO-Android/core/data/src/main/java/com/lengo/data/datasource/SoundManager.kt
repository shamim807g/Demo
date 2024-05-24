package com.lengo.data.datasource

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.lengo.common.R
import com.lengo.common.di.ApplicationScope
import com.lengo.database.appdatabase.doa.UserDoa
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
data class SoundManager @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    private val userDoa: UserDoa
) {


    fun playCorrect1() {
        playAudio(R.raw.correct1)
    }

    fun playCorrect2() {
        playAudio(R.raw.correct2)
    }

    fun playCorrect4() {
        playAudio(R.raw.correct4)
    }

    fun playWrong() {
        playAudio(R.raw.wrong)
    }

    fun playAudio(resId: Int) {
        MediaPlayer.create(context,resId).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    ).build()
            )
            setOnCompletionListener {
                release()
            }
            start()
        }
    }

}