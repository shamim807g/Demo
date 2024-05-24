package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.lengo.common.R
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.theme.Grey
import com.lengo.common.ui.theme.LengoHeading4
import logcat.logcat

@Composable
fun DashboardReviewSheet(openGoogleRating: () -> Unit,close: () -> Unit) {
    val rating = rememberSaveable { mutableStateOf(0) }
    val currentOpenGoogleRating by rememberUpdatedState(newValue = openGoogleRating)
    val currentClose by rememberUpdatedState(newValue = close)

    LaunchedEffect(key1 = rating.value, block = {
        logcat { "Rating final: ${rating.value}" }
        if(rating.value == 5) {
            currentOpenGoogleRating()
        } else if(rating.value in 1..4){
            currentClose()
        }
    })

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {

        VerticleSpace(20.dp)

        Text(
            text = stringResource(id = R.string.likeLengo),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            style = LengoHeading4(), modifier = Modifier
                .padding(vertical = 0.dp)
                .fillMaxWidth()
        )


        VerticleSpace(20.dp)

        Row( modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Star { rating.value = 1 }
            Star { rating.value = 2 }
            Star { rating.value = 3 }
            Star { rating.value = 4 }
            Star { rating.value = 5 }
        }


        VerticleSpace(50.dp)


    }

}

@Composable
fun Star(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        modifier = Modifier
            .size(38.dp)
            .clickable(onClick = onClick),
        tint = Grey
    )
}