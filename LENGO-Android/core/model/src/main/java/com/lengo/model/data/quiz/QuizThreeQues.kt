package com.lengo.model.data.quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.lengo.model.data.ObjParam

@Stable
data class QuizThreeQues(
    override val objParam: ObjParam,
    override val question: String = "",
    val options: List<StringWithTran> = emptyList(),
    val correctAnswers: List<StringWithTran> = emptyList(),
    val correctAnswersWithPlaceHolder: StringWithTran = StringWithTran(objId =  -1),
    val isGram: Boolean = false,
    override val pointEarn: MutableState<Int> = mutableStateOf(-2),
    override val isPointSubmitted: MutableState<Boolean> = mutableStateOf(false),
    override val answerSubmittedState: MutableState<AnswerSubmittedState> = mutableStateOf(
        AnswerSubmittedState()
    )
) : Quiz {

    @Stable
    val option1ColorState: State<ColorState>
        get() = derivedStateOf {
            getColorState(correctAnswers,answerSubmittedState.value.isAnswered,
                answerSubmittedState.value.answerSubmitted,options[0].text)
        }

    @Stable
    val option2ColorState: State<ColorState>
        get() = derivedStateOf {
            getColorState(correctAnswers,answerSubmittedState.value.isAnswered,
                answerSubmittedState.value.answerSubmitted,options[1].text)
        }

    @Stable
    val option3ColorState: State<ColorState>
        get() = derivedStateOf {
            getColorState(correctAnswers,answerSubmittedState.value.isAnswered,
                answerSubmittedState.value.answerSubmitted,options[2].text)
        }
}