package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.extension.removeAngleBraces
import com.lengo.common.extension.removeLastChar
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.AnswerSubmittedState
import com.lengo.model.data.quiz.CharItem
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.Listening
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.WordItem
import com.lengo.model.data.quiz.isCorrectAnswer
import com.lengo.common.ui.theme.*
import com.lengo.model.data.quiz.QuizListening
import kotlinx.coroutines.delay

@Composable
fun QuizListening(
    game: QuizListening,
    visiblePage: ObjParam?,
    quizCallback: QuizCallback
) {
    var isAnimating by remember(game) { mutableStateOf(false) }
    val updatedCallback by rememberUpdatedState(quizCallback)
    val answerText = remember(game) { mutableStateOf("") }

    DisposableEffect(game) {
        isAnimating = true
        if (game.isGram) {
            updatedCallback.onSpeak(
                game.objParam,
                game.correctAnsWithPlaceHolder.text,
                true
            )
        } else {
            updatedCallback.onSpeak(game.objParam,game.correctAnsText.text, true)
        }
        onDispose {}
    }

    Column(Modifier.fillMaxSize()) {

        VerticleSpace(16.dp)

        SoundButton(isAnimating,onAnimation = { isAnimating = it }) {
            if (game.isGram) {
                updatedCallback.onSpeak(
                    game.objParam,
                    game.correctAnsWithPlaceHolder.text,
                    true
                )
            } else {
                updatedCallback.onSpeak(game.objParam,game.correctAnsText.text, true)
            }
        }

        VerticleSpace(16.dp)

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

        if (game.answerSubmittedState.value.isAnswered) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = if(!game.isGram)"${game.correctAnsText.text}\n${game.correctAnsText.tranText}" else
                    "${game.correctAnsWithPlaceHolder.text}\n${game.correctAnsWithPlaceHolder.tranText}",
                style = LengoOptionButton().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if(!game.isGram) {
            CharsBoxes(game.charList, answerText.value.isEmpty(), onCharClick = {
                if (!game.answerSubmittedState.value.isAnswered) {
                    answerText.value = answerText.value + it
                    if (game.spaceIndexes.contains(answerText.value.length)) {
                        answerText.value = answerText.value + " "
                    }
                    if (answerText.value.length == game.correctAnsText.text.length) {
                        updatedCallback.submitAns(
                            game = game,
                            ans =  answerText.value,
                            isGrammer = game.isGram,
                            correctAns = game.correctAnsText.text,
                            correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
                        )

                    }
                }

            }, onRemoveLastChar = {
                if (!game.answerSubmittedState.value.isAnswered) {
                    answerText.value = answerText.value.trim()
                    answerText.value = answerText.value.removeLastChar()
                }
            })
        } else {
            WordBoxes(wordList = game.wordList,answerText.value.isEmpty(), onWordClick = {
                if (!game.answerSubmittedState.value.isAnswered) {
                    answerText.value = answerText.value + it + " "
                    if (answerText.value.trim().length == game.correctAnsWithPlaceHolder.text.length) {
                        game.answerSubmittedState.value = AnswerSubmittedState(
                            isAnswered = true,
                            isTakeHint = false,
                            answerSubmitted = answerText.value.trim()
                        )
                    }
                }

            },onRemoveLastChar = {
                if (!game.answerSubmittedState.value.isAnswered) {
                    answerText.value = answerText.value.trim()
                    val lastIndex = answerText.value.lastIndexOf(" ")
                    if(lastIndex != -1) {
                        answerText.value = answerText.value.substring(0, lastIndex + 1)
                    } else {
                        answerText.value = ""
                    }
                }
            })
        }

        VerticleSpace(16.dp)
        QuizAnswerOrNextButton(game.answerSubmittedState.value.isAnswered,
            onShowAns = {
                updatedCallback.submitAns(
                    game = game,
                    ans = if(!game.isGram) game.correctAnsText.text else game.correctAnsWithPlaceHolder.text.removeAngleBraces(),
                    isTakeHint = true,
                    isGrammer = game.isGram,
                    correctAns = game.correctAnsText.text,
                    correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text
                )
            }, onNext = {
                updatedCallback.onNextPage(true, game.pointEarn.value)
            })
    }

}

@Composable
fun SoundButton(isAnimating: Boolean, onAnimation: (Boolean) -> Unit, onSpeak: () -> Unit) {

    LaunchedEffect(key1 = isAnimating, block = {
        delay(2000)
        onAnimation(false)
    })

    IconButton(
        onClick = {
            onAnimation(true)
            onSpeak()
        }, modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .requiredSize(60.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colors.surface)
    ) {

        val infiniteTransition = rememberInfiniteTransition()

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(delayMillis = 100, durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val alpha2 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(delayMillis = 200, durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val alpha3 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(delayMillis = 300, durationMillis = 300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isAnimating) {
                Icon(
                    painter = painterResource(id = R.drawable.sounda),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colors.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.soundb),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha),
                    tint = MaterialTheme.colors.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.soundc),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha2),
                    tint = MaterialTheme.colors.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.soundd),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha3),
                    tint = MaterialTheme.colors.onBackground
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.soundb),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}