package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.AnswerSubmittedState
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.Quiz
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.getColorState
import com.lengo.common.ui.theme.*
import com.lengo.model.data.quiz.QuizThreeQues


@Composable
fun QuizThreeQues(
    game: QuizThreeQues,
    quizCallback: QuizCallback,
) {

    val updatedCallback by rememberUpdatedState(quizCallback)

    Column(Modifier.fillMaxSize()) {
        if(game.isGram) {
            QuizQuesGramText(game.question,game.answerSubmittedState.value.isAnswered,game.correctAnswersWithPlaceHolder)
        } else {
            QuizQuesText(game.question)
        }

        Box(
            Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OptionButton(
                    game.options[0].text,
                    game.options[0].tranText,
                    game.option1ColorState.value,
                    game.answerSubmittedState.value.isAnswered
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
                OptionButton(
                    game.options[1].text,
                    game.options[1].tranText,
                    game.option2ColorState.value,
                    game.answerSubmittedState.value.isAnswered
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
                OptionButton(
                    game.options[2].text,
                    game.options[2].tranText,
                    game.option3ColorState.value,
                    game.answerSubmittedState.value.isAnswered
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
            }

        }
        VerticleSpace(16.dp)
        QuizAnswerOrNextButton(game.answerSubmittedState.value.isAnswered,onShowAns = {
            updatedCallback.submitAns(isTakeHint = true, game = game,ans = game.correctAnswers[0].text, correctAnswers = game.correctAnswers)
        },onNext = {
            updatedCallback.onNextPage(true,game.pointEarn.value)
        })
    }
}

@Composable
fun OptionButton(
    optionText: String = "",
    optionTranText: String = "",
    colorState: ColorState = ColorState.DEFAULT,
    isAnswered: Boolean = false,
    onClick: (String) -> Unit = {}
) {

    val cardColor: Color by animateColorAsState(
        when (colorState) {
            ColorState.GREEN -> lightGreen
            ColorState.RED -> Red
            ColorState.DEFAULT -> MaterialTheme.colors.surface
            else -> MaterialTheme.colors.surface
        }
    )

    val textColor: Color by animateColorAsState(
        when (colorState) {
            ColorState.GREEN -> Green
            ColorState.RED -> Color.White
            ColorState.DEFAULT -> MaterialTheme.colors.onBackground
            else -> MaterialTheme.colors.onBackground
        }
    )

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = cardColor).testTag("quiz_option"),
        onClick = {
            if (!isAnswered) {
                onClick(optionText)
            }
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = cardColor,
        )
    ) {
        val annotatedString = buildAnnotatedString {
            append(optionText)
            if(!optionTranText.isNullOrEmpty()) {
                append("\n")
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp,
                        fontWeight = FontWeight.W400
                    )
                ) {
                    append(optionTranText)
                }
            }
        }


        Text(
            modifier = Modifier.padding(6.dp),
            text = annotatedString,
            style = LengoOptionButton().copy(
                color = textColor,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2
        )
    }


}

@Preview
@Composable
fun QuizThreeQuesDemo() {
    LENGOTheme {
        //QuizThreeQues({ a, e -> }, {})
    }
}