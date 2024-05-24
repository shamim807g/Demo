package com.lengo.uni.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.lengo.common.BuildConfig
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.LengoDispatchers
import com.lengo.common.TestIdlingResource
import com.lengo.common.getWelcomeString
import com.lengo.common.mapToSetupStructureLangCode
import com.lengo.data.datasource.BillingDataSource
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.ImageRepository
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.LoginEnableStatus
import com.lengo.data.repository.PacksRepository
import com.lengo.data.repository.UserRepository
import com.lengo.data.repository.VoiceRepository
import com.lengo.data.repository.WordsRepository
import com.lengo.data.worker.LectionImageWorker
import com.lengo.data.worker.SyncDataWorker
import com.lengo.model.data.Lang
import com.lengo.model.data.LectionId
import com.lengo.model.data.OnboardingFlags
import com.lengo.model.data.Pack
import com.lengo.model.data.SettingModel
import com.lengo.model.data.VoiceItem
import com.lengo.preferences.LengoPreference
import com.lengo.preferences.ObservablePrefData
import com.lengo.uni.ui.dashboard.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import java.util.Locale
import javax.inject.Inject


@Immutable
interface MainAction {
    fun reviewSubmitted()
    fun ownLanguageSelected(ownLang: String)
    fun selLanguageSelected(selLang: Lang)
    fun UpdateSettingModel(settingModel: SettingModel)
    fun onSessionPause()
    fun onSessionContinue()
    fun updatePackEmoji(packId: Long,type: String, owner: Long, lang: String, emoji: String)
    fun submitPackReviewRating(lectionId: LectionId, rating: Float, review: String)
    fun resetReviewSheet()
    fun markLangSheetShown()
    fun OnBoardingComplete()
}

@Stable
data class MainViewState(
    val isReviewSheetShowing: Boolean = false,
    val allLanguage: List<Lang> = emptyList(),
    val voices: StateFlow<List<VoiceItem>> = MutableStateFlow(emptyList()),
    val offlineVoice: StateFlow<List<VoiceItem>> = MutableStateFlow(emptyList()),
    val settingModel: SettingModel = SettingModel(),
    val userSelectedLang: Lang? = null,
    val deviceLang: String? = null,
    val imageMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val onboardingFlags: OnboardingFlags? = null,
    val prefData: ObservablePrefData? = null,
    val coinPack: SnapshotStateList<Pack> = SnapshotStateList(),
    val initialScreen: Screen? = null,
    val currentMenuScreen: String = MenuItem.Discover.route,
    val isUserLogin: Boolean = false,
) {
    companion object {
        val Empty = MainViewState()
    }

}


@FlowPreview
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    val lengoPreference: LengoPreference,
    private var billingDataSource: BillingDataSource,
    private val textToSpeechSpeaker: TextToSpeechSpeaker,
    private val languageRepository: LanguageRepository,
    private val userRepository: UserRepository,
    private val wordsRepository: WordsRepository,
    private val packsRepository: PacksRepository,
    private val voiceRepository: VoiceRepository,
    private val imageRepository: ImageRepository,
) : ViewModel(), MainAction {

    private val TAG = "LingoViewModel"

    var isStartAfterStop = false

    val liveWorkStatus = MediatorLiveData<WorkInfo>()

    private val _uiState = MutableStateFlow(
        MainViewState(
            imageMap = imageRepository.lecImage,
            coinPack = packsRepository.paidPackList,
            voices = voiceRepository.voicesList.asStateFlow(),
            offlineVoice = voiceRepository.offlineVoice.asStateFlow()
        )
    )
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "INIT: ")

        //TestIdlingResource.increment()

        //observePrefData
        viewModelScope.launch {
            lengoPreference.observePrefData.distinctUntilChanged().collect { data ->
                _uiState.update {
                    it.copy(
                        prefData = data,
                        onboardingFlags = OnboardingFlags(
                            data.isLangSheetShown,
                            data.isSubSheetShown
                        )
                    )
                }
                setInitialScreen(data)
            }
        }

        //Observe SettingModel
        viewModelScope.launch {
            userRepository.observeSettingModel.collect { setting ->
                _uiState.update { it.copy(settingModel = setting) }
            }
        }

        //Observe All Language
        viewModelScope.launch {
            languageRepository.observeAllLanguages.collect { allLang ->
                _uiState.update { it.copy(allLanguage = allLang) }
            }
        }

        //Observe Lang
        viewModelScope.launch {
            languageRepository.observeSelectedLang.collect { userLng ->
                _uiState.update { it.copy(userSelectedLang = userLng) }
                textToSpeechSpeaker.setSelectedLang(userLng)
                userRepository.initSession(
                    context.resources.configuration.locales.get(0).language,
                    userLng.code
                ) { remoteCofig ->
                    logcat { "remoteCofig ${remoteCofig}" }
                    viewModelScope.launch {
                        userRepository.isLoginOrRegisterEnable.value =
                            if (remoteCofig != null && remoteCofig.config.onboardingLogin == "true") LoginEnableStatus.ENABLE
                            else LoginEnableStatus.DISABLE
                    }
                }
            }
        }

        //Update all lng
        viewModelScope.launch {
            languageRepository.updateAllLanguges()
        }

        viewModelScope.launch(dispatchers) {
            billingDataSource.isNewSubEnable.mapLatest {
                packsRepository.updatePacksForSubAndUnSub()
                refreshVoices()
            }.collect()
        }

        userRepository.observeUserData.onEach { userEntity ->
            _uiState.update { it.copy(isUserLogin = userEntity.userid > -1,) }
        }.launchIn(viewModelScope)

        sendReviewEvent()
    }

    fun getAllVoices() {
        viewModelScope.launch {
            _uiState.value.userSelectedLang?.let { lang ->
                textToSpeechSpeaker.setSelectedLang(lang)
            }
        }
    }

    private fun setInitialScreen(data: ObservablePrefData) {
        if(_uiState.value.initialScreen == null) {
            if (!data.isOnboardingComplete) {
                _uiState.update { it.copy(initialScreen = Screen.OnBoardingMainScreen) }
            } else if (!data.isSubSheetShown) {
                _uiState.update { it.copy(initialScreen = Screen.OnboardingSubscription) }
            } else if (!data.isLangSheetShown && BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
                _uiState.update { it.copy(initialScreen = Screen.OnboardingSelectLang) }
            } else {
                _uiState.update { it.copy(initialScreen = Screen.Dashboard) }
            }
        }
    }

    private fun sendReviewEvent() {
        viewModelScope.launch {
            if (!lengoPreference.isReviewSubmitted() && lengoPreference.getPackReviewRating() >= 5f) {
                _uiState.update { it.copy(isReviewSheetShowing = true) }
            }
        }
    }

    override fun reviewSubmitted() {
        viewModelScope.launch {
            lengoPreference.setReviewSubmitted()
            userRepository.eventSession("display_review_alert")
        }
    }

    override fun ownLanguageSelected(ownLang: String) {
        viewModelScope.launch {
            DEFAULT_OWN_LANG = if(!ownLang.isNullOrEmpty()) {
                val locale = Locale(ownLang)
                Locale.setDefault(locale)
                mapToSetupStructureLangCode(ownLang)
            } else {
                mapToSetupStructureLangCode(context.resources.configuration.locales.get(0).language)
            }
            userRepository.updateOwnLanguage(DEFAULT_OWN_LANG)
            packsRepository.updateOrInsertPacks()
            packsRepository.updatePacksForSubAndUnSub()
            _uiState.update { it.copy(deviceLang = DEFAULT_OWN_LANG) }
        }
    }

    override fun selLanguageSelected(selLang: Lang) {
        viewModelScope.launch {
            userRepository.updateSelectedLang(selLang.code)
            packsRepository.updateOrInsertPacks()
            packsRepository.updatePacksForSubAndUnSub()
            textToSpeechSpeaker.setSelectedLang(selLang)
        }
    }

    fun refreshDataAfterLogin() {
        viewModelScope.launch {
            packsRepository.updateOrInsertPacks()
            packsRepository.updatePacksForSubAndUnSub()
            refreshVoices()
        }
    }

    override fun UpdateSettingModel(settingModel: SettingModel) {
        viewModelScope.launch {
            lengoPreference.updateSetting(settingModel.copy(isSync = false))
        }
    }

    override fun onSessionPause() {
        isStartAfterStop = true
        logcat { "onSessionPause" }
        viewModelScope.launch {
            userRepository.pauseOrContinueSession(true)
        }
    }

    override fun onSessionContinue() {
        if (isStartAfterStop) {
            logcat { "onSessionContinue" }
            viewModelScope.launch {
                userRepository.pauseOrContinueSession(false)
            }
        }
    }

    override fun updatePackEmoji(packId: Long,type: String, owner: Long, lang: String, emoji: String) {
        viewModelScope.launch {
            packsRepository.updatePackEmoji(packId,type,owner,lang, emoji)
        }
    }

    override fun submitPackReviewRating(lectionId: LectionId, rating: Float, review: String) {
        viewModelScope.launch {
            lengoPreference.setPackReviewRating(rating)
            userRepository.submitPackRating(lectionId, rating, review)
        }
    }

    override fun resetReviewSheet() {
        _uiState.update { it.copy(isReviewSheetShowing = false) }
    }

    override fun markLangSheetShown() {
        viewModelScope.launch {
            lengoPreference.setLangSheetShown(true)
        }
    }

    fun markSubSheetShown() {
        viewModelScope.launch {
            lengoPreference.setOnboardingSubSheetShown(true)
        }
    }

    fun addFakeData() {
        viewModelScope.launch {
            TestIdlingResource.increment()
            wordsRepository.addFakeData()
        }
    }

    override fun OnBoardingComplete() {
        viewModelScope.launch {
            lengoPreference.setOnboardingCompleted()
        }
    }

    fun updateVoice(selectVoice: VoiceItem) {
        _uiState.value.userSelectedLang?.let { lang ->
            viewModelScope.launch {
                textToSpeechSpeaker.saveVoice(lang,selectVoice)
            }
        }
    }

    fun refreshVoices() {
        _uiState.value.userSelectedLang?.let { lang ->
            viewModelScope.launch {
                textToSpeechSpeaker.refreshVoiceList(lang)
            }
        }
    }

    fun playVoice(selectVoice: VoiceItem) {
        _uiState.value.userSelectedLang?.let { lang ->
            textToSpeechSpeaker.speak(
                text = getWelcomeString(lang.code),
                forcePlayWithVoiceCode = selectVoice
            )
        }
    }

    fun updateMenuScreen(route: String) {
        _uiState.update { it.copy(currentMenuScreen = route) }
    }

    fun syncDataWithServer() {
        logcat { "CALL!! syncDataWithServer" }
        val request = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .addTag("SyncDataWorker")
            .build()

        val request2 = OneTimeWorkRequestBuilder<LectionImageWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("LectionImageWorker")
            .build()

         WorkManager.getInstance(context)
            .beginUniqueWork("syncDataWithServer", ExistingWorkPolicy.KEEP, request)
            .then(request2).enqueue()

        val liveOpStatus = WorkManager.getInstance(context).getWorkInfoByIdLiveData(request2.id)

        liveWorkStatus.addSource(liveOpStatus) { workStatus ->
            liveWorkStatus.value = workStatus
            if (workStatus.state.isFinished) {
                liveWorkStatus.removeSource(liveOpStatus)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "CLEAR: ")
    }


}