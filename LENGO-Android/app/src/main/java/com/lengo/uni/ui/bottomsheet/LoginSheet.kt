package com.lengo.uni.ui.bottomsheet

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.common.R
import com.lengo.common.extension.closeKeyboard
import com.lengo.common.extension.getStringByIdName
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoOptionButton
import com.lengo.data.datasource.UserJsonDataProvider
import com.lengo.data.repository.PacksRepository
import com.lengo.data.repository.UserRepository
import com.lengo.uni.ui.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@JvmOverloads
@Composable
fun LoginSheet(viewModel: LoginViewModel,
               onLoginComplete:() -> Unit,
               onBack: () -> Unit = {},
) {

    BackHandler(enabled = true, onBack = { onBack() })

    val activity = LocalContext.current as MainActivity
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
                    focusRequester.freeFocus()
                    currentOnLoginComplete()
                }
                is LoginEvent.LOGIN_ERROR -> {
                    focusRequester.freeFocus()
                    snackbarHostState.showSnackbar(
                        message = it.error,
                        actionLabel = "Hide",
                        duration = SnackbarDuration.Short
                    )
                }

                else -> {}
            }
        }
    }

    Column(Modifier.background(MaterialTheme.colors.background).statusBarsPadding().fillMaxSize()) {

        SheetAppBar(stringResource(R.string.LLogin)) {
            activity.closeKeyboard()
            focusRequester.freeFocus()
            onBack()
        }

        Column(modifier = Modifier
            .weight(1f)
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
                    // textColor = MaterialTheme.colors.onBackground,
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface
//                    disabledTextColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
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
                .background(MaterialTheme.colors.onSurface)
                .navigationBarsPadding()
                .imePadding(),action = {
                },
            ) {
                Text(text = snackbarHostState.currentSnackbarData?.message ?: "",color = MaterialTheme.colors.primary)
            }
        }
    }

}


sealed class LoginEvent {
    object LOGIN_COMPLETE : LoginEvent()
    data class LOGIN_ERROR(val error: String): LoginEvent()
}

@Stable
data class LoginViewState(
   val isLoading: Boolean = false
) {
    companion object {
        val Empty = LoginViewState()
    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository,
    private val userJsonDataProvider: UserJsonDataProvider,
    private val packsRepository: PacksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginViewState())
    val uiState: StateFlow<LoginViewState> = _uiState.asStateFlow()

    private val _channel = Channel<LoginEvent>()
    val event = _channel.receiveAsFlow()

    fun login(userNameOrEmail: String, password: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.login(userNameOrEmail, password) { loginRes ->
                if(loginRes != null) {
                    viewModelScope.launch {
                        if(loginRes.locale_msg_key != null ) {
                            val message = getStringByIdName(context,loginRes.locale_msg_key) ?: "error"
                            _channel.send(LoginEvent.LOGIN_ERROR(message))
                            _uiState.update { it.copy(isLoading = false) }
                        } else {
                            userJsonDataProvider.setUserLoginData(loginRes, password,true)
                            userJsonDataProvider.updateSetting(loginRes)
                            userJsonDataProvider.setDateState(loginRes)
                            userJsonDataProvider.updateData(loginRes) {
                                viewModelScope.launch {
                                    _channel.send(LoginEvent.LOGIN_COMPLETE)
                                    _uiState.update { it.copy(isLoading = false) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}