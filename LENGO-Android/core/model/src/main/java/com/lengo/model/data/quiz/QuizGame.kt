package com.lengo.model.data.quiz

import androidx.compose.runtime.*
import com.lengo.model.data.ObjParam
import java.util.*


@Immutable
enum class CurrentTask {
    QUIZTASK, QUIZLISTENING, QUIZSPEAKING, TESTTASK, MEMORYTASK,RESULT
}

@Immutable
data class ResultState(
    val highScore: Long = 0L,
    val score: String = "",
    val percentage: String = "0",
    val perFloat: Int = 0
)
@Stable
interface Game {
    val objParam: ObjParam
    val question: String
    val pointEarn: MutableState<Int>
    val isPointSubmitted: MutableState<Boolean>
    val answerSubmittedState: MutableState<AnswerSubmittedState>
}

@Stable
interface QuizCallback {
    fun onBack()
    fun onRepeatClick()
    fun onNextExcise()
    fun takeUserReview()
    fun onSpeak(objParam: ObjParam, text: String, addToQueque: Boolean)
    fun processAns(obj: ObjParam, point: Int, correctAns: String, isSpeech: Boolean)
    fun submitAns(
        game: Game,
        ans: String,
        isTakeHint: Boolean = false,
        isGrammer: Boolean = false,
        correctAns: String? = null,
        correctAnsWithPlaceHolder: String? = null,
        correctAnswers: List<StringWithTran>? = null,
        isSpeech: Boolean = true
    )

    fun onNextPage(actionFromUser: Boolean = false, pointEarn: Int)
    fun onMoveToNextTask(currentTask: CurrentTask)
    fun onResultScreen()
    fun onCompleteTimer()
    fun onSwithDisable()
    fun resetPendingNextPage()
    fun startTimeStamp()
    fun endTimeStamp()
}

@Stable
interface Quiz : Game
@Stable
interface Test : Game
@Stable
interface Memo : Game
@Stable
interface Result : Game
@Stable
interface Listening : Game
@Stable
interface Speaking : Game

@Immutable
data class StringWithTran(
    val text: String = "aa",
    val tranText: String = "aa",
    val objId: Long,
    val generatedWord: Boolean = false,
)

@Immutable
data class AnswerSubmittedState(
    val isAnswered: Boolean = false,
    val isTakeHint: Boolean = false,
    val answerSubmitted: String = ""
)

@Immutable
enum class ColorState {
    RED, GREEN, YELLOW, DEFAULT
}


fun getColorState(
    correctAnswers: List<StringWithTran> = emptyList(),
    isAnswered: Boolean,
    userAnswer: String,
    option: String
): ColorState {
    if (isAnswered) {
        val answersList = correctAnswers.map { it.text }
        return if (answersList.contains(option)) {
            ColorState.GREEN
        } else {
            if (userAnswer == option) {
                val isCorrect = answersList.contains(userAnswer)
                if (!isCorrect) {
                    ColorState.RED
                } else {
                    ColorState.DEFAULT
                }
            } else {
                ColorState.DEFAULT
            }
        }
    } else {
        return ColorState.DEFAULT
    }
}

@Stable
data class CharItem(
    val text: Char,
    val tranText: String = "",
    val isExpaded: MutableState<Boolean> = mutableStateOf(false)
)

@Stable
data class WordItem(
    val text: String,
    val tranText: String = "",
    val isExpaded: MutableState<Boolean> = mutableStateOf(false)
)




//TODO: https://stackoverflow.com/questions/2344320/comparing-strings-with-tolerance
@Stable
fun isCorrectAnswer(orginalAns: String, userAnswer: String): Boolean {
    return orginalAns == userAnswer || orginalAns.tolerantTxt() == userAnswer.tolerantTxt()
}




fun String.tolerantTxt(): String {
    val stripParanthesized = this.replace("\\(.*\\)".toRegex(),"")
    val remChars = setOf(',','.','!','¡','?','¿','_','\'','´','’','‘','“','”','\"','–','`',':',';',';','„','=','。','"')
    val filterString = stripParanthesized.filter { !remChars.contains(it) }
    val trimWhitSpace = filterString.replace("\\s".toRegex(), "").lowercase(Locale.getDefault())
    return trimWhitSpace
}

fun main() {
    val stripParanthesized = "The   man (Works)  In !Dubia!.".replace("\\(.*\\)".toRegex(),"")
    print(stripParanthesized)
}