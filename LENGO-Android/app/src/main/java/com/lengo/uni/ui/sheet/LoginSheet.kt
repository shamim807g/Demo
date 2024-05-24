package com.lengo.uni.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.extension.closeKeyboard
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.SheetAppBar
import com.lengo.uni.ui.bottomsheet.LoginEvent
import com.lengo.uni.ui.bottomsheet.LoginViewModel
import com.lengo.uni.ui.bottomsheet.LoginViewState
import com.lengo.uni.ui.MainActivity
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoOptionButton
import kotlinx.coroutines.delay

@JvmOverloads
@Composable
fun LoginSheet(viewModel: LoginViewModel,
               onLoginComplete:() -> Unit,
               onBack: () -> Unit = {},
) {

    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel
    val currentOnLoginComplete by rememberUpdatedState(newValue = onLoginComplete)
    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = LoginViewState.Empty)
    val snackbarHostState = remember{ SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    var nameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val isButtonEnable by remember {
        derivedStateOf { !viewState.isLoading && nameText.isNotEmpty() && passwordText.isNotEmpty() }
    }

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
        viewModel.event.collect {
            when(it) {
                LoginEvent.LOGIN_COMPLETE -> {
                    activity.closeKeyboard()
                    focusRequester.freeFocus()
                    mainViewModel.OnBoardingComplete()
                    activity.mainViewModel.syncDataWithServer()
                    currentOnLoginComplete()
                }
                is LoginEvent.LOGIN_ERROR -> {
                    activity.closeKeyboard()
                    focusRequester.freeFocus()
                    snackbarHostState.showSnackbar(
                        message = it.error,
                        actionLabel = "Hide",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Column(Modifier.background(MaterialTheme.colors.background).statusBarsPadding()) {

        SheetAppBar(stringResource(R.string.LLogin)) {
            activity.closeKeyboard()
            focusRequester.freeFocus()
            onBack()
        }

        Column(modifier = Modifier
            .navigationBarsPadding()) {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
                value = nameText,
                placeholder = {
                    Text(text = stringResource(id = R.string.LName),
                        style = LengoOptionButton().copy(
                            color = MaterialTheme.colors.secondary,
                            textAlign = TextAlign.Start,
                        ))
                },
                onValueChange = {
                    nameText = it
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onBackground,
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                textStyle = LengoOptionButton().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Start,
                )
            )


            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                //.focusRequester(focusRequester),
                value = passwordText,
                visualTransformation = PasswordVisualTransformation(),
                placeholder = {
                    Text(text = stringResource(id = R.string.LPassword), style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    ))
                },
                onValueChange = {
                    passwordText = it
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                textStyle = LengoOptionButton().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Start,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.login(nameText,passwordText)
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
                    viewModel.login(nameText,passwordText)
                }, enabled = isButtonEnable,
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
                        text = stringResource(R.string.LLogin),
                        style = LengoButtonText().copy(textAlign = TextAlign.Center),
                        maxLines = 1
                    )
                    if (viewState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(20.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState) {
            Snackbar(modifier = Modifier.padding(8.dp)
                .navigationBarsPadding()
                .imePadding(),action = {
            },
            ) { Text(text = snackbarHostState.currentSnackbarData?.message ?: "") }
        }
    }

}