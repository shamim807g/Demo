package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.lengo.common.extension.removeAngleBraces
import com.lengo.common.extension.removeLastChar
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.theme.*
import com.lengo.model.data.quiz.CharItem
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.QuizChars
import com.lengo.model.data.quiz.WordItem

@Composable
fun QuizCharOption(
    game: QuizChars,
    quizCallback: QuizCallback
) {

    val verticalScroll = rememberScrollState()
    val updatedCallback by rememberUpdatedState(quizCallback)

    val answerText = remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)) {

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

        Spacer(modifier = Modifier.height(16.dp))

        CharsBoxes(game.charList, answerText.value.isEmpty(), onCharClick = {
            if (!game.answerSubmittedState.value.isAnswered) {
                answerText.value = answerText.value + it
                if (game.spaceIndexes.contains(answerText.value.length)) {
                    answerText.value = answerText.value + " "
                }
                if (answerText.value.length == game.correctAnsText.text.length) {
                    updatedCallback.submitAns(
                        game = game,
                        ans = answerText.value,
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
                updatedCallback.onNextPage(true, game.pointEarn.value)
            })
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharsBoxes(
    charList: List<CharItem>,
    isUserTextEmpty: Boolean = true,
    onCharClick: (Char) -> Unit = {},
    onRemoveLastChar: () -> Unit = {},
) {
    val charItem: MutableState<MutableList<CharItem>> = remember {
        mutableStateOf(mutableListOf())
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = FlowMainAxisAlignment.Center,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        charList.forEach {
            CharBox(it) {
                charItem.value.add(it)
                it.isExpaded.value = false
                onCharClick(it.text)
            }
        }
        CrossCharBox(isUserTextEmpty) {
            if (charItem.value.size > 0) {
                charItem.value.last().isExpaded.value = true
                charItem.value.removeAt(charItem.value.size - 1)
                onRemoveLastChar()
            }
        }
    }
}

@Composable
fun WordBox(wordItem: WordItem, onClick: () -> Unit) {
    val color: Color by animateColorAsState(
        if (wordItem.isExpaded.value)
            MaterialTheme.colors.onBackground else MaterialTheme.colors.surface
    )
    val scale: Float by animateFloatAsState(if (wordItem.isExpaded.value) 1f else 0.3f)

    TextButton(onClick = {
        if (wordItem.isExpaded.value) {
            onClick()
        }
    }, modifier = Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colors.surface)

    ) {
        val annotatedString = buildAnnotatedString {
            append(wordItem.text)
            if (!wordItem.tranText.isNullOrEmpty()) {
                append("\n")
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp,
                        fontWeight = FontWeight.W400
                    )
                ) {
                    append(wordItem.tranText)
                }
            }
        }


        Text(
            modifier = Modifier.padding(8.dp),
            text = annotatedString,
            style = LengoSemiBold18h4().copy(
                color = color,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2
        )
    }


}

@Composable
fun CharBox(charItem: CharItem, onClick: () -> Unit = {}) {
    val color: Color by animateColorAsState(
        if (charItem.isExpaded.value)
            MaterialTheme.colors.onBackground else MaterialTheme.colors.surface
    )
    val scale: Float by animateFloatAsState(if (charItem.isExpaded.value) 1f else 0.3f)

    TextButton(onClick = {
        if (charItem.isExpaded.value) {
            onClick()
        }
    }, modifier = Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .requiredSize(60.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colors.surface)

    ) {
        val annotatedString = buildAnnotatedString {
            append(charItem.text)
            if (!charItem.tranText.isNullOrEmpty()) {
                append("\n")
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp,
                        fontWeight = FontWeight.W400
                    )
                ) {
                    append(charItem.tranText)
                }
            }
        }


        Text(
            modifier = Modifier,
            text = annotatedString,
            style = LengoSemiBold18h4().copy(
                color = color,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2
        )
    }
}


@Composable
fun CrossCharBox(isUserTextEmpty: Boolean, onClick: () -> Unit = {}) {
    val scale: Float by animateFloatAsState(if (!isUserTextEmpty) 1f else 0f)

    IconButton(onClick = {
        onClick()
    }, modifier = Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .requiredSize(60.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colors.surface)

    ) {
        Icon(
            imageVector = Icons.Filled.Backspace,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
    }
}


@Composable
fun WordBoxes(
    wordList: List<WordItem>,
    isUserTextEmpty: Boolean = true,
    onWordClick: (String) -> Unit = {},
    onRemoveLastChar: () -> Unit = {},
) {
    val charItem: MutableState<MutableList<WordItem>> = remember {
        mutableStateOf(mutableListOf())
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 10.dp,
        mainAxisAlignment = FlowMainAxisAlignment.Center,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        wordList.forEach {
            WordBox(it) {
                charItem.value.add(it)
                it.isExpaded.value = false
                onWordClick(it.text)
            }
        }
        CrossCharBox(isUserTextEmpty) {
            if (charItem.value.size > 0) {
                charItem.value.last().isExpaded.value = true
                charItem.value.removeAt(charItem.value.size - 1)
                onRemoveLastChar()
            }
        }
    }
}

@Preview
@Composable
fun CharBoxesDemo() {
    LENGOTheme {
        CharsBoxes(emptyList(), true)
    }
}

@Preview
@Composable
fun WordBoxesDemo() {
    LENGOTheme {
        WordBoxes(listOf(WordItem("john", "aadsada"), WordItem("Wants", "aa")), true)
    }
}

@Preview(backgroundColor = 0xFF9C27B0)
@Composable
fun CharBoxDemo() {
    LENGOTheme {
        val isExanded = remember {
            mutableStateOf(true)
        }
        CharBox(CharItem('A', "B", isExanded))
    }
}