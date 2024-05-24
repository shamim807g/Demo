package com.lengo.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.activity.result.contract.ActivityResultContract

private const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL =
    "https://play.google.com/store/account/subscriptions?sku=%s&package=com.lengo.uni"
class PlayStoreAppContract : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://play.google.com/store/apps/details?id=com.lengo.uni.${input}")
            setPackage("com.android.vending")
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return ""
    }
}

class DownloadTTSContract : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val installIntent = Intent()
        installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
        return installIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return ""
    }
}

class UnSubAppContract : ActivityResultContract<UnSubAppContract.UnSubAppContractInput, String?>() {

    data class UnSubAppContractInput(val code: String,val sku: String)
    override fun createIntent(context: Context, input: UnSubAppContractInput): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(String.format(PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,input.sku,input.code))
            setPackage("com.android.vending")
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return ""
    }

}

