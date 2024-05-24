package com.lengo.model.data.quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.lengo.model.data.ObjParam

@Stable
data class Memorize(
    override val objParam: ObjParam,
    override val question: String = "",
    val icons: List<Int> = emptyList(),
    val correctAnsText: StringWithTran = StringWithTran(objId =  -1),
    val correctAnsWithPlaceHolder: StringWithTran = StringWithTran(objId =  -1),
    val isGram: Boolean = false,
    override val pointEarn: MutableState<Int> = mutableStateOf(-2),
    override val isPointSubmitted: MutableState<Boolean> = mutableStateOf(false),
    override val answerSubmittedState: MutableState<AnswerSubmittedState> = mutableStateOf(
        AnswerSubmittedState()
    )
) : Memo