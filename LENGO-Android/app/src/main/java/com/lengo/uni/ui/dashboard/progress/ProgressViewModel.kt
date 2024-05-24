package com.lengo.uni.ui.dashboard.progress

import android.content.Context
import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.extension.getCurrentHour
import com.lengo.common.ui.graph.bar.BarChartData
import com.lengo.common.ui.line.data.ChartColors
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.ProgressRepository
import com.lengo.data.repository.ScoreModel
import com.lengo.model.data.Achievements
import com.lengo.model.data.Lang
import com.lengo.model.data.WeekModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.lengo.common.ui.line.data.LineChartData
import com.lengo.data.repository.RankingRepository
import com.lengo.model.data.Ranking
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ProgressViewAction {

}

@Stable
data class ProgressViewState(
    val weekList: List<WeekModel> = emptyList(),
    val userRankList: List<Ranking> = emptyList(),
    val userFullRankList: List<Ranking> = emptyList(),
    val topRankList: List<Ranking> = emptyList(),
    val topFullRankList: List<Ranking> = emptyList(),
    val wordsChartModel: ProgressRepository.LineChartModel? = null,
    val wordAvg: Long = 0L,
    val youTabSelected: Boolean = true,
    val minChartData: BarChartData? = null,
    val minAverage: Long = 0L,
    val expressionChartModel: ProgressRepository.LineChartModel? = null,
    val expressionAverage: Long = 0L,
    val scoreModel: ScoreModel = ScoreModel(),
    val achivementsList: List<Achievements>? = null,
    val lang: Lang? = null,
    val streakPercentage: String = "0",
    val streakProgress: Float = 0.0f
) {

    companion object {
        val Empty = ProgressViewState()
    }
}

@Immutable
sealed class ProgressEvents {
    object LangUpdated : ProgressEvents()
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val languageRepository: LanguageRepository,
    private val progressRepository: ProgressRepository,
    private val rankingRepository: RankingRepository,
) : ViewModel(), ProgressViewAction {

    private val _uiState = MutableStateFlow(ProgressViewState())
    val uiState: StateFlow<ProgressViewState> = _uiState.asStateFlow()

    val Channel = Channel<ProgressEvents>()
    val event = Channel.receiveAsFlow()


    init {
        Log.d("PROGRSS VIEWMODEL", "INIT: ")
        //Observe Lang
        viewModelScope.launch {
            languageRepository.observeSelectedLang.collect { userLng ->
                _uiState.update { it.copy(lang = userLng) }
                Channel.send(ProgressEvents.LangUpdated)
                getUserRankingList(true)
                getTopRankingList(true)
            }
        }
    }

    fun getData() {
        //Get Week data
        viewModelScope.launch {
            val list = progressRepository.getWeekData()
            _uiState.update { it.copy(weekList = list) }
        }
        //Streak
        viewModelScope.launch {
            val streak = progressRepository.getTotalStreak()
            val hourRemaining = getCurrentHour() / 24f
            _uiState.update {
                it.copy(
                    streakPercentage = streak.toString(),
                    streakProgress = hourRemaining
                )
            }
        }

        //Graph Data
        viewModelScope.launch {
            val pair = progressRepository.getWordsCharData()
            _uiState.update { it.copy(wordsChartModel = pair.second, wordAvg = pair.first) }
        }
        viewModelScope.launch {
            val pair = progressRepository.getExpressionChartData()
            _uiState.update {
                it.copy(
                    expressionChartModel = pair.second,
                    expressionAverage = pair.first
                )
            }
        }
        viewModelScope.launch {
            val pair = progressRepository.getMinChartData()
            _uiState.update { it.copy(minChartData = pair.second, minAverage = pair.first) }
        }


        //Score Data
        viewModelScope.launch {
            val model = progressRepository.getScoreModel()
            _uiState.update { it.copy(scoreModel = model) }
        }

        viewModelScope.launch {
            val list = progressRepository.getAchivementsData()
            _uiState.update { it.copy(achivementsList = list) }
        }

        viewModelScope.launch {
             rankingRepository.userRankingSmallList.onEach { rankList ->
                _uiState.update { it.copy(userRankList = rankList) }
            }.collect()
        }

        viewModelScope.launch {
            rankingRepository.userRankingFullList.onEach { rankList ->
                _uiState.update { it.copy(userFullRankList = rankList) }
            }.collect()
        }

        viewModelScope.launch {
            rankingRepository.topRankingSmallList.onEach { rankList ->
                _uiState.update { it.copy(topRankList = rankList) }
            }.collect()
        }

        viewModelScope.launch {
            rankingRepository.topRankingFullList.onEach { rankList ->
                _uiState.update { it.copy(topFullRankList = rankList) }
            }.collect()
        }

        getUserRankingList(false)
        getTopRankingList(false)

    }

    fun getUserRankingList(refreshData: Boolean = false) {
        viewModelScope.launch {
            rankingRepository.getUserRankingList(refreshData)
        }
    }

    fun getTopRankingList(refreshData: Boolean = false) {
        viewModelScope.launch {
            rankingRepository.getTopRankingList(refreshData)
        }
    }

    fun tabSelected(youTabSelected: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(youTabSelected = youTabSelected) }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("PROGRSS VIEWMODEL", "onCleared: ")
    }

}