package com.lengo.uni.ui

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.lengo.data.datasource.BillingDataSource
import com.lengo.data.datasource.InAppUpdates
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.preferences.LengoPreference
import com.lengo.uni.ui.dashboard.DiscoverViewModel
import com.lengo.uni.ui.dashboard.my_pack.MyPackViewModel
import com.lengo.uni.ui.dashboard.my_word.MyWordsViewModel
import com.lengo.uni.ui.dashboard.progress.ProgressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var billingDataSource: BillingDataSource
    @Inject
    lateinit var lengoPreference: LengoPreference
    @Inject
    lateinit var textToSpeechSpeaker: TextToSpeechSpeaker

    private val updateHelper by lazy { InAppUpdates(this) }
    private lateinit var splashScreen: SplashScreen
    var isLaunchSync = false
    @FlowPreview
    val mainViewModel: MainViewModel by viewModels()
    val discoverViewModel: DiscoverViewModel by viewModels()
    val myPackViewModel: MyPackViewModel by viewModels()
    val myWordsViewModel: MyWordsViewModel by viewModels()
    val progress: ProgressViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()
        setDeviceLocale()
        Log.d("COUNTRY CODE", "onCreate: ${resources.configuration.locales.get(0).displayCountry}")
        enableEdgeToEdge()
        setContent {
            val windowSize = calculateWindowSizeClass(this)
            MainScreen(
                windowSize = windowSize.widthSizeClass
            ) {
//                if(!TestIdlingResource.isAppInTest()) {
//                    splashScreen.setKeepOnScreenCondition(it)
//                }
            }
        }
        updateHelper.init()
        lifecycle.addObserver(billingDataSource)
        billingDataSource.initBillingClient()
    }

    private fun setDeviceLocale() {
        mainViewModel.ownLanguageSelected(
            AppCompatDelegate.getApplicationLocales().toLanguageTags())
    }


    override fun onStart() {
        super.onStart()
        mainViewModel.onSessionContinue()
    }

    override fun onStop() {
        mainViewModel.syncDataWithServer()
        mainViewModel.onSessionPause()
        super.onStop()
    }

    override fun onDestroy() {
        textToSpeechSpeaker.releaseTextToSpeech()
        super.onDestroy()
    }


    fun launchDownloadTTS() {
        try {
            val installIntent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            startActivity(installIntent)
        } catch (ex: Exception) {
        }
    }


    fun initReviews() {
        lifecycleScope.launch {
                try {
                    if (!lengoPreference.isReviewSubmitted()) {
                        logcat { "Review Not Submitted" }
                        val manager = ReviewManagerFactory.create(this@MainActivity)
                        val request = manager.requestReviewFlow()
                        request.addOnCompleteListener { req ->
                            if (req.isSuccessful) {
                                logcat { "Review Succesfull" }
                                val reviewInfo = req.result
                                manager.launchReviewFlow(this@MainActivity, reviewInfo)
                                mainViewModel.reviewSubmitted()
                            } else {
                                logcat { "Review not succesfull" }
                            }
                        }
                    } else {
                        logcat { "Review Already Submitted" }
                    }
                } catch (ex: Exception) {
                    logcat { ex.asLog() }
                }
            }
    }


}


