package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.extension.closeKeyboard
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoOptionButton
import com.lengo.common.ui.theme.LengoRegular18h4
import com.lengo.model.data.Lection
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.sheet.BaseModalSheet
import com.lengo.uni.ui.wordlist.PackPublicStatus

@ExperimentalMaterialApi
@Composable
fun UseWordUpdateSheet(
    visible: Boolean,
    ownWord: String = "",
    selWord: String = "",
    ownWordLabel: String,
    selWordLabel: String,
    onUpdateWords: (own: String, sel: String) -> Unit,
    onDismiss: () -> Unit
) {

    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        UseWordUpdateSheetContent(ownWord,selWord,ownWordLabel,selWordLabel, onUpdateWords, onDismiss)
    }
}

@ExperimentalMaterialApi
@Composable
private fun UseWordUpdateSheetContent(
    ownWord: String = "",
    selWord: String = "",
    ownWordLabel: String,
    selWordLabel: String,
    onUpdateWords: (own: String, sel: String) -> Unit,
    onDismiss: () -> Unit
) {
    val activity = (LocalContext.current as MainActivity)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var _ownWord by remember { mutableStateOf(ownWord) }
    var _selWord by remember { mutableStateOf(selWord) }
    val showButton by remember { derivedStateOf { _ownWord.isNotEmpty() && _selWord.isNotEmpty() } }

    Column(
        Modifier
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .fillMaxSize()) {

        SheetAppBar(stringResource(R.string.updateVoc)) {
            activity.closeKeyboard()
            focusRequester.freeFocus()
            onDismiss()
        }

        Column(modifier = Modifier
            .weight(1f)
            .navigationBarsPadding()) {

            TextField(
                value = _ownWord,
                onValueChange = { _ownWord = it },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onBackground,
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface
                ),
                maxLines = 1,
                placeholder = { Text(text = ownWordLabel,
                    style = LengoRegular18h4().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    activity.closeKeyboard()
                    focusRequester.freeFocus()
                }),
                textStyle = LengoRegular18h4().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Start,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )


            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                value = _selWord,
                placeholder = { Text(text = selWordLabel,
                    style = LengoRegular18h4().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )) },
                onValueChange = { _selWord = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                textStyle = LengoRegular18h4().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Start,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    activity.closeKeyboard()
                    focusRequester.freeFocus()
                })
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .padding(18.dp)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(6.dp)), onClick = {
                    onUpdateWords(_ownWord,_selWord)
                }, enabled = showButton,
                colors = ButtonDefaults.buttonColors(
                    disabledBackgroundColor = MaterialTheme.colors.surface,
                    disabledContentColor = MaterialTheme.colors.secondary,
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.primary
                ),
            ) {
                Box {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        text = stringResource(R.string.updateVoc),
                        style = LengoButtonText().copy(textAlign = TextAlign.Center),
                        maxLines = 1
                    )
                }
            }
        }
    }
}