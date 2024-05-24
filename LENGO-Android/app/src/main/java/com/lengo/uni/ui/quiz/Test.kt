package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.ui.theme.*
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.TestType
import com.lengo.model.data.quiz.removeAngleBraces
import kotlinx.coroutines.delay
import logcat.logcat


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Test(
    game: TestType,
    visiblePage: ObjParam?,
    quizCallback: QuizCallback
) {

    val updatedCallback by rememberUpdatedState(quizCallback)
    var answerText by remember(game) { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember(game.objParam) { FocusRequester() }
    var isFocusRemoved by remember { mutableStateOf(false) }


    LaunchedEffect(game.objParam) {
        logcat { "TEST TASK: ${game.objParam}" }
        delay(1000)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

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

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = textBackground)
                .focusRequester(focusRequester),
            value = answerText,
            onValueChange = {
                if (!game.answerSubmittedState.value.isAnswered) {
                    answerText = it
                }
                //onQueryChanged(it)
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = textColor,
                backgroundColor = Color.Transparent,
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            textStyle = LengoOptionButton().copy(
                color = textColor,
                textAlign = TextAlign.Center,
            ),
            keyboardActions = KeyboardActions(onDone = {
                //isFocusRemoved = true
                //focusRequester.freeFocus()
                keyboardController?.hide()
//                game.answerSubmittedState.value = AnswerSubmittedState(
//                    isAnswered = true,
//                    isTakeHint = false,
//                    answerSubmitted = answerText
//                )
                updatedCallback.submitAns(
                    game = game,
                    ans = answerText,
                    isGrammer = game.isGram,
                    correctAns = game.correctAnsText.text,
                    correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
                )
            })
        )

        if (game.answerSubmittedState.value.isAnswered) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "${game.correctAnsText.text}\n${game.correctAnsText.tranText}",
                style = LengoOptionButton().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        QuizAnswerOrNextButton(game.answerSubmittedState.value.isAnswered, onShowAns = {

            updatedCallback.submitAns(
                game = game,
                isTakeHint = true,
                ans = if(!game.isGram) game.correctAnsText.text else game.correctAnsWithPlaceHolder.text.removeAngleBraces(),
                isGrammer = game.isGram,
                correctAns = game.correctAnsText.text,
                correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
            )
//            game.answerSubmittedState.value = AnswerSubmittedState(
//                isAnswered = true,
//                isTakeHint = true,
//                answerSubmitted = game.correctAnsText.text
//            )
//            updatedCallback.onTakeHint()
            //isFocusRemoved = true
            //focusRequester.freeFocus()
            keyboardController?.hide()
        }, onNext = {
            updatedCallback.onNextPage(true,game.pointEarn.value)
        })


    }
}




//@ExperimentalComposeUiApi
//@Preview
//@Composable
//fun TestDemo() {
//    LENGOTheme {
//        Test(
//            TestType(
//            1,
//            "What",
//            "What"
//        ),obj = { a, v -> }, {})
//    }
//}