package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.extension.extractAfterAngleBracket
import com.lengo.common.extension.extractBeforeAngleBracket
import com.lengo.common.extension.extractBetweenAngleBracket
import com.lengo.common.extension.removeAngleBraces
import com.lengo.common.ui.SoundItem2
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.AnswerSubmittedState
import com.lengo.model.data.quiz.Memo
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.common.ui.theme.Grey
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoSubHeading3
import com.lengo.model.data.quiz.Memorize

@ExperimentalComposeUiApi
@Composable
fun MemorizeTask(
    game: Memorize,
    quizCallback: QuizCallback
) {

    val updatedCallback by rememberUpdatedState(quizCallback)
    val icons = remember { mutableStateOf(game.icons) }

    var rotated by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {

        MemoCard(
            modifier = Modifier
                .padding(20.dp)
                .padding(bottom = 10.dp)
                .weight(1f),
            icons.value,
            game.isGram, game.question,
            if (game.isGram) game.correctAnsWithPlaceHolder else game.correctAnsText, rotated,
            onSpeak = { word, isAdded ->
                updatedCallback.onSpeak(game.objParam,word,isAdded)
            }
        ) {
            rotated = it
            icons.value = icons.value.reversed()
        }

        Button(
            modifier = Modifier
                .navigationBarsPadding().imePadding()
                .padding(16.dp)
                .fillMaxWidth()
                .requiredHeight(52.dp)
                .clip(RoundedCornerShape(10.dp)),
            onClick = {
                updatedCallback.submitAns(
                    game = game,
                    isTakeHint = true,
                    ans = if(!game.isGram) game.correctAnsText.text else game.correctAnsWithPlaceHolder.text.removeAngleBraces(),
                    isGrammer = game.isGram,
                    correctAns = game.correctAnsText.text,
                    correctAnsWithPlaceHolder = game.correctAnsWithPlaceHolder.text,
                    isSpeech = false
                )
                updatedCallback.onNextPage(true, 0)
            },
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = MaterialTheme.colors.surface,
                disabledContentColor = MaterialTheme.colors.secondary,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary
            ),
        ) {
            Text(
                text = stringResource(R.string.next),
                style = LengoButtonText().copy(textAlign = TextAlign.Center),
                maxLines = 1
            )
        }
    }


}

enum class BoxState { Front, Back }

private class TransitionData(
    color: State<Color>,
    rotation: State<Float>,
    animateFront: State<Float>,
    animateBack: State<Float>
) {
    val rotation by rotation
    val animateFront by animateFront
    val animateBack by animateBack
}

@Composable
fun MemoCard(
    modifier: Modifier = Modifier,
    icons: List<Int>,
    isGram: Boolean,
    frontText: String,
    backText: StringWithTran,
    rotated: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    onRotate: (Boolean) -> Unit,
) {

    val transitionData = updateTransitionData(
        if (rotated) BoxState.Back else BoxState.Front
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = transitionData.rotation
                cameraDistance = 40.dp.value
            }
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onRotate(!rotated)
            },
        elevation = 2.dp
    )
    {
        Box(
            Modifier.fillMaxSize()
        ) {

            val annotatedBackString: AnnotatedString?
            val annotatedFrontString: AnnotatedString?

            if (!isGram) {

                annotatedFrontString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W600
                        )
                    ) {
                        append(frontText)
                    }
                }

                annotatedBackString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W600
                        )
                    ) {
                        append(backText.text)
                    }
                    if (backText.tranText.isNotEmpty()) {
                        append("\n")
                        withStyle(
                            style = SpanStyle(
                                fontSize = 38.sp,
                                letterSpacing = 0.25.sp,
                                fontWeight = FontWeight.W400
                            )
                        ) {
                            append(backText.tranText)
                        }
                    }
                }


            } else {

                annotatedFrontString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W600
                        )
                    ) {
                        append(frontText.extractBeforeAngleBracket())
                        withStyle(style = SpanStyle(color = Grey)) {
                            append(frontText.extractBetweenAngleBracket())
                        }
                        append(frontText.extractAfterAngleBracket())
                    }
                }

                val primaryColor = MaterialTheme.colors.primary
                annotatedBackString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W600
                        )
                    ) {
                        append(backText.text.extractBeforeAngleBracket())
                        withStyle(style = SpanStyle(color = primaryColor)) {
                            append(backText.text.extractBetweenAngleBracket())
                        }
                        append(backText.text.extractAfterAngleBracket())
                        if (!backText.tranText.isEmpty()) {
                            append("\n")
                            append(backText.tranText.extractBeforeAngleBracket())
                            withStyle(style = SpanStyle(color = primaryColor)) {
                                append(backText.tranText.extractBetweenAngleBracket())
                            }
                            append(backText.tranText.extractAfterAngleBracket())
                        }
                    }
                }

            }

            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha =
                            if (rotated) transitionData.animateBack else transitionData.animateFront
                        rotationY = -transitionData.rotation
                    }
                    .align(Alignment.Center),
                text = if (rotated) annotatedBackString else annotatedFrontString,
                color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center
            )

            SoundItem2(modifier = Modifier
                .padding(16.dp)
                .align(if (rotated) Alignment.TopStart else Alignment.TopEnd)
                .graphicsLayer {
                    alpha = if (rotated) transitionData.animateBack else 0f
                    rotationY = transitionData.rotation
                }, size = 45.dp
            )
            {
                if (isGram) {
                    onSpeak(backText.text.extractBetweenAngleBracket(), true)
                } else {
                    onSpeak(backText.text, true)
                }
            }

            if (!isGram) {
                ChipStack(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(if (rotated) Alignment.TopEnd else Alignment.TopStart)
                        .graphicsLayer {
                            alpha =
                                if (rotated) transitionData.animateBack else transitionData.animateFront
                            rotationY = transitionData.rotation
                        }, icons, rotated
                )
            }

            Text(
                text = stringResource(id = R.string.flipL),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha =
                            if (rotated) transitionData.animateBack else transitionData.animateFront
                        rotationY = -transitionData.rotation
                    }
                    .align(Alignment.BottomCenter),

                color = MaterialTheme.colors.secondary,
                textAlign = TextAlign.Center,
                style = LengoSubHeading3(),
            )

        }

    }
}

@Composable
private fun updateTransitionData(boxState: BoxState): TransitionData {
    val transition = updateTransition(boxState, label = "")
    val color = transition.animateColor(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            BoxState.Front -> Color.Blue
            BoxState.Back -> Color.Red
        }
    }
    val rotation = transition.animateFloat(
        transitionSpec = {
            tween(500)
        },
        label = ""
    ) { state ->
        when (state) {
            BoxState.Front -> 0f
            BoxState.Back -> 180f
        }
    }

    val animateFront = transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 500)
        },
        label = ""
    ) { state ->
        when (state) {
            BoxState.Front -> 1f
            BoxState.Back -> 0f
        }
    }
    val animateBack = transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 500)
        },
        label = ""
    ) { state ->
        when (state) {
            BoxState.Front -> 0f
            BoxState.Back -> 1f
        }
    }

    return remember(transition) { TransitionData(color, rotation, animateFront, animateBack) }
}


@Composable
fun ChipStack(modifier: Modifier = Modifier, icons: List<Int>, isRotate: Boolean) {
    val size = 38.dp
    val sizeModifier = Modifier.size(size)
    val width = (size / 2) * (icons.size + 1)
    Box(
        modifier = modifier
            .size(width, size)
            .graphicsLayer {
                alpha = 0.99f // slight alpha to force compositing layer
            },
    ) {
        var offset = 0.dp
        for (icon in icons) {
            Chip(strokeWidth = 10.0f, sizeModifier.offset(offset)) {
                Image(painter = painterResource(id = icon), contentDescription = null)
            }
            offset += size / 2
        }
    }
}

@Composable
fun Chip(
    strokeWidth: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val stroke = remember(strokeWidth) {
        Stroke(width = strokeWidth)
    }
    Box(modifier = modifier
        .drawWithContent {
            drawContent()
            drawCircle(
                Color.Black,
                size.minDimension / 2,
                size.center,
                style = stroke,
                blendMode = BlendMode.Clear
            )
        }
        .graphicsLayer {
            clip = true
            shape = CircleShape
        }
    ) {
        content()
    }
}
