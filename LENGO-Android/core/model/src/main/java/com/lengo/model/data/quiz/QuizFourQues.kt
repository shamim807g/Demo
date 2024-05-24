package com.lengo.model.data.quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.lengo.model.data.ObjParam

@Stable
data class QuizFourQues(
    override val objParam: ObjParam,
    override val question: String = "",
    val options: List<StringWithTran> = emptyList(),
    override val isPointSubmitted: MutableState<Boolean> = mutableStateOf(false),
    val correctAnswers: List<StringWithTran> = emptyList(),
    override val pointEarn: MutableState<Int> = mutableStateOf(-2),
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

    @Stable
    val option4ColorState: State<ColorState>
        get() = derivedStateOf {
            getColorState(correctAnswers,answerSubmittedState.value.isAnswered,
                answerSubmittedState.value.answerSubmitted,options[3].text)
        }

}