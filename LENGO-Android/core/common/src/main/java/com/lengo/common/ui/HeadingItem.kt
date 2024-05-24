package com.lengo.common.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoHeading2
import com.lengo.common.ui.theme.SeeALlText
import com.lengo.model.data.BADGE

@Composable
fun HeadingCoin(
    heading: String = "Vocabulary",
    coins: Int = 9,
    bagText: BADGE = BADGE.NONE,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        AutoResizeText(
            modifier = Modifier.weight(1f),
            text = heading,
            maxLines = 1,
            fontSizeRange = FontSizeRange(
                min = 23.sp,
                max = 24.sp,
            ),
            style = LengoHeading2().copy(color = MaterialTheme.colors.onBackground),
        )

        TextChip(onClick = onClick, coins = coins, bagText = bagText)
    }

}

@Composable
fun HeadingSeAll(heading: String = "Vocabulary", onSeeAllClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        AutoResizeText(
            modifier = Modifier.weight(1f),
            text = heading,
            maxLines = 1,
            fontSizeRange = FontSizeRange(
                min = 24.sp,
                max = 26.sp,
            ),
            style = LengoHeading2().copy(color = MaterialTheme.colors.onBackground),
        )

        TextButton(onClick = onSeeAllClicked) {
            AutoResizeText(
                text = stringResource(R.string.showAll),
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 14.sp,
                    max = 16.sp,
                ),
                style = SeeALlText().copy(color = MaterialTheme.colors.primary),
            )

        }

    }
}

@Preview
@Composable
fun HeadingCoinDemo() {
    LENGOTheme {
        HeadingCoin() {}
    }
}

@Preview
@Composable
fun HeadingSeAllDemo() {
    LENGOTheme {
        HeadingSeAll() {}
    }
}