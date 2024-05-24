package com.lengo.uni.ui.dashboard

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.ImageRepository
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.LoginEnableStatus
import com.lengo.data.repository.PacksRepository
import com.lengo.data.repository.UserRepository
import com.lengo.model.data.Lang
import com.lengo.model.data.Lection
import com.lengo.model.data.OnboardingFlags
import com.lengo.model.data.Pack
import com.lengo.model.data.SettingModel
import com.lengo.model.data.network.Recommendedresources
import com.lengo.preferences.LengoPreference
import com.lengo.preferences.ObservablePrefData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@Stable
data class DiscoverViewState(
    val packs: Map<String, List<Pack>> = emptyMap(),
    val imageMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val loadingPacks: Boolean = false,
    val lang: Lang? = null,
    val coinPack: List<Pack> = emptyList(),
    val recommendation: List<Recommendedresources> = emptyList(),
    val onboardingFlags: OnboardingFlags? = null,
    val prefData: ObservablePrefData? = null,
    val isLoginOrRegisterEnable: LoginEnableStatus = LoginEnableStatus.LOADING,
    val allLanguage: List<Lang> = emptyList(),
) {
    companion object {
        val Empty = DiscoverViewState()
    }
}

interface DiscoverAction {
    fun processPack(pack: Pack)
    fun processPackWithLection(pack: Pack, lection: Lection)
    fun referralSession(id: Int)
    fun UpdateSettingModel(settingModel: SettingModel)
}


@Immutable
sealed class DasboardEvents {
    data class INSUFFICIENT_COIN(val coinPacks: List<Pack>) : DasboardEvents()
    data class OPEN_PACK(val pack: Pack) : DasboardEvents()
    data class OPEN_LECTION(val pack: Pack, val lec: Lection) : DasboardEvents()
    data object OPEN_REVIEW : DasboardEvents()
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val packsRepository: PacksRepository,
    private val lengoPreference: LengoPreference,
    private val userRepository: UserRepository,
    private val languageRepository: LanguageRepository,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    private val imageRepository: ImageRepository,
    private val textToSpeechSpeaker: TextToSpeechSpeaker,
) : ViewModel(), DiscoverAction {

    private val dashboardChannel = Channel<DasboardEvents>()
    val event = dashboardChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        DiscoverViewState(
            loadingPacks = true,
            imageMap = imageRepository.lecImage,
            coinPack = packsRepository.paidPackList
        )
    )
    val uiState: StateFlow<DiscoverViewState> = _uiState.asStateFlow()


    init {
        logcat("DiscoverViewModel") { "DiscoverViewModel INIT" }

        //Observe Packs
        viewModelScope.launch(dispatchers) {
            packsRepository.observeDiscoverPack().flatMapLatest { packMap ->
                _uiState.update { it.copy(packs = packMap) }
                logcat("DiscoverViewModel") { "observeDiscoverPack ${packMap}" }
                imageRepository.fetchLectionImages(packMap)
            }.collect()
        }

        //Observe Lang
        viewModelScope.launch(dispatchers) {
            languageRepository.observeSelectedLang.collect { lang ->
                _uiState.update { it.copy(lang = lang) }
            }
        }

        //Observe Recommendations
        viewModelScope.launch(dispatchers) {
            lengoPreference.observeSessionRecommedRes().collect { rec ->
                _uiState.update { it.copy(recommendation = rec) }
            }
        }

        viewModelScope.launch(dispatchers) {
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
            }
        }

        viewModelScope.launch(dispatchers) {
            packsRepository.updateOrInsertPacks()
        }


        viewModelScope.launch(dispatchers) {
            userRepository.isLoginOrRegisterEnable.collect { isLogin ->
                _uiState.update { it.copy(isLoginOrRegisterEnable = isLogin) }
            }
        }


        //Observe All Language
        viewModelScope.launch(dispatchers) {
            languageRepository.observeAllLanguages.collect { allLang ->
                _uiState.update { it.copy(allLanguage = allLang) }
            }
        }
    }

    override fun UpdateSettingModel(settingModel: SettingModel) {
        viewModelScope.launch {
            lengoPreference.updateSetting(settingModel.copy(isSync = false))
        }
    }


    fun checkValidReviewState() {
        viewModelScope.launch {
            lengoPreference.isValidToShowReview { valid ->
                if (valid) {
                    viewModelScope.launch {
                        dashboardChannel.send(DasboardEvents.OPEN_REVIEW)
                    }
                }
            }
        }
    }


    override fun processPack(pack: Pack) {
        viewModelScope.launch {
            when (val result = packsRepository.processPack(pack)) {
                is PacksRepository.PackOrLectionStatus.InsufficientCoin ->
                    dashboardChannel.send(DasboardEvents.INSUFFICIENT_COIN(_uiState.value.coinPack))
                is PacksRepository.PackOrLectionStatus.LectionOpen ->
                    dashboardChannel.send(DasboardEvents.OPEN_LECTION(pack, result.lec))
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete ->
                    dashboardChannel.send(
                        DasboardEvents.OPEN_LECTION(pack, result.lec)
                    )
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete -> dashboardChannel.send(
                    DasboardEvents.OPEN_PACK(result.pack)
                )
                is PacksRepository.PackOrLectionStatus.PurchaseOpen -> dashboardChannel.send(
                    DasboardEvents.OPEN_PACK(result.pack)
                )
                else -> {}
            }

        }
    }

    override fun processPackWithLection(pack: Pack, lection: Lection) {
        viewModelScope.launch {
            when (val result = packsRepository.processPackAndLection(pack, lection)) {
                is PacksRepository.PackOrLectionStatus.InsufficientCoin ->
                    dashboardChannel.send(DasboardEvents.INSUFFICIENT_COIN(_uiState.value.coinPack))
                is PacksRepository.PackOrLectionStatus.LectionOpen -> dashboardChannel.send(
                    DasboardEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete -> dashboardChannel.send(
                    DasboardEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete -> dashboardChannel.send(
                    DasboardEvents.OPEN_PACK(result.pack)
                )
                is PacksRepository.PackOrLectionStatus.PurchaseOpen -> dashboardChannel.send(
                    DasboardEvents.OPEN_PACK(result.pack)
                )
                else -> {}
            }

        }
    }

    override fun referralSession(id: Int) {
        viewModelScope.launch {
            userRepository.referralSession(id)
        }
    }

    override fun onCleared() {
        logcat("DiscoverViewModel") { "DiscoverViewModel CLEARED!!!" }
        super.onCleared()
    }

//    class MyViewModelFactory(private val dbname: String) : ViewModelProvider.NewInstanceFactory() {
//        override fun <T : ViewModel?> create(modelClass: Class<T>): T = D(dbname) as T
//    }
}