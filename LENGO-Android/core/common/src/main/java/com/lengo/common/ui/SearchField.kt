package com.lengo.common.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoSearchField

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun SearchField(
    onQueryChanged: (String) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val isFocus by remember { mutableStateOf(false) }
    //val color = if (isFocus) MaterialTheme.colors.primary else MaterialTheme.colors.surface


    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colors.surface,
                shape = CircleShape
            ),
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onQueryChanged(it)
        },
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(R.string.app_name),
                style = LengoSearchField().copy(
                    color = MaterialTheme.colors.secondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Left,
                ),
                maxLines = 1
            )
        },
        colors = TextFieldDefaults
            .textFieldColors(
                textColor = MaterialTheme.colors.secondary,
                backgroundColor = Color.Transparent,
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
        trailingIcon = {

        },
        leadingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                    searchQuery = ""
                    onQueryChanged("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Search ,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        textStyle = LengoSearchField().copy(textAlign = TextAlign.Left),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
        })
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun Demo() {
    LENGOTheme {
        SearchField {}
    }
}