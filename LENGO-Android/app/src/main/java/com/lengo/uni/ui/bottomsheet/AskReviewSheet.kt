package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.theme.LengoBold20
import com.lengo.common.ui.theme.LengoHeading4
import com.lengo.common.ui.theme.LengoHeadingh6

@Composable
fun AskReviewSheet(close: () -> Unit,support: ()-> Unit) {
    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
        .navigationBarsPadding()) {


        Text(
            text = stringResource(id = R.string.AlertT1LocalShort),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = LengoHeading4(), modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(id = R.string.AlertT1Local).replace("\\n","\n"),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = LengoHeadingh6(), modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        Text(
            stringResource(id = R.string.rating_android_text).replace("\\n","\n"),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            style = LengoBold20(), modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth())

        Column(
            Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()) {

            TextButton(onClick = close, Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.CloseTLocal),Modifier.padding(8.dp))
            }

            VerticleSpace()

            Button(onClick = support , Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.OkTLocal),Modifier.padding(8.dp))
            }
        }

    }
}