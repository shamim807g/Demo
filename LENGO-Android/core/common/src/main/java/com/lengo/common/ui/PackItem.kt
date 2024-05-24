package com.lengo.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoCaption
import com.lengo.common.ui.theme.LengoSubHeading
import com.lengo.model.data.BADGE

@Composable
fun PackItem(
    modifier: Modifier = Modifier,
    heading: String = "Timeasdadadasdadasdadsads adasdasdasdasdsadasdad",
    subHeading: String = "Everasdasdasddasdasdyday Life",
    bagText: BADGE = BADGE.NONE,
    coins: Int = 9,
    emojiText: String = "",
    onClick:(String) -> Unit = {}
) {
    Row(
        modifier = modifier.clickable {
            onClick(heading)
        }
    ) {
        EmojiBox(emojiText)
        Column(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)) {

            AutoResizeText(
                modifier = Modifier,
                text = heading,
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 16.sp,
                    max = 18.sp,
                ),
                style = LengoSubHeading()
                    .copy(color = MaterialTheme.colors.onBackground),
            )

            AutoResizeText(
                modifier = Modifier,
                text = subHeading,
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 10.sp,
                    max = 12.sp,
                ),
                style = LengoCaption()
                    .copy(color = MaterialTheme.colors.secondary),
            )
        }
        TextChip(coins = coins,bagText = bagText) {
            onClick(heading)
        }
    }

}

@Preview
@Composable
fun PackItemDemo() {
    LENGOTheme {
        PackItem()
    }
}