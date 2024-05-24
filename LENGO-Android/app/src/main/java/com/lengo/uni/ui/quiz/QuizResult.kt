package com.lengo.uni.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.AnimatedCircle
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.VerticleSpaceWithNav
import com.lengo.common.ui.WordItem
import com.lengo.common.ui.theme.Green
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoCaption
import com.lengo.common.ui.theme.LengoHeading2
import com.lengo.common.ui.theme.LengoHeading4
import com.lengo.common.ui.theme.LengoSubHeading
import com.lengo.common.ui.theme.Orange
import com.lengo.common.ui.theme.Red
import com.lengo.common.ui.theme.lightGrey
import com.lengo.common.ui.theme.translucentBarAlpha
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.ResultState
import com.lengo.model.data.quiz.Word

@Composable
fun QuizResult(
    resultState: ResultState,
    wordList: List<Word>,
    scoreCounter: Int,
    quizCallback: QuizCallback
) {
    val updatedCallback by rememberUpdatedState(quizCallback)
    val scrollState = rememberScrollState()
    val weakWords = remember { wordList.filter { it.color == Red } }
    val mediumWords = remember { wordList.filter { it.color == Orange } }
    val strongWords = remember { wordList.filter { it.color == Green } }
    val newWords = remember { wordList.filter { it.color == lightGrey } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
                .verticalScroll(scrollState)
        ) {
            ScoreCard(
                resultState.highScore,
                resultState.score,
                scoreCounter,
                resultState.percentage
            )
            if (weakWords.isNotEmpty()) {
                WordList(R.string.weak_words, weakWords, quizCallback = updatedCallback)
            }
            if (mediumWords.isNotEmpty()) {
                WordList(R.string.medium_words, mediumWords, quizCallback = updatedCallback)
            }
            if (strongWords.isNotEmpty()) {
                WordList(R.string.strong_words, strongWords, quizCallback = updatedCallback)
            }
            if (newWords.isNotEmpty()) {
                WordList(R.string.LNew, newWords, quizCallback = updatedCallback)
            }
            VerticleSpaceWithNav(100.dp)
        }
        QuizBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            quizCallback = updatedCallback
        )
    }
}

@Composable
fun ScoreCard(
    highScore: Long,
    result: String = "",
    scoreCounter: Int = 0,
    percentage: String = ""
) {


    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuizProgress(modifier = Modifier.size(180.dp), percentage, result)
            Text(stringResource(id = getResultRemark(percentage)), style = LengoHeading4())
            Text("HighScore: ${highScore}", style = LengoSubHeading())
            Text("score: ${scoreCounter}", style = LengoSubHeading())
        }
    }
}

fun getResultRemark(percentage: String): Int {
    val percent = (percentage.toFloat() / 100)
    return if (percent >= 0.9 && percent < 1.1) {
        R.string.SehrGutT
    } else if (percent >= 0.7 && percent < 0.9) {
        R.string.GutT
    } else if (percent >= 0.5 && percent < 0.7) {
        R.string.GehtBesserT
    } else if (percent >= 0.3 && percent < 0.5) {
        R.string.UbenT
    } else if (percent >= 0.0 && percent < 0.3) {
        R.string.VerfehltT
    } else {
        R.string.GutT
    }
}


@Composable
fun WordList(
    title: Int,
    wordList: List<Word>,
    quizCallback: QuizCallback
) {
    val updatedCallback by rememberUpdatedState(quizCallback)
    Column {
        Text(
            stringResource(title).uppercase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, bottom = 8.dp, end = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp)
            ) {
                wordList.forEachIndexed { index, obj ->
                    WordItem(
                        obj.deviceLngWord,
                        obj.selectedLngWord,
                        obj.selectedLngWordTransliterator,
                        false,
                        obj.isGram,
                        obj.color,
                        onSpeak = { txt, isAdd ->
                            updatedCallback.onSpeak(
                                ObjParam(
                                    obj.obj,
                                    obj.pck,
                                    obj.lec,
                                    obj.type,
                                    obj.owner
                                ), txt, isAdd
                            )
                        }
                    )
                    if (index != wordList.size - 1) {
                        Divider(color = MaterialTheme.colors.surface, thickness = 2.dp)
                    }
                }
            }
        }
    }
}


@Composable
fun QuizProgress(
    modifier: Modifier = Modifier,
    percentage: String = "0",
    result: String = ""
) {
    Box(modifier = modifier) {
        AnimatedCircle(
            listOf(1.0f, (percentage.toFloat() / 100)),
            listOf(
                MaterialTheme.colors.surface,
                MaterialTheme.colors.primary,
                MaterialTheme.colors.primaryVariant
            ),
            isGradient = true,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "${percentage.toFloat().toInt()}%",
                style = LengoHeading2(),
                textAlign = TextAlign.Center
            )
            Text(result, style = LengoSubHeading(), textAlign = TextAlign.Center)
        }

    }
}


@Composable
fun QuizBottomBar(
    modifier: Modifier = Modifier,
    quizCallback: QuizCallback
) {
    val updatedCallback by rememberUpdatedState(quizCallback)
    var isLoading by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background.copy(alpha = translucentBarAlpha()))
    ) {

        VerticleSpace(16.dp)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            onClick = {
                updatedCallback.onNextExcise()
                isLoading = true
            },
        ) {
            Box {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.next),
                    style = LengoButtonText().copy(textAlign = TextAlign.Center),
                    maxLines = 1
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(20.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }

        }

        VerticleSpace(8.dp)

        TextButton(
            onClick = { updatedCallback.onRepeatClick() },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.repeat), style = LengoButtonText()
                    .copy(color = MaterialTheme.colors.primary)
            )
        }


    }
}


@Preview
@Composable
fun QuizResultDemo() {
    LENGOTheme {
        //QuizResult(0L,emptyList(), "", 0, "")
    }
}

@Preview
@Composable
fun QuizProgressDemo() {
    LENGOTheme {
        QuizProgress(modifier = Modifier.size(180.dp), "70", "2/2")
    }
}