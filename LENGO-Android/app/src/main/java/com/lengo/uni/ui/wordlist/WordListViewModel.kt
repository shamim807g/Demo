package com.lengo.uni.ui.wordlist

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.USER_VOCAB
import com.lengo.common.extension.getStringByIdName
import com.lengo.common.extension.getStringId
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.LectionRepository
import com.lengo.data.repository.PacksRepository
import com.lengo.data.repository.QuizRepository
import com.lengo.data.repository.UserRepository
import com.lengo.data.repository.WordsRepository
import com.lengo.model.data.Lection
import com.lengo.model.data.quiz.Word
import com.lengo.preferences.LengoPreference
import com.lengo.uni.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

interface WordListAction {
    fun onSpeak(word: Word, text: String, isAdded: Boolean)
    fun onPrepareQuiz()
    fun onWordSeleted(word: Word)
    fun selWordChange(word: String)
    fun ownWordChange(word: String)
    fun addWord(onUpdate: () -> Unit)
}

@Immutable
data class PackPublicStatus(
    val isPackPublic: Boolean = false,
    val message: Int = com.lengo.common.R.string.pack_not_public,
    val isPackPublicLoading: Boolean = false
)

@Stable
data class WordListViewState(
    val isWordLoading: Boolean = false,
    val isQuizButtonEnable: Boolean = false,
    val isUserVock: Boolean = false,
    val lectionName: String = "",
    val packName: String = "",
    val packPublic: PackPublicStatus = PackPublicStatus(),
    val pacKEmoji: String = "✏️",
    val lection: Lection? = null,
    val packId: Long = 0L,
    val packType: String = "",
    val selectedLang: String = DEFAULT_SEL_LANG,
    val ownLang: String = DEFAULT_OWN_LANG,
    val words: ImmutableList<Word>? = null,
    val selWord: String = "",
    val ownWord: String = "",
    val selLabel: String = "",
    val ownLabel: String = "",
) {
    val addButtonEnable: Boolean
        get() = (!selWord.isNullOrEmpty()) && (!ownWord.isNullOrEmpty())

    companion object {
        val Empty = WordListViewState()
    }
}

sealed class WordListEvents {
    object LangDownload : WordListEvents()
}

@HiltViewModel
class WordListViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val savedStateHandle: SavedStateHandle,
    val userRepository: UserRepository,
    val quizRepository: QuizRepository,
    val wordsRepository: WordsRepository,
    val lectionRepository: LectionRepository,
    val languageRepository: LanguageRepository,
    val packRepository: PacksRepository,
    val speaker: TextToSpeechSpeaker,
    @Dispatcher(LengoDispatchers.IO) val appCoroutineDispatchers: CoroutineDispatcher,
    val lengoPreference: LengoPreference,
) : ViewModel(), WordListAction {

    private val lectionName: String = URLDecoder.decode(savedStateHandle["lectionName"]!!, StandardCharsets.UTF_8.toString())
    private val packName: String = URLDecoder.decode(savedStateHandle["packName"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private val packEmoji: String = URLDecoder.decode(savedStateHandle["packEmoji"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private val packId: Long = savedStateHandle.get<String>("pck")!!.toLong()
    private val lectionID: Long = savedStateHandle.get<String>("lec")!!.toLong()
    private val owner: Long = savedStateHandle.get<String>(key = "owner")!!.toLong()
    private val type: String = savedStateHandle["type"]!!
    private val lang: String = savedStateHandle["lang"]!!


    private val _uiState = MutableStateFlow(
        WordListViewState(
            isWordLoading = true,
            isQuizButtonEnable = true
        )
    )
    val uiState: StateFlow<WordListViewState> = _uiState.asStateFlow()

    private val wordListChannel = Channel<WordListEvents>()
    val wordListEvent = wordListChannel.receiveAsFlow()

    init {

        _uiState.update {
            it.copy(
                isUserVock = type == USER_VOCAB,
                lectionName = lectionName,
                packName = packName,
                pacKEmoji = packEmoji,
                packId = packId,
                packType = type
            )
        }

        //Fetch Lection
        viewModelScope.launch {
            lectionRepository.getLection(packId, lectionID, owner, type, lang)?.let { lec ->
                _uiState.update { it.copy(lection = lec) }
            }
        }


        //Observe Seleted Lang
        viewModelScope.launch {
            userRepository.observeUserEntitySelAndDevice.collect { lng ->
                _uiState.update { it.copy(selectedLang = lng.sel, ownLang = lng.own) }
            }
        }

        //Observe Lang
        viewModelScope.launch {
            languageRepository.observeSelectedLang.collect { lang ->
                _uiState.update {
                    it.copy(
                        selLabel = lang.locale.displayLanguage,
                        ownLabel = context.resources.configuration.locales.get(0).displayLanguage
                    )
                }
            }
        }

        //get pack public status
        viewModelScope.launch {
            val packStatus = packRepository.fetchUserPacksPublicStatus(packId, owner, lang)
                _uiState.update {
                    it.copy(packPublic = PackPublicStatus(
                        packStatus,
                        if(packStatus) com.lengo.common.R.string.pack_accepted_and_public else com.lengo.common.R.string.pack_not_public,
                        false
                    ))
                }
            }

        viewModelScope.launch {
            lengoPreference.incrementUserVisitedWordList()
        }

    }

    fun updatePackPublicStatus(status: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(packPublic = _uiState.value.packPublic.copy(isPackPublicLoading = true))
            }
            delay(3000)
            val response = packRepository.updatePackPublicStatus(packId,lang,status)
            _uiState.update {
                it.copy(packPublic = _uiState.value.packPublic.copy(
                    isPackPublic = response?.accepted ?: false,
                    isPackPublicLoading = false,
                    message = getStringId(context,response?.msg) ?: com.lengo.common.R.string.pack_not_public
                ))
            }
        }
    }

    fun fetchWords() {
        //Observe Words
        viewModelScope.launch {
            val words = wordsRepository.getWords(packId, lectionID, owner, type.replace("-SUGGESTED",""), lang)
            _uiState.update { it.copy(words = words) }
        }
    }

    override fun onSpeak(word: Word, text: String, isAdded: Boolean) {
        viewModelScope.launch {
            speaker.speak(
                text = text,
                isAddedToQueue = isAdded,
                onSpeechComplete = null
            ) {
                viewModelScope.launch {
                    wordListChannel.send(WordListEvents.LangDownload)
                }
            }
        }
    }


    override fun onPrepareQuiz() {
        uiState.value.words?.let {
            quizRepository.submitWords(it)
        }
    }

    fun updatedPackLecName(pName: String,lecName: String) {
        _uiState.update { it.copy(packName = pName, lectionName = lecName) }
    }

    override fun onWordSeleted(word: Word) {
        viewModelScope.launch {
            withContext(appCoroutineDispatchers) {
                val wordList = uiState.value.words?.map {
                    if (it.obj == word.obj
                        && it.lec == word.lec
                        && it.owner == word.owner
                        && it.type == word.type
                        && it.pck == word.pck
                    ) {
                        it.copy(isChecked = !word.isChecked)
                    } else {
                        it
                    }
                }?.toImmutableList()
                _uiState.update { it.copy(words = wordList) }
            }
            updateButtonState()
        }
    }

    fun updateButtonState() {
        viewModelScope.launch {
            val isFound = uiState.value.words?.find { it.isChecked }
            _uiState.update { it.copy(isQuizButtonEnable = isFound != null) }
        }
    }

    override fun selWordChange(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(selWord = word) }
        }
    }

    override fun ownWordChange(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(ownWord = word) }
        }
    }

    override fun addWord(onUpdate: () -> Unit) {
        viewModelScope.launch {
            val ownLang = context.resources.configuration.locales.get(0).language
            wordsRepository.addWord(
                packId,
                lectionID,
                owner,
                type.replace("-SUGGESTED",""),
                lang,
                ownLang,
                uiState.value.ownWord,
                uiState.value.selWord,
            )
            _uiState.update { it.copy(ownWord = "", selWord = "") }
            val words = wordsRepository.getWords(packId, lectionID, owner, type.replace("-SUGGESTED",""), lang)
            _uiState.update { it.copy(words = words) }
            onUpdate()
        }
    }

}