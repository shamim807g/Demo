package com.lengo.uni.ui.quiz

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.di.ApplicationScope
import com.lengo.common.extension.removeAngleBraces
import com.lengo.data.datasource.SoundManager
import com.lengo.data.datasource.TextToSpeechSpeaker
import com.lengo.data.repository.ProgressRepository
import com.lengo.data.repository.QuizRepository
import com.lengo.data.repository.UserRepository
import com.lengo.data.repository.WordsRepository
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.model.data.LectionId
import com.lengo.model.data.ObjParam
import com.lengo.model.data.SettingModel
import com.lengo.model.data.quiz.AnswerSubmittedState
import com.lengo.model.data.quiz.CurrentTask
import com.lengo.model.data.quiz.Game
import com.lengo.model.data.quiz.Listening
import com.lengo.model.data.quiz.Memo
import com.lengo.model.data.quiz.Quiz
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.QuizListening
import com.lengo.model.data.quiz.QuizSpeaking
import com.lengo.model.data.quiz.ResultState
import com.lengo.model.data.quiz.Speaking
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.Test
import com.lengo.model.data.quiz.Word
import com.lengo.model.data.quiz.isCorrectAnswer
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Stable
data class QuizState constructor(
    val quizImage: SnapshotStateMap<String, String?> = mutableStateMapOf(),
    var gamesList: ImmutableList<Game> = persistentListOf(),
    var settingModel: SettingModel = SettingModel(),
    val selectedLang: String = DEFAULT_SEL_LANG,
    val wordList: ImmutableList<Word> = persistentListOf(),
    var currentTask: CurrentTask = CurrentTask.MEMORYTASK,
    val scoreState: ScoreState = ScoreState(),
    val scoreCounter: Int = 0,
    val currentPage: Int = 0,
    val previousCorrectAnswers: Int = 0,
    val isCounterRunningFor2x: Boolean = false,
    val isCounterRunningFor4x: Boolean = false,
    val timeForCounter: Int = 0,
    val isRefreshCounter: Int = 0,
    val isSpeechComplete: Boolean = false,
    val highScore: Long = 0L,
    val moveToNextPagePending: Boolean = false,
    val resultState: ResultState = ResultState()
) {

    companion object {
        val Empty = QuizState()
    }
}

@Immutable
data class ScoreState(
    val offSetEnable: Boolean = true,
    val alphaState: Boolean = true,
    val isPlusShowing: Boolean = false,
    val isMinusShowing: Boolean = false,
    val pointToAddOrMinus: String = "",
)

@Immutable
sealed class QuizEvents {
    object LangDownload : QuizEvents()
    data class PackReview(val lectionId: LectionId) : QuizEvents()
    object onBack : QuizEvents()
    data class OnRestartQuiz(val lectionId: LectionId) : QuizEvents()
    data class OnRestartQuizForMyWords(val wordColor: Int) : QuizEvents()
}


@HiltViewModel
class QuizViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    private val wordsRepository: WordsRepository,
    private val userRepository: UserRepository,
    private val speaker: TextToSpeechSpeaker,
    private val packsDao: PacksDao,
    private val userDoa: UserDoa,
    private val soundManager: SoundManager,
    private val lengoPreference: LengoPreference,
    private val progressRepository: ProgressRepository
) : ViewModel(), QuizCallback {


    private val wordColor: Int = savedStateHandle.get<String>("wordColor")!!.toInt()
    private val lectionName: String = URLDecoder.decode(
        savedStateHandle["lectionName"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private val packName: String = URLDecoder.decode(
        savedStateHandle["packName"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private val packId: Long = savedStateHandle.get<String>("pck")!!.toLong()
    private val lectionID: Long = savedStateHandle.get<String>("lec")!!.toLong()
    private val owner: Long = savedStateHandle.get<String>("owner")!!.toLong()
    private val type: String = savedStateHandle.get<String>("type")!!
    private val lang: String = savedStateHandle.get<String>("lang")!!


    private val _uiState = MutableStateFlow(
        QuizState(wordList = quizRepository.words, quizImage = quizRepository.quizImage)
    )
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private val quizChannel = Channel<QuizEvents>()
    val quizEvents = quizChannel.receiveAsFlow()

    init {
        //Observe GameList
        prepareGameList()

        //Observe Seleted Lang
        viewModelScope.launch {
            userRepository.observeUserEntitySelAndDevice.collect { lng ->
                _uiState.update { it.copy(selectedLang = lng.sel) }
            }
        }

        //Observe HighScore
        viewModelScope.launch {
            userRepository.observeHighScore.collect { highScore ->
                _uiState.update { it.copy(highScore = highScore) }
            }
        }

        //Observe SettingModel
        viewModelScope.launch {
            userRepository.observeSettingModel.collect { settingModel ->
                _uiState.update { it.copy(settingModel = settingModel) }
            }
        }

    }

    private fun prepareGameList() {
        viewModelScope.launch {
            val gameList = quizRepository.prepareQuizSync()
            if (gameList.isNotEmpty()) {
                val currentTask = when (gameList.first()) {
                    is Quiz -> CurrentTask.QUIZTASK
                    is QuizListening -> CurrentTask.QUIZLISTENING
                    is QuizSpeaking -> CurrentTask.QUIZSPEAKING
                    is Test -> CurrentTask.TESTTASK
                    is Memo -> CurrentTask.MEMORYTASK
                    else -> CurrentTask.QUIZTASK
                }
                _uiState.update { it.copy(gamesList = gameList, currentTask = currentTask) }
            } else {
                quizChannel.send(QuizEvents.onBack)
            }
        }
    }


    override fun onBack() {
        viewModelScope.launch {
            quizChannel.send(QuizEvents.onBack)
        }
    }

    override fun onRepeatClick() {
        resetAndRestartQuiz()
    }

    fun resetAndRestartQuiz() {
        _uiState.update {
            QuizState(
                wordList = quizRepository.words,
                quizImage = quizRepository.quizImage,
                settingModel = _uiState.value.settingModel,
                selectedLang = _uiState.value.selectedLang,
                highScore = _uiState.value.highScore,
            )
        }
        prepareGameList()
    }

    override fun onNextExcise() {
        viewModelScope.launch {
            if (wordColor != 0) {
                wordsRepository.userWords(wordColor).collect {
                    if (!it.isNullOrEmpty()) {
                        quizRepository.submitWords(it)
                        resetAndRestartQuiz()
                    } else {
                        quizChannel.send(QuizEvents.onBack)
                    }
                }
            } else {
                val words = wordsRepository.getWords(
                    lectionID = lectionID,
                    owner = owner,
                    type = type,
                    lang = lang,
                    packId = packId
                )
                if (!words.isNullOrEmpty()) {
                    quizRepository.submitWords(words)
                    resetAndRestartQuiz()
                } else {
                    quizChannel.send(QuizEvents.onBack)
                }
            }
        }
    }

    override fun takeUserReview() {
        viewModelScope.launch {
            if (!lengoPreference.isReviewSubmitted() && lengoPreference.getPackReviewRating() == 0f) {
                if (wordColor == 0) {
                    quizChannel.send(
                        QuizEvents.PackReview(
                            LectionId(
                                lectionName,
                                packId,
                                packName,
                                lectionID,
                                owner,
                                type,
                                lang
                            )
                        )
                    )
                }
            }
        }
    }

    override fun onSpeak(objParam: ObjParam, text: String, addToQueque: Boolean) {
        viewModelScope.launch {
            speaker.speak(
                text = text,
                isAddedToQueue = addToQueque,
                onSpeechComplete = null
            ) {
                viewModelScope.launch {
                    quizChannel.send(QuizEvents.LangDownload)
                }
            }
        }
    }

    override fun processAns(obj: ObjParam, point: Int, correctAns: String, isSpeech: Boolean) {
        var finalPoints = 0
        var isCounterRun2x = false
        var isCounterRun4x = false
        var newCorrectAnsCounter = 0
        var timer = 0
        if (point >= 1) {
            newCorrectAnsCounter = uiState.value.previousCorrectAnswers + 1
            if (newCorrectAnsCounter in 2..3) {
                timer = ((2 * 14000 * point) / 2)
                isCounterRun2x = true
            } else if (newCorrectAnsCounter >= 4) {
                timer = ((2 * 14000 * point) / 4)
                isCounterRun4x = true
            }

            finalPoints = when {
                uiState.value.isCounterRunningFor2x || newCorrectAnsCounter in 2..3 -> {
                    point * 2
                }
                uiState.value.isCounterRunningFor4x || newCorrectAnsCounter >= 4 -> {
                    point * 4
                }
                else -> {
                    point
                }
            }

        } else {
            finalPoints = point
        }

        playVoiceForSubmitAnsPoints(finalPoints)
        updateScroe(obj, point)
        val newScore = uiState.value.scoreCounter + finalPoints
        _uiState.update {
            it.copy(
                scoreState = ScoreState(
                    offSetEnable = false,
                    alphaState = false,
                    isPlusShowing = finalPoints > 0,
                    isMinusShowing = finalPoints < 0,
                    pointToAddOrMinus = if (finalPoints > 0) "+${finalPoints}" else "${finalPoints}"
                ),
                scoreCounter = if (newScore < 0) 0 else newScore,
                previousCorrectAnswers = newCorrectAnsCounter,
                isCounterRunningFor2x = isCounterRun2x,
                isCounterRunningFor4x = isCounterRun4x,
                timeForCounter = timer,
                isRefreshCounter = uiState.value.isRefreshCounter + 1
            )
        }

        viewModelScope.launch {
            progressRepository.updateEditPack(point, obj.type, uiState.value.selectedLang)
        }

        if (isSpeech) {
            viewModelScope.launch {
                delay(500)
                speaker.speak(
                    text = correctAns,
                    isAddedToQueue = true,
                    isPronounceEnable = uiState.value.settingModel.pronounceEnable,
                    onSpeechComplete = {
                        onNextPage(actionFromUser = false, pointEarn = point)
                    }
                ) {

                }
            }
        }
    }

    private fun playVoiceForSubmitAnsPoints(finalPoints: Int) {
        if (uiState.value.settingModel.audioEnable) {
            when {
                finalPoints == 1 -> {
                    soundManager.playCorrect1()
                }

                finalPoints in 2..3 -> {
                    soundManager.playCorrect2()
                }

                finalPoints >= 4 -> {
                    soundManager.playCorrect4()
                }

                finalPoints == -1 -> {
                    soundManager.playWrong()
                }
            }
        }
    }

    override fun submitAns(
        game: Game,
        ans: String,
        isTakeHint: Boolean,
        isGrammer: Boolean,
        correctAns: String?,
        correctAnsWithPlaceHolder: String?,
        correctAnswers: List<StringWithTran>?,
        isSpeech: Boolean
    ) {
        game.answerSubmittedState.value = AnswerSubmittedState(
            isAnswered = true,
            answerSubmitted = ans,
            isTakeHint = isTakeHint
        )
        var pointEarn = -2
        if (correctAnswers != null) {
            pointEarn = if (game.answerSubmittedState.value.isAnswered) {
                val answersList = correctAnswers.map { it.text }
                if (answersList.contains(game.answerSubmittedState.value.answerSubmitted) && game.answerSubmittedState.value.isTakeHint) {
                    0
                } else if (answersList.contains(game.answerSubmittedState.value.answerSubmitted) && !game.answerSubmittedState.value.isTakeHint) {
                    1
                } else {
                    -1
                }
            } else {
                -2
            }
        } else if (correctAns != null) {
            if (game.answerSubmittedState.value.isAnswered) {
                if (!isGrammer) {
                    pointEarn =
                        if (isCorrectAnswer(
                                correctAns,
                                game.answerSubmittedState.value.answerSubmitted
                            )
                        && !game.answerSubmittedState.value.isTakeHint
                    ) {
                        1
                    } else if (isCorrectAnswer(
                                correctAns,
                                game.answerSubmittedState.value.answerSubmitted
                            )
                        && game.answerSubmittedState.value.isTakeHint
                    ) {
                        0
                    } else {
                        -1
                    }
                } else if (correctAnsWithPlaceHolder != null) {
                    pointEarn = if (
                        isCorrectAnswer(
                            correctAns,
                            game.answerSubmittedState.value.answerSubmitted
                        )
                        && !game.answerSubmittedState.value.isTakeHint
                    ) {
                        1
                    } else if (
                        isCorrectAnswer(
                            correctAns,
                            game.answerSubmittedState.value.answerSubmitted
                        )
                        && game.answerSubmittedState.value.isTakeHint
                    ) {
                        0
                    } else {
                        -1
                    }
                }
            }
        }
        game.pointEarn.value = pointEarn
        if (pointEarn != -2 && !game.isPointSubmitted.value) {
            if (correctAnswers != null) {
                processAns(game.objParam, game.pointEarn.value, correctAnswers[0].text, isSpeech)
            } else if (correctAns != null && correctAnsWithPlaceHolder != null) {
                processAns(
                    game.objParam,
                    game.pointEarn.value,
                    if (isGrammer) correctAnsWithPlaceHolder.removeAngleBraces() else correctAns,
                    isSpeech
                )
            }
            game.isPointSubmitted.value = true
        }

    }

    fun updateScroe(obj: ObjParam, point: Int) {
        viewModelScope.launch {
            packsDao.increaseOrDecreaseScroe(
                obj,
                point,
                uiState.value.selectedLang
            )
        }
    }

    override fun onNextPage(actionFromUser: Boolean, pointEarn: Int) {
        if (actionFromUser || pointEarn >= 1) {
            val currentPage = _uiState.value.currentPage
            if(currentPage + 1 < _uiState.value.gamesList.size) {
                val game = _uiState.value.gamesList[currentPage]
                if(game.answerSubmittedState.value.isAnswered) {
                    _uiState.update { it.copy(currentPage = currentPage + 1) }
                }
            }
        }

    }


    fun updateResult(): ResultState {
        val list = uiState.value.gamesList.filter { (it is Test || it is Quiz || it is Listening || it is Speaking) && (it.pointEarn.value == 1 || it.pointEarn.value == -1) }
        val totalItem = list.size
        val earnPoint = list.filter { it.pointEarn.value == 1 }.size

        val score = "${earnPoint}/${totalItem}"
        val perFloat = ((earnPoint.toFloat() / totalItem.toFloat()) * 100).toInt()
        val percentage = if (totalItem != 0) {
            "$perFloat"
        } else {
            "0"
        }
        val highScore = if(uiState.value.highScore <= 0) {
            uiState.value.scoreCounter.toLong()
        } else {
            if(uiState.value.scoreCounter.toLong() > uiState.value.highScore) {
                uiState.value.scoreCounter.toLong()
            } else { uiState.value.highScore }
        }
        return ResultState(highScore = highScore, score, percentage, perFloat)
    }

    fun addPoints(point: Long) {
        appScope.launch {
            userDoa.addNewPoints(point)
        }
    }

    fun updateNewHighScore(point: Long) {
        viewModelScope.launch {
            userDoa.updateCurrentHighScore(point)
        }
    }

    override fun onMoveToNextTask(currentTask: CurrentTask) {
        if (uiState.value.currentTask != currentTask) {
            _uiState.update { it.copy(currentTask = currentTask) }
            if(currentTask == CurrentTask.RESULT) {
               onResultScreen()
               takeUserReview()
            }
        }
    }

    override fun onResultScreen() {
        _uiState.update {
            it.copy(
                isCounterRunningFor4x = false,
                isCounterRunningFor2x = false,
                timeForCounter = 0,
                previousCorrectAnswers = 0,
                resultState = updateResult()
            )
        }
        updateNewHighScore(uiState.value.scoreCounter.toLong())
    }

    override fun onCompleteTimer() {
        _uiState.update {
            it.copy(
                isCounterRunningFor4x = false,
                isCounterRunningFor2x = false,
                timeForCounter = 0,
                previousCorrectAnswers = 0
            )
        }
    }

    override fun onSwithDisable() {
        _uiState.update {
            it.copy(scoreState = ScoreState(true, true, isPlusShowing = false))
        }
    }

    override fun resetPendingNextPage() {
        _uiState.update {
            it.copy(moveToNextPagePending = false)
        }
    }

    override fun startTimeStamp() {
        savedStateHandle["timestamp"] = System.currentTimeMillis()
    }

    override fun endTimeStamp() {
        val startTimeStamp = savedStateHandle.get<Long>("timestamp") ?: 0L
        if (startTimeStamp > 0L) {
            val diff = System.currentTimeMillis() - startTimeStamp
            val secounds = TimeUnit.MILLISECONDS.toSeconds(diff)
            appScope.launch {
                progressRepository.updateSecounds(secounds, uiState.value.selectedLang)
            }
            Log.d("TAG", "endTimeStamp: ${secounds}")
        }
    }

//    fun loadQuiz(
//        wordColor: Int,
//        lectionName: String,
//        packName: String,
//        pck: Long,
//        lec: Long,
//        owner: Long,
//        type: String,
//        lang: String
//    ) {
//        this.wordColor = wordColor
//        this.lectionName = lectionName
//        this.packName = packName
//        this.packId = pck
//        this.lectionID = lec
//        this.owner = owner
//        this.type = type
//        this.lang = lang
//    }

}