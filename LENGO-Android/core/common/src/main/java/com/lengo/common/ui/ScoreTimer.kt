package com.lengo.common.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lengo.common.ui.theme.Green
import com.lengo.common.ui.theme.LengoSemiBold16
import com.lengo.model.data.quiz.QuizCallback


@Composable
fun BoxScope.ScoreTimerBox(isCurrentlyRunning2x: Boolean = false,
                            isCurrentlyRunning4x: Boolean = false,
                            isRefreshCounter: Int = 0,
                            timeForCounter: Int = 0,
                            quizCallback: QuizCallback
) {

    val updatedCallback by rememberUpdatedState(quizCallback)

    val transX = with(LocalDensity.current) { -60.dp.toPx() }

    Box(modifier = Modifier
        .size(32.dp)
        .align(Alignment.CenterEnd)
        .graphicsLayer {
            translationX = transX
        }, contentAlignment = Alignment.Center
    ) {
        ScoreTimer(
            modifier = Modifier.fillMaxSize(),
            isRefreshCounter,
            timeForCounter,
            listOf(Green,MaterialTheme.colors.background),
            quizCallback = updatedCallback,
        )
        Text(
            text = if(isCurrentlyRunning2x) "2x" else "4x",
            textAlign = TextAlign.Center,
            color = Green,
            style = LengoSemiBold16(),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun ScoreTimer(
    modifier: Modifier = Modifier,
    isRefreshCounter: Int = 0,
    timeForCounter: Int = 0,
    colors: List<Color> = emptyList(),
    stroke: Dp = 4.dp,
    quizCallback: QuizCallback
) {
    val updatedCallback by rememberUpdatedState(quizCallback)
    val currentState = remember(isRefreshCounter) {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val transition = updateTransition(currentState, label = "")
    val stroke = with(LocalDensity.current) { Stroke(stroke.toPx(),cap = StrokeCap.Round) }

    if(transition.currentState == AnimatedCircleProgress.END) {
        updatedCallback.onCompleteTimer()
    }

    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 0,
                durationMillis = timeForCounter,
                easing = LinearEasing
            )
        }, label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }

    Canvas(modifier) {
        val innerRadius = (size.minDimension) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = 0f - 90f

        drawArc(
            color = Green,
            startAngle = 90f,
            sweepAngle =  360f,
            topLeft = topLeft,
            size = size,
            useCenter = false,
            style = stroke
        )
        val sweep = angleOffset
        drawArc(
            color = colors[1],
            startAngle = 90f,
            sweepAngle =  sweep,
            topLeft = topLeft,
            size = size,
            useCenter = false,
            style = stroke
        )

//        proportions.forEachIndexed { index, proportion ->
//            val sweep = proportion * angleOffset
//            drawArc(
//                color = colors[index],
//                startAngle = 90f,
//                sweepAngle = sweep,
//                topLeft = topLeft,
//                size = size,
//                useCenter = false,
//                style = stroke
//            )
//            startAngle += sweep
//        }
    }
}


