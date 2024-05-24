package com.lengo.common.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.ui.theme.ChipText
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.model.data.BADGE


@Composable
fun BuyTextChip(
    modifier: Modifier = Modifier,
    stringRes: Int = R.string.buy,
    onClick: () -> Unit = { }
) {
    TextButton(onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(backgroundColor = MaterialTheme.colors.onSurface), contentPadding = PaddingValues(2.dp)) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            text = stringResource(id = stringRes), style = ChipText()
                .copy(color = MaterialTheme.colors.primary)
        )
    }
}


@Composable
fun TextChip(
    modifier: Modifier = Modifier,
    coins: Int = 0,
    color: Color = MaterialTheme.colors.surface,
    bagText: BADGE = BADGE.NONE,
    onClick: () -> Unit
) {

    TextButton(
        onClick = {
            if (bagText != BADGE.NONE) {
                onClick()
            }
        }, colors = ButtonDefaults.textButtonColors(backgroundColor = color),
        shape = CircleShape, contentPadding = PaddingValues(2.dp)
    ) {
        if (bagText != BADGE.NONE && bagText != BADGE.LOADING) {
            AutoResizeText(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
                text = bagText.geText(LocalContext.current, coins),
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 12.sp,
                    max = 13.sp,
                ),
                style = ChipText()
                    .copy(color = MaterialTheme.colors.primary),)
        } else {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
    }
}

fun BADGE.geText(context: Context, coins: Int): String {
    return when (this) {
        BADGE.GET -> context.getString(R.string.LLoad)
        BADGE.OPEN -> context.getString(R.string.open)
        BADGE.COIN -> {
            if (coins == 1) {
                "$coins ${context.getString(R.string.coin)}"
            } else {
                "$coins ${context.getString(R.string.coins)}"
            }
        }
        BADGE.LOADING -> ""
        BADGE.SHARE -> context.getString(R.string.share).uppercase()
        BADGE.NONE -> ""
    }
}

@Preview
@Composable
fun TextChipDemo() {
    LENGOTheme {
        TextChip {}
    }
}