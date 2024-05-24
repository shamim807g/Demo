package com.lengo.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoSubHeading

@Composable
fun LangSelectItem(
    flag: Int = R.drawable.spain, name: String = "English",
    onItemSelected: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable { onItemSelected() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = flag),
            contentDescription = null,
            modifier = Modifier
                .requiredSize(50.dp)
                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
        )

        Text(
            text = name,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp),
            style = LengoSubHeading().copy(textAlign = TextAlign.Left)
        )

        IconButton(onClick = onItemSelected) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Outlined.NavigateNext,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Preview
@Composable
fun LangSelectItemDemo() {
    LENGOTheme {
        LangSelectItem()
    }
}