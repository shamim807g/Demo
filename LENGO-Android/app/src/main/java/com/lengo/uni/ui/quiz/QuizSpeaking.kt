package com.lengo.uni.ui.quiz

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.AnswerSubmittedState
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.Speaking
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.isCorrectAnswer
import com.lengo.model.data.quiz.removeAngleBraces
import com.lengo.common.ui.theme.*
import com.lengo.model.data.quiz.QuizSpeaking

@Composable
fun QuizSpeaking(
    game: QuizSpeaking,
    selLang: String,
    quizCallback: QuizCallback
) {
    val answerText = remember(game.objParam) { mutableStateOf("") }
    val result = remember(game.objParam) { mutableStateOf<String?>(null) }

    val updatedCallback by rememberUpdatedState(quizCallback)

    val launcher = rememberLauncherForActivityResult(SpeechContract()) {
        result.value = it
        if(!it.isNullOrEmpty()) {
            answerText.value = it

            updatedCallback.submitAns(
                game = game,
                ans = answerText.value,
                isGrammer = game.isGram,
                correctAns = game.correctAnsText.text,
                correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
            )
        }
    }

//    if (game.pointEarn.value != -2 && !game.isPointSubmitted.value) {
//        updatedCallback.processAns(
//            game.objParam,
//            game.pointEarn.value,
//            if(game.isGram) game.correctAnsWithPlaceHolder.text.removeAngleBraces() else game.correctAnsText.text,
//            true
//        )
//        game.isPointSubmitted.value = true
//    }




    Column(Modifier.fillMaxSize()) {

        val textBackground: Color by animateColorAsState(
            when (game.fieldColorState.value) {
                ColorState.GREEN -> lightGreen
                ColorState.RED -> Red
                ColorState.YELLOW -> Orange
                ColorState.DEFAULT -> MaterialTheme.colors.surface
            }
        )

        val textColor: Color by animateColorAsState(
            when (game.fieldColorState.value) {
                ColorState.GREEN -> Green
                ColorState.RED -> Color.White
                ColorState.YELLOW -> Orange
                ColorState.DEFAULT -> MaterialTheme.colors.onBackground
            }
        )

        if (game.isGram) {
            QuizQuesGramText(
                game.question,
                game.answerSubmittedState.value.isAnswered,
                game.correctAnsWithPlaceHolder
            )
        } else {
            QuizQuesText(game.question)
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = textBackground)
                .padding(vertical = 14.dp),
            text = answerText.value,
            style = LengoOptionButton().copy(
                color = textColor,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1
        )

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(onClick = { launcher.launch(selLang) },
            modifier = Modifier.fillMaxWidth().wrapContentWidth(CenterHorizontally),
            backgroundColor = MaterialTheme.colors.primary) {
            Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }


        VerticleSpace(16.dp)
        QuizAnswerOrNextButton(game.answerSubmittedState.value.isAnswered,
            onShowAns = {
                updatedCallback.submitAns(
                    game = game,
                    isTakeHint = true,
                    ans = if(!game.isGram) game.correctAnsText.text else game.correctAnsWithPlaceHolder.text.removeAngleBraces(),
                    isGrammer = game.isGram,
                    correctAns = game.correctAnsText.text,
                    correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
                )
            }, onNext = {
                updatedCallback.onNextPage(true,game.pointEarn.value)
            })
    }

}