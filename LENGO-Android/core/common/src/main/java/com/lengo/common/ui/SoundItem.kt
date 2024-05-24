package com.lengo.common.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.theme.LENGOTheme
import kotlinx.coroutines.delay


@Composable
fun SoundItem2(modifier: Modifier = Modifier,size: Dp = 40.dp, onClick:() -> Unit) {

    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isAnimating, block = {
        delay(2000)
        isAnimating = false
    })


    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 100,durationMillis =  600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 200,durationMillis =  500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(delayMillis = 300,durationMillis =  300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        if(isAnimating) {
            Icon(
                painter = painterResource(id = R.drawable.sounda),
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = MaterialTheme.colors.onBackground
            )
            Icon(
                painter = painterResource(id = R.drawable.soundb),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .alpha(alpha),
                tint = MaterialTheme.colors.onBackground
            )
            Icon(
                painter = painterResource(id = R.drawable.soundc),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .alpha(alpha2),
                tint = MaterialTheme.colors.onBackground
            )
            Icon(
                painter = painterResource(id = R.drawable.soundd),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .alpha(alpha3),
                tint = MaterialTheme.colors.onBackground
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.soundb),
                contentDescription = null,
                modifier = Modifier
                    .size(size).clickable {
                       isAnimating = true
                        onClick()
                    },
                tint = MaterialTheme.colors.onBackground
            )
        }
    }

}

@Preview
@Composable
fun SoundItem2Demo() {
    LENGOTheme {
        SoundItem2 {}
    }
}

@Composable
fun SoundItem() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.burkina_faso)
    val infiniteTransition = rememberInfiniteTransition()

    val animationTargetState = remember { mutableStateOf(0f) }

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis =  300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier
            .size(32.dp)
            .clickable {
                animationTargetState.value = 1f
            },
        onDraw = {
            val canvasWidth = size.width
            val canvasHeight = size.height

            //drawImage(imageBitmap)


            withTransform({
                scale(0.9f,0.9f)
            }) {
                drawArc(
                    Color.Black,
                    310f,
                    90f,
                    false,
                    topLeft = Offset(0f, 0f),
                    style = Stroke(width = 10f,cap = StrokeCap.Round)
                )
            }
            withTransform({
                scale(0.6f,0.6f)
            }) {
                drawArc(
                    Color.Black,
                    310f,
                    90f,
                    false,
                    topLeft = Offset(0f, 0f),
                    style = Stroke(width = 10f,cap = StrokeCap.Round)
                )
            }

            withTransform({
                scale(0.3f,0.3f)
            }) {
                drawArc(
                    Color.Black,
                    310f,
                    90f,
                    false,
                    topLeft = Offset(0f, 0f),
                    style = Stroke(width = 18f,cap = StrokeCap.Round)
                )
            }

//            inset(-40F, 0F) {
//                drawArc(
//                    Color.Black,
//                    270f,
//                    180f,
//                    false,
//                    topLeft = Offset(0f, 0f),
//                    style = Stroke(width = 10f, cap = StrokeCap.Round)
//                )
//
//            }
//            drawArc(Color.Black, 290f, 130f,
//                false,topLeft = Offset(20f,10f),size = Size(width = canvasWidth/2f,canvasHeight),style = Stroke(width = 10f,cap = StrokeCap.Round)
//            )
//            drawArc(Color.Black, 290f, 130f,
//                false,topLeft = Offset(0f,10f),size = Size(width = canvasWidth/3f,canvasHeight),style = Stroke(width = 10f,cap = StrokeCap.Round)
//            )
//            drawArc(Color.Black, 0f, 90f,
//                false, Offset(x = 100f,y = 0f),style = Stroke(width = 10f)
//            )
        }
    )
}

@Preview
@Composable
fun SoundItemDemo() {
    LENGOTheme {
        IconButton(onClick = { /*TODO*/ }) {
            Box(contentAlignment = Alignment.Center) {
                SoundItem()
//                Icon(
//                    imageVector = Icons.Filled.SurroundSound,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(40.dp)
//                        .offset((-15).dp),
//                    tint = MaterialTheme.colors.onBackground
//                )

            }
        }

    }
}