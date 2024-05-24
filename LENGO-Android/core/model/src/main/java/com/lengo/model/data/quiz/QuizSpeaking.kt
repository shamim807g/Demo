package com.lengo.model.data.quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.lengo.model.data.ObjParam


@Stable
data class QuizSpeaking(
    override val objParam: ObjParam,
    override val question: String = "",
    val correctAnsText: StringWithTran = StringWithTran(objId =  -1),
    val correctAnsWithPlaceHolder: StringWithTran = StringWithTran(objId =  -1),
    val isGram: Boolean = false,
    override val pointEarn: MutableState<Int> = mutableStateOf(-2),
    override val isPointSubmitted: MutableState<Boolean> = mutableStateOf(false),
    override val answerSubmittedState: MutableState<AnswerSubmittedState>
    = mutableStateOf(AnswerSubmittedState())
) : Speaking {

    @Stable
    inline val fieldColorState: State<ColorState>
        get() = derivedStateOf {
            if (answerSubmittedState.value.isAnswered) {
                if(!isGram) {
                    if (isCorrectAnswer(correctAnsText.text,
                            answerSubmittedState.value.answerSubmitted)
                        && !answerSubmittedState.value.isTakeHint
                    ) {
                        ColorState.GREEN
                    } else if (isCorrectAnswer(correctAnsText.text,
                            answerSubmittedState.value.answerSubmitted)
                        && answerSubmittedState.value.isTakeHint
                    ) {
                        ColorState.YELLOW
                    } else {
                        ColorState.RED
                    }
                } else {
                    if (
                        isCorrectAnswer(correctAnsWithPlaceHolder.text.removeAngleBraces(),
                            answerSubmittedState.value.answerSubmitted)
                        && !answerSubmittedState.value.isTakeHint
                    ) {
                        ColorState.GREEN
                    } else if (
                        isCorrectAnswer(correctAnsWithPlaceHolder.text.removeAngleBraces(),
                            answerSubmittedState.value.answerSubmitted)
                        && answerSubmittedState.value.isTakeHint
                    ) {
                        ColorState.YELLOW
                    } else {
                        ColorState.RED
                    }
                }
            } else {
                ColorState.DEFAULT
            }
        }


}

fun String.removeAngleBraces(): String {
    return this.replace("<","").replace(">","")
}