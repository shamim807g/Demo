package com.lengo.common.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.ui.theme.Green

@ExperimentalAnimationApi
@Preview
@Composable
fun ScoreItem() {
    Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
        Column(Modifier.padding(20.dp),horizontalAlignment = Alignment.CenterHorizontally) {

            var switch by remember { mutableStateOf(false) }


            var visible by remember { mutableStateOf(false) }
            var alphaState  by remember { mutableStateOf(true) }

            val offset: Float by animateFloatAsState(targetValue = if(!visible) -500f else 0f,finishedListener = {
                switch = false
                visible = false
                alphaState = true
            })

            //val weight: Float by animateFloatAsState(targetValue = if(!visible) 1f else 0f)
            val aalpha: Float by animateFloatAsState(targetValue = if(alphaState) 1f else 0f)

            var count by remember { mutableStateOf(0) }

//            AnimatedContent(
//                targetState = count,
//                transitionSpec = {
//                    // Compare the incoming number with the previous number.
//                    if (targetState > initialState) {
//                        // If the target number is larger, it slides up and fades in
//                        // while the initial (smaller) number slides up and fades out.
////                        slideInVertically({ height -> height }) + fadeIn() with
////                                slideOutVertically({ height -> -height }) + fadeOut(animationSpec = tween(300))
//
//                        slideInHorizontally({ width -> width  },animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) with
//                                slideOutHorizontally({ width -> -width },animationSpec = tween(600)) + fadeOut(animationSpec = tween(600))
//                    } else {
//                        // If the target number is smaller, it slides down and fades in
//                        // while the initial number slides down and fades out.
//                        slideInHorizontally({ width -> -width  },animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)) with
//                                slideOutHorizontally({ width -> width },animationSpec = tween(1000)) + fadeOut(animationSpec = tween(1000))
//
//
////                        slideInVertically({ height -> -height }) + fadeIn() with
////                                slideOutVertically({ height -> height }) + fadeOut(animationSpec = tween(300))
//                    }.using(
//                        // Disable clipping since the faded slide-in/out should
//                        // be displayed out of bounds.
//                        SizeTransform(clip = false)
//                    )
//                }
//            ) { targetCount ->
//                Text(text = "$targetCount",fontSize = 20.sp)
//            }


            Box(modifier = Modifier.width(200.dp)) {
                //if(visible) {
                //if(switch) {
                    Text("+1", color = Green, fontSize = 50.sp, modifier = Modifier.graphicsLayer {
                        alpha = aalpha
                        translationX = offset
                    }.align(Alignment.CenterEnd))
                //}
                //}
                Text(text = "10",fontSize = 50.sp,modifier = Modifier.align(Alignment.CenterEnd))
            }

            Spacer(modifier = Modifier.size(20.dp))
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Button(onClick = {
                    visible = true
                    alphaState = false
                    switch = true
                }) {
                    Text("Minus")
                }
                Spacer(modifier = Modifier.size(60.dp))
                Button(onClick = { count++ }) {
                    Text("Plus")
                }
            }
        }
    }
}