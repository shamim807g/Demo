package com.lengo.uni.ui.quiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.result.contract.ActivityResultContract

class SpeechContract : ActivityResultContract<String, String?>() {

    override fun createIntent(context: Context, input: String): Intent {
        val spech = SpeechRecognizer.createSpeechRecognizer(context)
        val fl = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        fl.putExtra("android.speech.extra.LANGUAGE", input ?: "en")
        fl.putExtra(RecognizerIntent.EXTRA_LANGUAGE, input ?: "en")
        fl.putExtra(RecognizerIntent.EXTRA_PROMPT, "Text to speech")
        fl.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        spech.startListening(fl)
        return fl
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK && intent == null) {
            return null
        }
        val result = intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        println("result: $result?.get(0)")
        return result?.getOrNull(0) ?: ""
    }

}