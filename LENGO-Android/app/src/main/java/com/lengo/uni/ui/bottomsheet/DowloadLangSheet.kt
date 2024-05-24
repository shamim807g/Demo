package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.theme.LengoHeading5
import com.lengo.common.ui.theme.LengoSemiBold18h4

@Composable
fun DownloadLangSheet(ok: () -> Unit,cancel: () -> Unit) {
    Column(modifier = Modifier
        .padding(horizontal = 16.dp,vertical = 16.dp)
        .navigationBarsPadding()) {
        Text(
            text = stringResource(id = R.string.languageDownloadL),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            style = LengoHeading5(), modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )
        Text(stringResource(id = R.string.languageDownlaodDiscL),
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center,
            style = LengoSemiBold18h4(), modifier = Modifier
                .padding(vertical = 16.dp)
            .fillMaxWidth())

        Row(Modifier.padding(vertical = 16.dp).fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = cancel,Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.CloseL))
            }

            Button(onClick = ok ,Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.okayLocal))
            }
        }

    }
}