package com.lengo.uni.ui.quiz

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.extension.extractAfterAngleBracket
import com.lengo.common.extension.extractBeforeAngleBracket
import com.lengo.common.extension.extractBetweenAngleBracket
import com.lengo.common.ui.AutoResizeText
import com.lengo.common.ui.FontSizeRange
import com.lengo.common.ui.ScoreTimerBox
import com.lengo.model.data.LectionId
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.CurrentTask
import com.lengo.model.data.quiz.Game
import com.lengo.model.data.quiz.Memo
import com.lengo.model.data.quiz.Quiz
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.ResultState
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.Test
import com.lengo.model.data.quiz.Word
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.sheet.DownloadLangModelSheet
import com.lengo.uni.ui.sheet.PackReviewModalSheet
import com.lengo.common.ui.theme.*
import com.lengo.model.data.quiz.Memorize
import com.lengo.model.data.quiz.QuizChars
import com.lengo.model.data.quiz.QuizFourQues
import com.lengo.model.data.quiz.QuizListening
import com.lengo.model.data.quiz.QuizSpeaking
import com.lengo.model.data.quiz.QuizThreeQues
import com.lengo.model.data.quiz.ResultType
import com.lengo.model.data.quiz.TestType
import com.lengo.uni.utils.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(
    ExperimentalComposeUiApi::class,
    InternalCoroutinesApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun QuizMain() {
    val controller = LocalNavigator.current
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<QuizViewModel>()
    val quizState: QuizState by viewModel.uiState.collectAsState()
    var isLangDownload by remember { mutableStateOf(false) }
    var isPackReview by remember { mutableStateOf(false) }
    var isPackReviewData: LectionId? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.quizEvents.collect { event ->
            when (event) {
                QuizEvents.LangDownload -> {
                    isLangDownload = true
                }
                is QuizEvents.PackReview -> {
                    isPackReview = true
                    isPackReviewData = event.lectionId
                }
                QuizEvents.onBack -> {
                    controller.popBackStack()
                }
                is QuizEvents.OnRestartQuiz -> {

                }
                is QuizEvents.OnRestartQuizForMyWords -> {

                }
            }
        }
    })

    DisposableEffect(key1 = Unit, effect = {
        Log.d("TAG", "QuizMain: start")
        viewModel.startTimeStamp()
        onDispose {
            viewModel.endTimeStamp()
            viewModel.addPoints(quizState.scoreCounter.toLong())
            Log.d("TAG", "QuizMain: end")
        }
    })


    QuizMainContent(
        quizState,
        quizState.wordList,
        quizState.quizImage,
        quizState.selectedLang,
        quizCallback = viewModel
    )

    PackReviewModalSheet(isPackReviewData,isPackReview) {
        isPackReview = false
    }

    DownloadLangModelSheet(isLangDownload) {
        isLangDownload = false
    }

}

@ExperimentalComposeUiApi
@InternalCoroutinesApi
@FlowPreview
@ExperimentalMaterialApi
@Composable
internal fun QuizMainContent(
    quizState: QuizState,
    wordList: ImmutableList<Word>,
    imageMap: SnapshotStateMap<String, String?>,
    selectedLang: String,
    quizCallback: QuizCallback,
) {

    val updatedCallback by rememberUpdatedState(quizCallback)

    Column(Modifier.fillMaxSize()) {
        QuizAppBar(
            quizState.currentTask,
            quizState.gamesList,
            score = quizState.scoreCounter.toString(),
            scoreState = quizState.scoreState,
            isCurrentlyRunning2x = quizState.isCounterRunningFor2x,
            isCurrentlyRunning4x = quizState.isCounterRunningFor4x,
            timeForCounter = quizState.timeForCounter,
            isRefreshCounter = quizState.isRefreshCounter,
            quizCallback = updatedCallback
        )
        if (!quizState.gamesList.isEmpty()) {
            QuizPager(
                quizState.resultState,
                quizState.gamesList.getOrNull(quizState.currentPage),
                wordList,
                quizState.scoreCounter,
                imageMap,
                selectedLang,
                quizCallback = updatedCallback
            )
        }
    }
}

@ExperimentalComposeUiApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun QuizPager(
    resultState: ResultState,
    quizItem: Game? = null,
    words: ImmutableList<Word>,
    scoreCounter: Int,
    quizImage: SnapshotStateMap<String, String?>,
    selLang: String,
    quizCallback: QuizCallback,
) {

    val updatedCallback by rememberUpdatedState(quizCallback)
    val visiblePage: MutableState<ObjParam?> = remember { mutableStateOf(null) }

    if (quizItem != null) {
        when (quizItem) {
            is QuizFourQues -> {
                updatedCallback.onMoveToNextTask(CurrentTask.QUIZTASK)
                QuizFourOptions(
                    quizItem,
                    quizImage,
                    selLang,
                    quizCallback = updatedCallback
                )
            }
            is QuizThreeQues -> {
                updatedCallback.onMoveToNextTask(CurrentTask.QUIZTASK)
                QuizThreeQues(
                    quizItem,
                    quizCallback = updatedCallback
                )
            }
            is QuizChars -> {
                updatedCallback.onMoveToNextTask(CurrentTask.QUIZTASK)
                QuizCharOption(
                    quizItem,
                    quizCallback = updatedCallback
                )
            }
            is QuizListening -> {
                updatedCallback.onMoveToNextTask(CurrentTask.QUIZLISTENING)
                QuizListening(
                    quizItem,
                    visiblePage.value,
                    quizCallback = updatedCallback
                )
            }
            is QuizSpeaking -> {
                updatedCallback.onMoveToNextTask(CurrentTask.QUIZSPEAKING)
                QuizSpeaking(
                    quizItem,
                    selLang,
                    quizCallback = updatedCallback
                )
            }
            is Memorize -> {
                updatedCallback.onMoveToNextTask(CurrentTask.MEMORYTASK)
                MemorizeTask(
                    quizItem,
                    quizCallback = updatedCallback
                )
            }
            is TestType -> {
                updatedCallback.onMoveToNextTask(CurrentTask.TESTTASK)
                Test(
                    quizItem,
                    visiblePage.value,
                    quizCallback = updatedCallback
                )
            }
            is ResultType -> {
                updatedCallback.onMoveToNextTask(CurrentTask.RESULT)
                QuizResult(
                    resultState,
                    words,
                    scoreCounter,
                    quizCallback = updatedCallback
                )
            }
        }
    }
}

@Composable
fun QuizQuesText(question: String = "Some Question") {

    AutoResizeText(
        text = question,
        maxLines = 3,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        fontSizeRange = FontSizeRange(
            min = 22.sp,
            max = 24.sp,
        ),
        style = LengoHeading5()
            .copy(color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center),
    )

//    Text(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
//        text = question, style = LengoHeading2()
//            .copy(color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center)
//    )
}

@Composable
fun QuizQuesGramText(
    question: String = "Some Question",
    isAnswered: Boolean,
    answer: StringWithTran
) {
    if (isAnswered) {
        val annotatedSecoundString = buildAnnotatedString {
            append(answer.text.extractBeforeAngleBracket())
            withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                append(answer.text.extractBetweenAngleBracket())
            }
            append(answer.text.extractAfterAngleBracket())
            if (!answer.tranText.isNullOrEmpty()) {
                append("\n")
                append(answer.tranText.extractBeforeAngleBracket())
                withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(answer.tranText.extractBetweenAngleBracket())
                }
                append(answer.tranText.extractAfterAngleBracket())
            }
        }

        AutoResizeText(
            text = annotatedSecoundString.toString(),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            fontSizeRange = FontSizeRange(
                min = 22.sp,
                max = 24.sp,
            ),
            style = LengoHeading5()
                .copy(color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center),
        )


    } else {
        val annotatedSecoundString = buildAnnotatedString {
            append(question.extractBeforeAngleBracket())
            withStyle(style = SpanStyle(color = MaterialTheme.colors.secondary)) {
                append(question.extractBetweenAngleBracket())
            }
            append(question.extractAfterAngleBracket())
        }

        AutoResizeText(
            text = annotatedSecoundString.toString(),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            fontSizeRange = FontSizeRange(
                min = 22.sp,
                max = 24.sp,
            ),
            style = LengoHeading5()
                .copy(color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center),
        )



    }

}


@Composable
fun QuizAnswerOrNextButton(
    isAnswered: Boolean = true,
    onShowAns: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .navigationBarsPadding().imePadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .requiredHeight(52.dp)

    ) {
        if (!isAnswered) {
            TextButton(onClick = onShowAns, Modifier.fillMaxSize().testTag("quiz_answer_button")) {
                Text(
                    text = stringResource(R.string.Antwort), style = LengoButtonText()
                        .copy(color = MaterialTheme.colors.secondary)
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .testTag("quiz_next_button"),
                onClick = onNext,
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
}


@Composable
fun QuizAppBar(
    currentTask: CurrentTask = CurrentTask.QUIZTASK,
    quizList: ImmutableList<Game> = persistentListOf(),
    score: String = "0",
    scoreState: ScoreState = ScoreState(),
    isCurrentlyRunning2x: Boolean = false,
    isCurrentlyRunning4x: Boolean = false,
    timeForCounter: Int = 0,
    isRefreshCounter: Int = 0,
    quizCallback: QuizCallback,
) {
    val updatedCallback by rememberUpdatedState(quizCallback)

    Column {
        Surface(
            color = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
            elevation = 0.dp,
            shape = RectangleShape,
        ) {
            val aalpha: Float by animateFloatAsState(
                targetValue = if (scoreState.alphaState) 1f else 0f,
                animationSpec = tween(
                    delayMillis = 0,
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            )


            val transX = with(LocalDensity.current) { -60.dp.toPx() }

            val offset: Float by animateFloatAsState(
                targetValue = if (scoreState.offSetEnable) transX else 0f,
                finishedListener = {
                    updatedCallback.onSwithDisable()
                }, animationSpec = tween(
                    delayMillis = 0,
                    durationMillis = 600,
                    easing = LinearOutSlowInEasing
                )

                //SpringSpec(Spring.DampingRatioNoBouncy, Spring.StiffnessLow, null)
            )

            val color: Color by animateColorAsState(
                targetValue = when {
                    scoreState.isPlusShowing -> Green
                    scoreState.isMinusShowing -> Red
                    else -> MaterialTheme.colors.onBackground
                },
                animationSpec = tween(durationMillis = 100)
            )


            Box(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars.add(WindowInsets(top = 56.dp, bottom = 56.dp)))
                    .padding(horizontal = 8.dp)
                    .statusBarsPadding(),
            ) {

                val toolbarTitle = when (currentTask) {
                    CurrentTask.QUIZTASK -> stringResource(id = R.string.task_QUIZ)//vvv
                    CurrentTask.TESTTASK -> stringResource(id = R.string.task_TEST)
                    CurrentTask.QUIZLISTENING -> stringResource(id = R.string.task_LISTENING)
                    CurrentTask.QUIZSPEAKING -> stringResource(id = R.string.task_SPEAKING)
                    CurrentTask.MEMORYTASK -> stringResource(id = R.string.task_MEMORIZE)
                    CurrentTask.RESULT -> stringResource(id = R.string.task_QUIZ)
                }

                Text(
                    text = toolbarTitle,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground,
                    style = LengoTopBar(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )


                IconButton(
                    onClick = {
                        updatedCallback.onBack()
                              },
                    modifier = Modifier.align(Alignment.CenterStart).testTag("quiz_close") ,
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    })


                if (isCurrentlyRunning2x || isCurrentlyRunning4x) {
                    ScoreTimerBox(
                        isCurrentlyRunning2x = isCurrentlyRunning2x,
                        isCurrentlyRunning4x = isCurrentlyRunning4x,
                        isRefreshCounter = isRefreshCounter,
                        timeForCounter = timeForCounter,
                        quizCallback = updatedCallback
                    )
                }


                if (scoreState.isPlusShowing || scoreState.isMinusShowing) {

                    Text(
                        text = scoreState.pointToAddOrMinus,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = aalpha
                                translationX = offset
                            }
                            .align(Alignment.CenterEnd),
                        textAlign = TextAlign.Center,
                        color = if (scoreState.isPlusShowing) Green else Red,
                        style = LengoBold20()
                    )
                }


                Text(
                    text = score,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = color,
                    style = LengoHeading2()
                )
            }

        }
        QuizProgress(currentTask, quizList)
    }


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuizProgress(
    currentTask: CurrentTask = CurrentTask.QUIZTASK,
    quizList: ImmutableList<Game> = persistentListOf(),
) {
    val currentProgressList = remember(currentTask, quizList) {
        derivedStateOf {
            when (currentTask) {
                CurrentTask.QUIZTASK -> {
                    quizList.filterIsInstance(Quiz::class.java)
                }
                CurrentTask.QUIZLISTENING -> {
                    quizList.filterIsInstance(QuizListening::class.java)
                }
                CurrentTask.QUIZSPEAKING -> {
                    quizList.filterIsInstance(QuizSpeaking::class.java)
                }
                CurrentTask.TESTTASK -> {
                    quizList.filterIsInstance(Test::class.java)
                }
                CurrentTask.MEMORYTASK -> {
                    quizList.filterIsInstance(Memo::class.java)
                }
                else -> { emptyList() }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        currentProgressList.value.forEach {
            val color = remember(it.pointEarn.value, currentTask) {
                derivedStateOf {
                    when (it.pointEarn.value) {
                        0 -> {
                            Orange
                        }
                        1 -> {
                            Green
                        }
                        -1 -> {
                            Red
                        }
                        else -> {
                            lightGrey
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(color.value, CircleShape)
            )

        }
    }
}

