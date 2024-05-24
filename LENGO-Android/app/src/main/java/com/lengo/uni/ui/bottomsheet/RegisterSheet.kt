package com.lengo.uni.ui.bottomsheet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
fun RegisterSheet(
    viewModel: RegisterViewModel,
    onRegisterComplete: () -> Unit,
    onBack: () -> Unit = {}
) {
    val currentOnRegisterComplete by rememberUpdatedState(newValue = onRegisterComplete)
    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel

    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = RegisterViewState.Empty)

    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val isButtonEnable by remember {
        derivedStateOf { nameText.isNotEmpty() && passwordText.isNotEmpty() }
    }

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
        viewModel.event.collect {
            when (it) {
                RegisterEvent.REGISTER_COMPLETE -> {
                    activity.closeKeyboard()
                    focusRequester.freeFocus()
                    mainViewModel.OnBoardingComplete()
                    currentOnRegisterComplete()
                }
                is RegisterEvent.REGISTER_ERROR -> {
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


    Column(
        Modifier
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
    ) {
        SheetAppBar(stringResource(R.string.LRegister)) {
            activity.closeKeyboard()
            focusRequester.freeFocus()
            onBack()
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            value = nameText,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.LName), style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )
                )
            },
            onValueChange = {
                nameText = it
            },
            colors = TextFieldDefaults.textFieldColors(
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

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = emailText,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.LEmail), style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )
                )
            },
            onValueChange = {
                emailText = it
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedIndicatorColor = MaterialTheme.colors.surface
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
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

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = passwordText,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.LPassword),
                    style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )
                )
            },
            onValueChange = { passwordText = it },
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
                viewModel.register(nameText, emailText, passwordText)
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
                viewModel.register(nameText, emailText, passwordText)
            }, enabled = isButtonEnable,
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = MaterialTheme.colors.surface,
                disabledContentColor = MaterialTheme.colors.secondary,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary
            )
        ) {

            Box {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    text = stringResource(R.string.LRegister),
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

@Stable
data class RegisterViewState(
    val isLoading: Boolean = false
) {
    companion object {
        val Empty = RegisterViewState()
    }
}


sealed class RegisterEvent {
    object REGISTER_COMPLETE : RegisterEvent()
    data class REGISTER_ERROR(val error: String) : RegisterEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository,
    private val userJsonDataProvider: UserJsonDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterViewState())
    val uiState: StateFlow<RegisterViewState> = _uiState.asStateFlow()

    private val _channel = Channel<RegisterEvent>()
    val event = _channel.receiveAsFlow()

    fun register(name: String, email: String, password: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.register(name, email, password) { registerRes ->
                if (registerRes != null) {
                    viewModelScope.launch {
                        if (registerRes.locale_msg_key != null) {
                            val message = getStringByIdName(context, registerRes.locale_msg_key) ?: "error"
                            _channel.send(RegisterEvent.REGISTER_ERROR(message))
                        } else {
                            _channel.send(RegisterEvent.REGISTER_COMPLETE)
                            registerRes.userid?.let { id ->
                                userJsonDataProvider.updateUserData(id, name, email, password)
                            }
                        }
                    }
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
