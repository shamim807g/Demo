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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.work.WorkInfo
import com.lengo.common.R
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoOptionButton
import com.lengo.data.repository.UserRepository
import com.lengo.uni.ui.MainActivity
import com.lengo.common.EventHandler
import com.lengo.common.EventQueue
import com.lengo.common.mutableEventQueue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@JvmOverloads
@Composable
fun UpdateUserSheet(
    viewModel: UpdateUserViewModel,
    onUpdateComplete: () -> Unit,
    onBack: () -> Unit = {},
) {

    val mainViewModel = (LocalContext.current as MainActivity).mainViewModel

    val currentOnUpdateComplete by rememberUpdatedState(newValue = onUpdateComplete)

    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = UpdateUserState.Empty)
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    var workerIsRunning = remember { mutableStateOf(false) }
    var workerInfo = mainViewModel.liveWorkStatus.observeAsState()

    LaunchedEffect(workerInfo.value) {
        if(workerInfo.value != null && workerInfo.value?.state != null) {
           if(workerInfo.value?.state == WorkInfo.State.SUCCEEDED) {
               if(workerIsRunning.value) {
                   workerIsRunning.value = false
                   viewModel.logoutUser()
               }
           }
           if(workerInfo.value?.state == WorkInfo.State.BLOCKED) {
               workerIsRunning.value = true
           }
       }
    }

    val isButtonEnable by remember {
        derivedStateOf { viewState.userName.isNotEmpty() && viewState.email.isNotEmpty() }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        delay(200)
        focusRequester.requestFocus()
    }


    EventHandler(eventQueue = viewModel.eventQueue) { event ->
        when (event) {
            UpdateUserEvent.SUCESS -> {
                focusRequester.freeFocus()
                currentOnUpdateComplete()
            }
            is UpdateUserEvent.ERROR -> {
                focusRequester.freeFocus()
                snackbarHostState.showSnackbar(
                    message = event.error,
                    actionLabel = "Hide",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Column(
        Modifier
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        SheetAppBar(stringResource(R.string.updateVoc), onBack)

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            value = viewState.userName,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.LName), style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )
                )
            },
            onValueChange = {
                viewModel.updateData(userName = it)
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
            value = viewState.email,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.LEmail), style = LengoOptionButton().copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                    )
                )
            },
            onValueChange = {
                viewModel.updateData(emai = it)
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
            value = viewState.password,
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
            onValueChange = { viewModel.updateData(pass = it) },
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            textStyle = LengoOptionButton().copy(
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Start,
            ),
            keyboardActions = KeyboardActions(onDone = {
            })
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = CenterHorizontally),
            onClick = {
                viewModel.deleteUser()
            }) {
            Text(
                text = stringResource(R.string.account_delete), style = LengoButtonText()
                    .copy(color = MaterialTheme.colors.secondary)
            )
        }

        Button(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 4.dp)
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(6.dp)), onClick = {
                viewModel.updateUser()
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
                    text = stringResource(R.string.updateVoc),
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

        Button(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 10.dp)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(6.dp)), onClick = {
                    mainViewModel.syncDataWithServer()
                 },
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = MaterialTheme.colors.onBackground,
                disabledContentColor = MaterialTheme.colors.surface,
                backgroundColor = MaterialTheme.colors.onBackground,
                contentColor = MaterialTheme.colors.background
            )
        ) {
            Box {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    text = stringResource(R.string.LLogout),
                    style = LengoButtonText().copy(textAlign = TextAlign.Center),
                    maxLines = 1
                )
                if (workerIsRunning.value) {
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
            Snackbar(
                modifier = Modifier
                    .padding(8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                action = { },
            ) { Text(text = snackbarHostState.currentSnackbarData?.message ?: "") }
        }
    }


}

@Stable
data class UpdateUserState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val password: String = "",
    val email: String = ""
) {
    companion object {
        val Empty = UpdateUserState()
    }
}

sealed class UpdateUserEvent {
    data object SUCESS : UpdateUserEvent()
    data class ERROR(val error: String) : UpdateUserEvent()
}


@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateUserState())
    val uiState: StateFlow<UpdateUserState> = _uiState.asStateFlow()

    private val _eventQueue = mutableEventQueue<UpdateUserEvent>()
    val eventQueue: EventQueue<UpdateUserEvent> = _eventQueue

    init {
        logcat("User Data Init") { "init" }
    }

    fun fetchData() {
        logcat("User Data Init") { "fetchData" }
        viewModelScope.launch {
            userRepository.observeUserData.collectLatest { usr ->
                _uiState.update {
                    it.copy(
                        userName = usr.name ?: "",
                        email = usr.email ?: "",
                    )
                }
            }
        }
    }

    fun updateData(userName: String? = null, pass: String? = null, emai: String? = null) {
        if (userName != null) {
            _uiState.update { it.copy(userName = userName) }
        }
        if (pass != null) {
            _uiState.update { it.copy(password = pass) }
        }
        if (emai != null) {
            _uiState.update { it.copy(email = emai) }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logout {
                _eventQueue.push(UpdateUserEvent.SUCESS)
            }
        }
    }

    fun updateUser() {
        viewModelScope.launch {
            userRepository.updateUser(_uiState.value.email,_uiState.value.userName,_uiState.value.password) {
                if(it != null) {
                    _eventQueue.push(UpdateUserEvent.ERROR(it))
                } else {
                    _eventQueue.push(UpdateUserEvent.SUCESS)
                }
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUser {
                 if(it != null) {
                     _eventQueue.push(UpdateUserEvent.ERROR(it))
                 } else {
                     _eventQueue.push(UpdateUserEvent.SUCESS)
                 }
            }
        }
    }
}