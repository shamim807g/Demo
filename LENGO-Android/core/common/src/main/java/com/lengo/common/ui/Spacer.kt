package com.lengo.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun VerticleSpace(height: Dp = 8.dp) {
    Spacer(modifier = Modifier.requiredHeight(height))
}

@Composable
fun VerticleSpaceWithNav(height: Dp = 8.dp) {
    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars.add(WindowInsets(top = height, bottom = height))))
}


@Composable
fun HorizontalSpace(width: Dp = 8.dp) {
    Spacer(modifier = Modifier.requiredWidth(width))
}