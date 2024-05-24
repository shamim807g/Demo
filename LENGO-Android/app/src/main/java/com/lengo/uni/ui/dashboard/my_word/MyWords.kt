package com.lengo.uni.ui.dashboard.my_word

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.GREEN_WORDS
import com.lengo.common.R
import com.lengo.common.RED_WORDS
import com.lengo.common.YELLOW_WORDS
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.AnimatedCircle
import com.lengo.common.ui.HorizontalSpace
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.WordItem
import com.lengo.model.data.quiz.Word
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.Screen
import com.lengo.common.ui.theme.Green
import com.lengo.common.ui.theme.LengoSubHeading
import com.lengo.common.ui.theme.LengoSubHeading3
import com.lengo.common.ui.theme.Orange
import com.lengo.common.ui.theme.Red
import com.lengo.common.ui.theme.WordCardText
import com.lengo.uni.ui.bottomsheet.LangSelectSheet

@Composable
fun MyWords() {
    val context = LocalContext.current
    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    val navigator = LocalNavigator.current
    val viewModel = activity.myWordsViewModel

    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = MyWordsViewState.Empty)
    var isLangSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val columnScrollstate = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(columnScrollstate)
        ) {

            VerticleSpace()

            val state = rememberScrollState()
            Row(modifier = Modifier.horizontalScroll(state)) {
                HorizontalSpace(16.dp)
                WordCard(
                    text = R.string.strong_words,
                    progress = viewState.scoreCard.greenPer,
                    percentage = viewState.scoreCard.greenPercent,
                    totalWords = viewState.scoreCard.greenWords
                ) {
                    navigator.navigate(Screen.MyWordDetail.createRoute(context.getString(R.string.strong_words), GREEN_WORDS))
                }
                HorizontalSpace()
                WordCard(
                    text = R.string.medium_words,
                    color1 = Orange.copy(alpha = 0.5f),
                    color2 = Orange,
                    progress = viewState.scoreCard.yellowPer,
                    percentage = viewState.scoreCard.yellowPercent,
                    totalWords = viewState.scoreCard.yellowWords
                ) {
                    navigator.navigate(Screen.MyWordDetail.createRoute(context.getString(R.string.medium_words), YELLOW_WORDS))
                }
                HorizontalSpace()
                WordCard(
                    text = R.string.weak_words, color1 = Red.copy(alpha = 0.5f), color2 = Red,
                    progress = viewState.scoreCard.redPer,
                    percentage = viewState.scoreCard.redPercent,
                    totalWords = viewState.scoreCard.redWords
                ) {
                    navigator.navigate(Screen.MyWordDetail.createRoute(context.getString(R.string.weak_words), RED_WORDS))
                }
                HorizontalSpace(16.dp)
            }
            VerticleSpace(16.dp)
            WordList(viewState.words, viewModel::onSpeak)

            VerticleSpace(120.dp)
        }
    }

    LangSelectSheet(isLangSheet) {
        isLangSheet = false
    }
}

@Composable
fun WordList(wordList: List<Word>, onSpeak: (Word, String, Boolean) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        wordList.forEachIndexed { index, obj ->
            WordItem(
                obj.deviceLngWord,
                obj.selectedLngWord,
                obj.selectedLngWordTransliterator,
                false,
                obj.isGram,
                obj.color,
                onSpeak = { txt, added -> onSpeak(obj, txt, added) }
            )
        }
    }

}

@Preview
@Composable
fun WordCard(
    text: Int = R.string.strong_words,
    color1: Color = Green.copy(alpha = 0.5f),
    color2: Color = Green,
    progress: Float = 0.5f,
    percentage: String = "100",
    totalWords: Int = 100,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color1,
                            color2,
                            color2
                        )
                    ), shape = RoundedCornerShape(8.dp)
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Progress(
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f), progress, percentage
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${totalWords}",
                    color = Color.White,
                    style = LengoSubHeading().copy(color = MaterialTheme.colors.background),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.woerter),
                    color = Color.White,
                    style = LengoSubHeading3().copy(color = MaterialTheme.colors.background),
                    textAlign = TextAlign.Center
                )
            }
        }
        VerticleSpace()
        Text(
            text = stringResource(text),
            style = LengoSubHeading3().copy(color = MaterialTheme.colors.onBackground),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Progress(modifier: Modifier = Modifier, progress: Float = 0.5f, percentage: String = "100%") {
    Box(modifier = modifier) {
        AnimatedCircle(
            listOf(1.0f, progress),
            listOf(
                Color.White.copy(alpha = 0.2f),
                Color.White
            ),
            modifier = Modifier.fillMaxSize(),
            strokeDp = 8.dp
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            text = "$percentage",
            style = WordCardText().copy(MaterialTheme.colors.background),
            textAlign = TextAlign.Center
        )
    }
}