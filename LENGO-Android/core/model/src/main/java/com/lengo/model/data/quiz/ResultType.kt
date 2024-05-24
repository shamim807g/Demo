package com.lengo.model.data.quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.lengo.model.data.ObjParam

@Stable
data class ResultType(
    override val objParam: ObjParam = ObjParam(-1,-1,-1,"-1",-1),
    override val question: String = "",
    override val pointEarn: MutableState<Int> = mutableStateOf(-2),
) : Result {
    override val isPointSubmitted: MutableState<Boolean>
        get() = mutableStateOf(false)
    override val answerSubmittedState: MutableState<AnswerSubmittedState>
        get() = mutableStateOf(AnswerSubmittedState())
}