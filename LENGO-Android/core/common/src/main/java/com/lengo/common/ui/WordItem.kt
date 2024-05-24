package com.lengo.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.extension.extractAfterAngleBracket
import com.lengo.common.extension.extractBeforeAngleBracket
import com.lengo.common.extension.extractBetweenAngleBracket
import com.lengo.common.ui.theme.BodyText
import com.lengo.common.ui.theme.DarkSurface
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.lightGrey

@Composable
fun WordItem(
    text1: String = "Hello how are your <my> name is Shakil?",
    text2: String = "hola <my> aasdadasdasbb",
    text2Tran: String = "",
    isChecked: Boolean = false,
    isGram: Boolean = true,
    color: Color = MaterialTheme.colors.surface,
    onCheckChanged: (Boolean) -> Unit = { },
    onSpeak: (String, Boolean) -> Unit = { _, _ -> },
    onItemClicked: () -> Unit = {}
) {

    val isDarkTheme = LocalDarkModeEnable.current

    Row(
        modifier = Modifier.clickable { onItemClicked() }
            .padding(top = 8.dp, start = 8.dp, bottom = 8.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        if (isGram) {
            val annotatedFirstStringColor = MaterialTheme.colors.secondary
            val annotatedFirstString = buildAnnotatedString {
                append(text1.extractBeforeAngleBracket())
                withStyle(style = SpanStyle(color = annotatedFirstStringColor)) {
                    append(text1.extractBetweenAngleBracket())
                }
                append(text1.extractAfterAngleBracket())
            }
            val annotatedSecoundString = buildAnnotatedString {
                append(text2.extractBeforeAngleBracket())
                withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(text2.extractBetweenAngleBracket())
                }
                append(text2.extractAfterAngleBracket())
                if (!text2Tran.isNullOrEmpty()) {
                    val primaryColor = MaterialTheme.colors.primary
                    append("\n")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W400
                        )
                    ) {
                        append(text2Tran.extractBeforeAngleBracket())
                        withStyle(style = SpanStyle(color = primaryColor)) {
                            append(text2Tran.extractBetweenAngleBracket())
                        }
                        append(text2Tran.extractAfterAngleBracket())
                    }
                }
            }
            Text(
                annotatedFirstString,
                modifier = Modifier.weight(1f),
                style = BodyText().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Left,
                ),
                maxLines = 4
            )
            HorizontalSpace()
            Text(
                modifier = Modifier.weight(1f),
                text = annotatedSecoundString,
                style = BodyText().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Left,
                ),
                maxLines = 4
            )

        } else {
            Text(
                text1,
                modifier = Modifier.weight(1f),
                style = BodyText().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Left,
                ),
                maxLines = 4
            )
            HorizontalSpace()
            val annotatedString = buildAnnotatedString {
                append(text2)
                if (!text2Tran.isNullOrEmpty()) {
                    append("\n")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                            fontWeight = FontWeight.W400
                        )
                    ) {
                        append(text2Tran)
                    }
                }
            }

            Text(
                modifier = Modifier.weight(1f),
                text = annotatedString,
                style = BodyText().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Left,
                ),
                maxLines = 4
            )
        }

        Box(
            modifier = Modifier
                .size(25.dp)
                .background(
                    if (color == lightGrey) if (isDarkTheme) DarkSurface else lightGrey else color,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckChanged,
                modifier = Modifier.fillMaxSize(),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Transparent,
                    uncheckedColor = Color.Transparent,
                    checkmarkColor = MaterialTheme.colors.onBackground
                )
            )
        }

        HorizontalSpace()
        SoundItem2 {
            if (isGram) {
                onSpeak(text2.extractBetweenAngleBracket(), false)
            } else {
                onSpeak(text2, false)
            }
        }
    }

}

@Preview
@Composable
fun WordItemDemo() {
    LENGOTheme {
        WordItem()
    }
}