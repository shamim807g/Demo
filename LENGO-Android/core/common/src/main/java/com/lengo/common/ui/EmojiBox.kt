package com.lengo.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lengo.common.ui.theme.LENGOTheme

@Composable
fun EmojiBox(emojiText: String = "🔄", size: Dp = 52.dp, onClick: () -> Unit = {}) {
    Box(modifier = Modifier
        .size(size)
        .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(6.dp))
        .clickable { onClick() }) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp),
            text = emojiText, style = MaterialTheme.typography.h5
        )
    }

}

@Preview
@Composable
fun EmojiBoxDemo() {
    LENGOTheme {
        EmojiBox()
    }
}