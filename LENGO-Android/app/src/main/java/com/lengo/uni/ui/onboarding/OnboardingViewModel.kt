package com.lengo.uni.ui.onboarding

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.data.repository.LoginEnableStatus
import com.lengo.data.repository.UserRepository
import com.lengo.uni.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class OnboardingViewState(
    val isLoginOrRegisterEnable: LoginEnableStatus = LoginEnableStatus.LOADING
) {
    companion object {
        val Empty = OnboardingViewState()
    }
}

sealed class OnboardingEvent {

}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
) : BaseViewModel<OnboardingViewState, OnboardingEvent>() {

    private val _uiState = MutableStateFlow(OnboardingViewState.Empty)
    val uiState: StateFlow<OnboardingViewState> = _uiState.asStateFlow()

    override val initialState: OnboardingViewState = OnboardingViewState.Empty

    init {
        viewModelScope.launch(dispatchers) {
            userRepository.isLoginOrRegisterEnable.collect { isLogin ->
                _uiState.update { it.copy(isLoginOrRegisterEnable = isLogin) }
            }
        }
    }

}


