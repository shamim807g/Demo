package com.lengo.uni.ui.bottomsheet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.R
import com.lengo.common.ui.VerticleSpace
import com.lengo.data.repository.UserRepository
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.network.ApiService
import com.lengo.common.ui.theme.LengoOptionButton
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@ExperimentalComposeUiApi
@JvmOverloads
@Composable
fun CouponSheet(
    viewModel: CouponViewModel,
    onBack: () -> Unit = {}
) {

    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = CouponViewState.Empty)


    var answerText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.initState()
        delay(300)
        focusRequester.requestFocus()
    }

    Column(Modifier.fillMaxSize()) {
        SheetAppBar(stringResource(R.string.couponName),onBack = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onBack()
        })
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .navigationBarsPadding()
            .verticalScroll(scrollState)) {

            VerticleSpace(16.dp)
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colors.surface)
                    .focusRequester(focusRequester),
                value = answerText,
                onValueChange = {
                    answerText = it
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onBackground,
                    backgroundColor = Color.Transparent,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                textStyle = LengoOptionButton().copy(
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusRequester.freeFocus()
                    keyboardController?.hide()
                    viewModel.processCoupon(answerText)
                }),
            )
            VerticleSpace(16.dp)
            Text(
                text = viewState.hintText,
                color = viewState.hintTextColor ?: MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

    }



}

@Stable
data class CouponViewState(
   val hintText: String = "",
   val hintTextColor: Color? = null
) {
    companion object {
        val Empty = CouponViewState()
    }
}

@HiltViewModel
class CouponViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val userRepository: UserRepository,
    private val userDoa: UserDoa,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(CouponViewState(hintText = context.getString(R.string.couponText)))
    val uiState: StateFlow<CouponViewState> = _uiState.asStateFlow()

    fun initState() {
        _uiState.update { it.copy(hintText = context.getString(R.string.couponText),
            hintTextColor = null) }
    }

    suspend fun validateCoupon(code: String, sel_lng_tkn: String): Int? {

        val stillValid: Boolean = userRepository.isCouponValid(code)
        if (!stillValid) {
            return 0
        }

        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("code", code)
            jsonObject.addProperty("sel_lng_tkn", sel_lng_tkn)
            val result = apiService.validateCoupon(jsonObject)
            val coins_val = result?.coins_val
            if (coins_val != null) { // make less junkie
                if (coins_val > 0) {
                    userRepository.makeCouponCodeInvalid(code)
                }
            }
            logcat("COUPON API") { "COUPON API Complete ${result}" }
            return coins_val
        } catch (ex: Exception) {
            logcat("COUPON API") { ex.asLog() }
            return 0
        }
    }

    fun processCoupon(answerText: String) {
        viewModelScope.launch {
            val currentUser = userDoa.currentUser()
            if(currentUser != null) {
                val coins = validateCoupon(answerText, currentUser.sel)
                if (coins != null) {
                    if (coins > 0) {
                        val coinsTxt = "$coins ${context.getString(R.string.bronze_coins)}"
                        _uiState.update { it.copy(hintText = context.getString(R.string.youReceivedCoins)
                            .replace("coins_value", coinsTxt), hintTextColor = Color.Green) }
                        addCoins(coins)
                    } else {
                        _uiState.update { it.copy(hintText = context.getString(R.string.invalidCoupon),
                            hintTextColor = Color.Red) }
                    }
                } else {
                    _uiState.update { it.copy(hintText = context.getString(R.string.checkInternet),
                        hintTextColor = Color.Red) }
                }
            }
        }
    }

    fun addCoins(coins: Int) {
        viewModelScope.launch {
            userRepository.addCoins(coins)
        }
    }
}