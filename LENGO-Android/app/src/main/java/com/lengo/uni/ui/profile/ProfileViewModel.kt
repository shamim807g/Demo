package com.lengo.uni.ui.profile

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.R
import com.lengo.common.extension.Coins
import com.lengo.common.extension.bronzeToCoins
import com.lengo.common.inAppList
import com.lengo.common.subscriptionsList
import com.lengo.data.datasource.BillingDataSource
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.UserRepository
import com.lengo.model.data.BillingInAppItem
import com.lengo.model.data.Lang
import com.lengo.model.data.SettingModel
import com.lengo.model.data.Subscription
import com.lengo.preferences.LengoPreference
import com.lengo.uni.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject


@Stable
data class ProfileViewState(
    val coinTypes: Coins = Coins(),
    val totalCoins: Int = 0,
    val skuList: ImmutableList<BillingInAppItem> = persistentListOf(),
    val subList: ImmutableList<Subscription> = persistentListOf(),
    val isLoading: Boolean = false,
    val settingModel: SettingModel = SettingModel(),
    val userSelectedLang: Lang? = null,
    val deviceLang: Lang? = null,
    val showCoupon: Boolean = false,
    val isUserLogin: Boolean = false,
    val billingError: String = "",
    val userNameorEmail: String = "",
    val levelString: String = "",
    val scoreString: String = "",
    val progressPercent: Float = 0f
) {

    companion object {
        val Empty = ProfileViewState()
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var billingDataSource: BillingDataSource,
    private val userRepository: UserRepository,
    private val lengoPreference: LengoPreference,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    private val lengoDataSource: LengoDataSource,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileViewState(
        subList = subscriptionsList,
        skuList = inAppList,
    )
    )
    val uiState: StateFlow<ProfileViewState> = _uiState.asStateFlow()

    init {

        //Observe Coins
        viewModelScope.launch {
            userRepository.observeCoin.collect { coins ->
                _uiState.update { it.copy(
                    totalCoins = coins,
                    coinTypes = coins.bronzeToCoins()) }
            }
        }

        //Billing DataSource
        billingDataSource.billingError.onEach { error ->
            logcat { "HAS ERRRO ${error}" }
            _uiState.update { it.copy(billingError = error) }
        }.launchIn(viewModelScope)

        //Observe SettingModel
        userRepository.observeSettingModel.onEach { setting ->
            _uiState.update { it.copy(settingModel = setting) }
        }.launchIn(viewModelScope)

        //Observe Lang
        languageRepository.observeSelectedLang.onEach { userLng ->
            _uiState.update { it.copy(userSelectedLang = userLng) }
        }.launchIn(viewModelScope)


        languageRepository.observeDeviceLang.onEach { deviceLang ->
            _uiState.update { it.copy(deviceLang = deviceLang) }
        }.launchIn(viewModelScope)


        userRepository.observeUserData.onEach { userEntity ->
            val score = getScore(userEntity.points)
            _uiState.update {
                it.copy(
                    isUserLogin = userEntity.userid > -1,
                    scoreString = score.second,
                    progressPercent = score.third,
                    levelString = score.first,
                    userNameorEmail = userEntity.name ?: ""
                )
            }
        }.launchIn(viewModelScope)

        //Observe coupon state
        userRepository.showCoupons.onEach { showCoupon ->
            _uiState.update { it.copy(showCoupon = showCoupon) }
        }.launchIn(viewModelScope)

    }

    fun updateSettingModel(settingModel: SettingModel) {
        viewModelScope.launch {
            lengoPreference.updateSetting(settingModel.copy(isSync = false))
        }
    }

    fun getScore(currentPoints: Long): Triple<String, String, Float> {
        val pointMap = lengoDataSource.getLevelPointMap()
        var currentLevelString = ""
        var totalpoints = 0
        var currentLevel = 0
        run loop@{
            lengoDataSource.getLevelPointsKeys().forEach {
                val step = pointMap[it]
                if (currentPoints < step!!) {
                    currentLevel = it
                    currentLevelString = "${context.getString(R.string.level)} ${it}"
                    return@loop
                }
            }
        }
        val highestPoints = pointMap[lengoDataSource.getLevelPointsKeys().last()]!!
        if(currentLevel == 0 && currentPoints > highestPoints) {
            currentLevel = lengoDataSource.getLevelPointsKeys().last()
        }

        totalpoints = pointMap.getOrDefault(currentLevel, -1)
        val percent = if(currentPoints > highestPoints) 1f else (currentPoints / totalpoints.toFloat()) * 1f
        logcat("SCORE") { "$currentPoints/$totalpoints percentage ${percent}" }
        return Triple(currentLevelString, "$currentPoints/$totalpoints", percent)

    }
}