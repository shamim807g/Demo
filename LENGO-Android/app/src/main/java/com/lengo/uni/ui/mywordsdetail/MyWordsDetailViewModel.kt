package com.lengo.uni.ui.mywordsdetail

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.MoleculeViewModel
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.QuizRepository
import com.lengo.data.repository.UserRepository
import com.lengo.data.repository.WordsRepository
import com.lengo.model.data.quiz.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject


sealed interface MyWordsDetailViewEvent {
    data class onSpeak(val word: Word, val text: String, val addToQueque: Boolean) : MyWordsDetailViewEvent
    data object onPrepareQuiz: MyWordsDetailViewEvent
    data class onWordSeleted(val word: Word): MyWordsDetailViewEvent
    data class DownloadSheetState(val visible: Boolean = false): MyWordsDetailViewEvent
    data class QuizSettingSheetState(val visible: Boolean = false): MyWordsDetailViewEvent
}



@Stable
data class MyWordsDetailViewState(
    val words: List<Word>? = null,
    val wordTitle: String = "",
    val wordsColor: Int = -1,
    val isQuizButtonEnable: Boolean = false,
    val isLangDownloadSheetVisible: Boolean = false,
    val isQuizSettingSheetVisible: Boolean = false,
    val selectedLang: String = DEFAULT_SEL_LANG,
) {
    companion object {
        val Empty = MyWordsDetailViewState()
    }
}
@HiltViewModel
class MyWordsDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val wordsRepository: WordsRepository,
    private val languageRepository: LanguageRepository,
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
    private val textToSpeechSpeaker: TextToSpeechSpeaker,
) : MoleculeViewModel<MyWordsDetailViewEvent, MyWordsDetailViewState>() {

    private var wordName: String = URLDecoder.decode(
        savedStateHandle["name"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private var wordColor: Int = savedStateHandle.get<String>("word_color")!!.toInt()

    fun onSpeak(word: Word, text: String, addToQueque: Boolean) {
        viewModelScope.launch {
            textToSpeechSpeaker.speak(
                text = text,
                isAddedToQueue = addToQueque,
                onSpeechComplete = null,
            ) {
                take(MyWordsDetailViewEvent.DownloadSheetState(true))
            }
        }
    }

    fun onPrepareQuiz() {
        viewModelScope.launch {
            quizRepository.submitWords(models.value.words ?: emptyList())
        }
    }

    @Composable
    override fun models(events: Flow<MyWordsDetailViewEvent>): MyWordsDetailViewState {
        var userWords: List<Word> by remember { mutableStateOf(emptyList()) }
        val userSelAndDeviceLang by userRepository.observeUserEntitySelAndDevice.collectAsState(null)
        var isLangDownload by remember { mutableStateOf(false) }
        var isQuizSetting by remember { mutableStateOf(false) }
        var selectedWord: Word? by remember { mutableStateOf(null) }
        var isQuizButtonEnable by remember { mutableStateOf(false) }


        LaunchedEffect(Unit) {
            wordsRepository.userWords(wordColor).collectLatest {
                userWords = it
                isQuizButtonEnable = !userWords.isNullOrEmpty()
            }
        }

        LaunchedEffect(selectedWord) {
            if(selectedWord != null) {
                val wordList = userWords.map {
                    if (it.obj == selectedWord!!.obj
                        && it.lec == selectedWord!!.lec
                        && it.owner == selectedWord!!.owner
                        && it.type == selectedWord!!.type
                        && it.pck == selectedWord!!.pck
                    ) {
                        it.copy(isChecked = !selectedWord!!.isChecked)
                    } else {
                        it
                    }
                }
                userWords = wordList
                val isFound = userWords?.find { it.isChecked }
                isQuizButtonEnable = isFound != null
            }
        }



        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is MyWordsDetailViewEvent.DownloadSheetState -> {
                        isLangDownload = event.visible
                    }
                    MyWordsDetailViewEvent.onPrepareQuiz -> {
                        onPrepareQuiz()
                    }
                    is MyWordsDetailViewEvent.onSpeak -> {
                        onSpeak(event.word,event.text,event.addToQueque)
                    }
                    is MyWordsDetailViewEvent.onWordSeleted -> {
                        selectedWord = event.word
                    }
                    is MyWordsDetailViewEvent.QuizSettingSheetState -> {
                        isQuizSetting = event.visible
                    }
                }
            }
        }


        return MyWordsDetailViewState(
             words = userWords,
             wordTitle = wordName,
             wordsColor = wordColor,
             isLangDownloadSheetVisible = isLangDownload,
             isQuizButtonEnable  = isQuizButtonEnable,
             isQuizSettingSheetVisible = isQuizSetting,
             selectedLang = userSelAndDeviceLang?.sel ?: DEFAULT_SEL_LANG,
        )
    }

}

