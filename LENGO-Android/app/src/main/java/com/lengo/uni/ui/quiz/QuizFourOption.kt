package com.lengo.uni.ui.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lengo.common.PLACEHOLDER
import com.lengo.common.R
import com.lengo.common.images
import com.lengo.common.ui.AutoResizeText
import com.lengo.common.ui.FontSizeRange
import com.lengo.common.ui.theme.Alpha80Black
import com.lengo.common.ui.theme.Alpha80White
import com.lengo.common.ui.theme.Green
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoBold18h4
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.Red
import com.lengo.common.ui.theme.lightGreen
import com.lengo.model.data.ObjParam
import com.lengo.model.data.quiz.ColorState
import com.lengo.model.data.quiz.CurrentTask
import com.lengo.model.data.quiz.Game
import com.lengo.model.data.quiz.QuizCallback
import com.lengo.model.data.quiz.QuizFourQues
import com.lengo.model.data.quiz.StringWithTran
import java.io.File


@ExperimentalMaterialApi
@Composable
fun QuizFourOptions(
    game: QuizFourQues,
    quizImage: SnapshotStateMap<String, String?>,
    selLang: String,
    quizCallback: QuizCallback
) {

    val updatedCallback by rememberUpdatedState(quizCallback)


    Column(Modifier.fillMaxSize()) {

        val randomImage = remember { images.shuffled() }

        QuizQuesText(game.question)

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                QuizCard(
                    Modifier.weight(1f),
                    game.options[0].text,
                    game.options[0].tranText,
                    game.option1ColorState.value,
                    game.answerSubmittedState.value.isAnswered,
                    quizImage["IMG_${game.objParam.owner}_${game.objParam.type}_${game.objParam.pck}_${game.objParam.lec}_${game.options[0].objId}"] ?: PLACEHOLDER,
                    randomImage[0]
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }

                QuizCard(
                    Modifier.weight(1f),
                    game.options[1].text,
                    game.options[1].tranText,
                    game.option2ColorState.value,
                    game.answerSubmittedState.value.isAnswered,
                    quizImage["IMG_${game.objParam.owner}_${game.objParam.type}_${game.objParam.pck}_${game.objParam.lec}_${game.options[1].objId}"] ?: PLACEHOLDER,
                    randomImage[1]
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuizCard(
                    Modifier.weight(1f),
                    game.options[2].text,
                    game.options[2].tranText,
                    game.option3ColorState.value,
                    game.answerSubmittedState.value.isAnswered,
                    quizImage["IMG_${game.objParam.owner}_${game.objParam.type}_${game.objParam.pck}_${game.objParam.lec}_${game.options[2].objId}"] ?: PLACEHOLDER,
                    randomImage[2]
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
                QuizCard(
                    Modifier.weight(1f),
                    game.options[3].text,
                    game.options[3].tranText,
                    game.option4ColorState.value,
                    game.answerSubmittedState.value.isAnswered,
                    quizImage["IMG_${game.objParam.owner}_${game.objParam.type}_${game.objParam.pck}_${game.objParam.lec}_${game.options[3].objId}"] ?: PLACEHOLDER,
                    randomImage[3]
                ) {
                    updatedCallback.submitAns(game = game,ans = it, correctAnswers = game.correctAnswers)
                }
            }
        }
        QuizAnswerOrNextButton(game.answerSubmittedState.value.isAnswered, onShowAns = {
            updatedCallback.submitAns(isTakeHint = true, game = game,ans = game.correctAnswers[0].text, correctAnswers = game.correctAnswers)
        }, onNext = {
            updatedCallback.onNextPage(true,game.pointEarn.value)
        })
    }
}

@ExperimentalMaterialApi
@Composable
fun QuizCard(
    modifier: Modifier,
    option1: String = "",
    option1Tran: String = "",
    colorState: ColorState = ColorState.DEFAULT,
    isAnswered: Boolean = false,
    quizImage: String,
    placeHolderImage: Int = -1,
    onClick: (String) -> Unit = {}
) {

    val isDarkTheme = LocalDarkModeEnable.current

    val cardColor: Color by animateColorAsState(
        when (colorState) {
            ColorState.GREEN -> lightGreen
            ColorState.RED -> Red
            ColorState.DEFAULT -> if (isDarkTheme) Alpha80Black else Alpha80White
            else -> if (isDarkTheme) Alpha80Black else Alpha80White
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


    Box(modifier = modifier
        .clip(RoundedCornerShape(6.dp))
        .clickable {
            if (!isAnswered) {
                onClick(option1)
            }
        }
        .testTag("quiz_option")) {

        val file = remember(key1 = quizImage) { File(quizImage) }
        if(quizImage != PLACEHOLDER) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(file).build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.africa),
                contentScale = ContentScale.Crop
            )
        } else {
            CircularProgressIndicator(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .testTag("quiz_progress"))
        }

            val annotatedString = buildAnnotatedString {
                append(option1)
                if (!option1Tran.isNullOrEmpty()) {
                    append("\n")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W400
                        )
                    ) {
                        append(option1Tran)
                    }
                }
            }

                AutoResizeText(
                    text = annotatedString.text,
                    maxLines = 2,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(cardColor)
                    ,
                    fontSizeRange = FontSizeRange(
                        min = 16.sp,
                        max = 18.sp,
                    ),
                    textAlign = TextAlign.Center,
                    style = LengoBold18h4().copy(color = textColor, textAlign = TextAlign.Center),
                )

//            Text(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .fillMaxWidth()
//                    .background(cardColor)
//                    .padding(16.dp),
//                text = annotatedString,
//                style = LengoBold18h4().copy(color = textColor, textAlign = TextAlign.Center)
//            )
        }
    }

@ExperimentalMaterialApi
@Preview(showSystemUi = true, device = "spec:width=1080px,height=2340px", fontScale = 1.0f)
@Composable
fun QuizCardDemo() {
    LENGOTheme {
        CompositionLocalProvider(LocalDarkModeEnable provides false) {
            QuizFourOptions(
                game = QuizFourQues(
                    objParam = ObjParam(1,2,4,"",3),
                    question = "Some question Some question  Some question ?",
                    options = listOf(
                        StringWithTran("SSasd","asdasda",1,true),
                        StringWithTran("sdfsdfs","sdf",3,true),
                        StringWithTran("sdfsdfs","sdfsdf",4,true),
                        StringWithTran("sdfsdfsdf","sdfsdf",5,true)
                    )
                ),
                quizCallback = object : QuizCallback {
                    override fun onBack() {
                        TODO("Not yet implemented")
                    }

                    override fun onRepeatClick() {
                        TODO("Not yet implemented")
                    }

                    override fun onNextExcise() {
                        TODO("Not yet implemented")
                    }

                    override fun takeUserReview() {
                        TODO("Not yet implemented")
                    }

                    override fun onSpeak(objParam: ObjParam, text: String, addToQueque: Boolean) {
                        TODO("Not yet implemented")
                    }

                    override fun processAns(
                        obj: ObjParam,
                        point: Int,
                        correctAns: String,
                        isSpeech: Boolean
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun submitAns(
                        game: Game,
                        ans: String,
                        isTakeHint: Boolean,
                        isGrammer: Boolean,
                        correctAns: String?,
                        correctAnsWithPlaceHolder: String?,
                        correctAnswers: List<StringWithTran>?,
                        isSpeech: Boolean
                    ) {
                        TODO("Not yet implemented")
                    }
                    override fun onNextPage(actionFromUser: Boolean, pointEarn: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onMoveToNextTask(currentTask: CurrentTask) {
                        TODO("Not yet implemented")
                    }

                    override fun onResultScreen() {
                        TODO("Not yet implemented")
                    }

                    override fun onCompleteTimer() {
                        TODO("Not yet implemented")
                    }

                    override fun onSwithDisable() {
                        TODO("Not yet implemented")
                    }

                    override fun resetPendingNextPage() {
                        TODO("Not yet implemented")
                    }

                    override fun startTimeStamp() {
                        TODO("Not yet implemented")
                    }

                    override fun endTimeStamp() {
                        TODO("Not yet implemented")
                    }

                },
                quizImage = SnapshotStateMap(),
                selLang = ""
            )
        }

    }
}




//@ExperimentalMaterialApi
//@Preview
//@Composable
//fun QuizFourOptionsDemo() {
//    LENGOTheme {
//        QuizFourOptions(obj = { a, b -> }, onNextPage = {})
//    }
//}