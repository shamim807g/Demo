package com.lengo.uni.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.lengo.common.R
import com.lengo.common.TestIdlingResource
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.wave.MultiWaveHeader
import com.lengo.data.repository.LoginEnableStatus
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.bottomsheet.LoginViewModel
import com.lengo.uni.ui.bottomsheet.RegisterSheet
import com.lengo.uni.ui.bottomsheet.RegisterViewModel
import com.lengo.uni.ui.sheet.BaseModalSheet
import com.lengo.uni.ui.sheet.LoginSheet
import com.lengo.common.ui.theme.LengoButtonText
import com.lengo.common.ui.theme.LengoHeading4
import com.lengo.common.ui.theme.LengoHeadingh6

@Composable
fun OnboardingMain(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    viewModel: OnboardingViewModel,
) {
    val navigator = LocalNavigator.current
    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel
    val viewModelState by viewModel.uiState.collectAsState()
    var isLoginSheet by remember { mutableStateOf(false) }
    var isRegisterSheet by remember { mutableStateOf(false) }


    Column(Modifier.fillMaxSize()) {
        if(!TestIdlingResource.isInTest.get()) {
            WaveHeader()
        }
        VerticleSpace(20.dp)
        Text(
            stringResource(R.string.lengo),
            style = LengoHeading4().copy(MaterialTheme.colors.onBackground),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .semantics { contentDescription = "app_name" }
        )
        Text(
            stringResource(R.string.learnLanguagesEverywhere),
            style = LengoHeadingh6().copy(MaterialTheme.colors.secondary),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))

        if (viewModelState.isLoginOrRegisterEnable == LoginEnableStatus.LOADING) {
            CircularProgressIndicator(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .size(40.dp))
        } else {
            if (viewModelState.isLoginOrRegisterEnable == LoginEnableStatus.ENABLE) {
                Column {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        Button(modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)), onClick = { isRegisterSheet = true }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.LRegister),
                                style = LengoButtonText().copy(textAlign = TextAlign.Center),
                                maxLines = 1
                            )
                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp)), onClick = { isLoginSheet = true }) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = stringResource(R.string.LLogin),
                                style = LengoButtonText().copy(textAlign = TextAlign.Center),
                                maxLines = 1
                            )

                        }

                    }
                    TextButton(
                        onClick = {
                            mainViewModel.OnBoardingComplete()
                            navigator.navigate(Screen.OnboardingSubscription.route)
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.skip), style = LengoButtonText()
                                .copy(color = MaterialTheme.colors.primary)
                        )
                    }

                }
            } else {
                Button(
                    modifier = Modifier
                        .padding(18.dp)
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .testTag("start"), onClick = {
                        mainViewModel.OnBoardingComplete()
                        navigator.navigate(Screen.OnboardingSubscription.route)
                    }) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        text = stringResource(R.string.Start),
                        style = LengoButtonText().copy(textAlign = TextAlign.Center),
                        maxLines = 1
                    )
                }
            }
        }

    }


    BaseModalSheet(visible = isLoginSheet, onDismiss = { isLoginSheet = false }) {
        LoginSheet(viewModel = loginViewModel, onLoginComplete = {
            navigator.navigate(Screen.OnboardingSubscription.route)
        }, onBack = {
            isLoginSheet = false
        })
    }

    BaseModalSheet(visible = isRegisterSheet, onDismiss = {
        isRegisterSheet = false }) {
        RegisterSheet(viewModel = registerViewModel, onRegisterComplete = {
            navigator.navigate(Screen.OnboardingSubscription.route)
        },onBack = {
            isRegisterSheet = false
        })
    }



}




@Composable
fun WaveHeader() {
    //val userSel = LocalUserSelLang.current
    val primary = MaterialTheme.colors.primary.toArgb()
    val secoundry = MaterialTheme.colors.primaryVariant.toArgb()
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp), // Occupy the max size in the Compose UI tree
        factory = { context ->
            MultiWaveHeader(context).apply {
                val waves =
                    "70,25,1.4,1.4,-26\n100,5,1.4,1.2,15\n420,0,1.15,1,-10\n520,10,1.7,1.5,20\n220,0,1,1,-15".split(
                        "\n"
                    )
                setWaves(waves.subList(0, 3).joinToString("\n"))
                startColor = primary
                closeColor = secoundry
                velocity = 2.0f
                start()
            }
        }
    )
}