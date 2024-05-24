package com.lengo.uni.ui.dashboard.my_word

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.WordsRepository
import com.lengo.model.data.Lang
import com.lengo.model.data.quiz.ScoreCard
import com.lengo.model.data.quiz.Word
import com.lengo.model.data.quiz.toScoreCards
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MyWordsViewAction {
    fun onSpeak(word: Word, text: String, addToQueque: Boolean)
}

@Stable
data class MyWordsViewState(
    val words: List<Word> = emptyList(),
    val lang: Lang? = null
) {
    val scoreCard: ScoreCard
        get() = words.toScoreCards()

    companion object {
        val Empty = MyWordsViewState()
    }
}

@Immutable
sealed class MyWordEvents {
    object LangDownload : MyWordEvents()
}

@HiltViewModel
class MyWordsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val wordsRepository: WordsRepository,
    private val textToSpeechSpeaker: TextToSpeechSpeaker,
    private val languageRepository: LanguageRepository,
) : ViewModel(), MyWordsViewAction {

    private val _uiState = MutableStateFlow(MyWordsViewState())
    val uiState: StateFlow<MyWordsViewState> = _uiState.asStateFlow()

    val Channel = Channel<MyWordEvents>()
    val event = Channel.receiveAsFlow()

    init {
        //Observe Words
        viewModelScope.launch {
            wordsRepository.userWords().collect { words ->
                _uiState.update { it.copy(words = words) }
            }
        }

        //Observe Lang
        viewModelScope.launch {
            languageRepository.observeSelectedLang.collect { lang ->
                _uiState.update { it.copy(lang = lang) }
            }
        }

    }

    override fun onSpeak(word: Word, text: String, addToQueque: Boolean) {
        _uiState.value.lang?.let { lang ->
            viewModelScope.launch {
                textToSpeechSpeaker.speak(
                    text = text,
                    isAddedToQueue = addToQueque,
                    onSpeechComplete =  {}
                ) {
                    viewModelScope.launch {
                        Channel.send(MyWordEvents.LangDownload)
                    }
                }
            }
        }
    }

}