package com.lengo.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoButtonText

@Composable
fun LengoButton(
    modifier: Modifier,
    text: String = stringResource(R.string.Start),
    color: ButtonColors = ButtonDefaults.buttonColors(
        disabledBackgroundColor = MaterialTheme.colors.surface,
        disabledContentColor = MaterialTheme.colors.secondary,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.background
    ),
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        onClick = onClick,
        colors = color,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = LengoButtonText().copy(textAlign = TextAlign.Center),
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun DemoButton() {
    LENGOTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            LengoButton(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            )
        }
    }
}