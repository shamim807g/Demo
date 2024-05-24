package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.lengo.common.R
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.rating.RatingBar
import com.lengo.common.ui.rating.RatingBarStyle
import com.lengo.common.ui.rating.StepSize
import com.lengo.common.ui.theme.LengoBold20
import com.lengo.common.ui.theme.LengoHeading4

@Composable
fun PackReviewSheet(packName: String,close: () -> Unit, submit: (Float,String) -> Unit) {
    val text = rememberSaveable { mutableStateOf("") }
    val rating = rememberSaveable { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {


        Text(
            text = stringResource(id = R.string.pack_feedback).replace("pack_name",packName).replace("+"," "),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            style = LengoHeading4(), modifier = Modifier
                .padding(vertical = 0.dp)
                .fillMaxWidth()
        )

        Text(
            stringResource(id = R.string.pack_feedback_text).replace("pack_name",packName).replace("+"," "),
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center,
            style = LengoBold20(), modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        VerticleSpace(16.dp)

        RatingBar(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            value = rating.value,
            size = 36.dp,
            padding = 8.dp,
            numStars = 5,
            stepSize = StepSize.ONE,
            ratingBarStyle = RatingBarStyle.HighLighted, onValueChange = {
                rating.value = it
            }) {
        }

        VerticleSpace(16.dp)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text(stringResource(id = R.string.freiwilligeAngabe)) }
        )

        VerticleSpace(16.dp)

        Row(
            Modifier.fillMaxWidth()
        ) {

            TextButton(onClick = close, Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.CloseL))
            }

            VerticleSpace(16.dp)

            Button(
                onClick = {
                    submit(rating.value,text.value)
                }, modifier = Modifier.weight(1f),
                enabled = rating.value > 0f, colors = ButtonDefaults.buttonColors(
                    disabledBackgroundColor = MaterialTheme.colors.surface,
                    disabledContentColor = MaterialTheme.colors.secondary,
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.background
                )
            ) {
                Text(text = stringResource(id = R.string.send))
            }
        }

        VerticleSpace(16.dp)

    }
}