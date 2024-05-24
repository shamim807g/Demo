package com.lengo.uni.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.uni.BuildConfig
import com.lengo.common.R
import com.lengo.common.extension.Coins
import com.lengo.common.getDrawableForLangCode
import com.lengo.uni.ui.bottomsheet.*
import com.lengo.common.ui.ChildAppBar
import com.lengo.uni.ui.sheet.BaseModalSheet
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.common.ui.BuyTextChip
import com.lengo.common.ui.HorizontalSpace
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.VerticleSpaceWithNav
import com.lengo.model.data.Lang
import com.lengo.model.data.SettingModel
import com.lengo.model.data.Subscription
import com.lengo.uni.ui.sheet.BottomSheetVisibleState
import com.lengo.uni.ui.sheet.BuyCoinModelSheet
import com.lengo.uni.ui.sheet.BuyCoinSheetState
import com.lengo.uni.ui.sheet.DeviceLangSelectSheet
import com.lengo.uni.ui.sheet.SubscriptionModelSheet
import com.lengo.uni.ui.sheet.SubscriptionSheetState
import com.lengo.uni.ui.sheet.VoicesSelectSelectSheet
import com.lengo.common.ui.theme.*
import com.lengo.uni.utils.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Profile() {
    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel
    val loginViewModel = androidx.hilt.navigation.compose.hiltViewModel<LoginViewModel>()
    val registerViewModel = androidx.hilt.navigation.compose.hiltViewModel<RegisterViewModel>()
    val updateUserViewModel = androidx.hilt.navigation.compose.hiltViewModel<UpdateUserViewModel>()
    val couponViewModel = androidx.hilt.navigation.compose.hiltViewModel<CouponViewModel>()
    val controller = LocalNavigator.current
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<ProfileViewModel>()
    val profileViewState: ProfileViewState by viewModel.uiState.collectAsState()
    val sheetState by remember { mutableStateOf(BottomSheetVisibleState()) }

    val vScrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        ChildAppBar(title = stringResource(R.string.profile), onBack = {
            controller.popBackStack()
        })
        Column(
            modifier = Modifier
                .verticalScroll(vScrollState)
                .weight(1f)
        ) {
            VerticleSpace(16.dp)
            ProfileBox(
                isUserLogin = profileViewState.isUserLogin,
                userNameorEmail = profileViewState.userNameorEmail,
                scoreString = profileViewState.scoreString,
                progressPercent = profileViewState.progressPercent,
                level = profileViewState.levelString,
                openLoginSheet = {
                    sheetState.isLoginSheet.value = true
                }, openRegisterSheet = {
                    sheetState.isRegisterSheet.value = true
                }, openUpdateUserSheet = {
                    sheetState.isUserUpdateSheet.value = true
                }
            )
            profileViewState.userSelectedLang?.let { lang ->
                profileViewState.showCoupon.let { showCoupon ->
                    if (showCoupon) {
                        VerticleSpace(16.dp)
                        Coupons { sheetState.isCouponSteet.value = true }
                    }
                }
                VerticleSpace(16.dp)
                BuyCoinsOrSub(
                    totalCoins = profileViewState.totalCoins,
                    coinTypes = profileViewState.coinTypes,
                    billingError = profileViewState.billingError,
                    subList = profileViewState.subList,
                    selLang = lang,
                    onSubscription = { sub ->
                    sheetState.subscriptionSheetState.value = SubscriptionSheetState(true, sub)
                },onBuyChip = {
                        sheetState.buyCoinSheetState.value = BuyCoinSheetState(true)
            })
                VerticleSpace(16.dp)
                SettingBox(
                    profileViewState.settingModel,
                    profileViewState?.deviceLang?.drawable,
                    profileViewState?.deviceLang?.locale?.displayLanguage,
                    changeModel = viewModel::updateSettingModel,
                    openLangSheet = { sheetState.isDeviceLangSheet.value = true },
                    openVoiceSheet = { sheetState.isVoiceSheet.value = true }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "LENGO ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ${(BuildConfig.BUILD_TYPE).uppercase()}",
                    style = LengoNormal14body2(),
                    textAlign = TextAlign.Center
                )
            }

        }
        VerticleSpaceWithNav(16.dp)
    }


    BaseModalSheet(
        visible = sheetState.isLoginSheet.value,
        onDismiss = { sheetState.isLoginSheet.value = false }) {
        LoginSheet(viewModel = loginViewModel, onLoginComplete = {
            mainViewModel.refreshDataAfterLogin()
            sheetState.isLoginSheet.value = false
        }, onBack = {
            sheetState.isLoginSheet.value = false
        })
    }

    BaseModalSheet(visible = sheetState.isRegisterSheet.value, onDismiss = {
        sheetState.isRegisterSheet.value = false
    }) {
        RegisterSheet(viewModel = registerViewModel, onRegisterComplete = {
            sheetState.isRegisterSheet.value = false
        }, onBack = {
            sheetState.isRegisterSheet.value = false
        })
    }

    BaseModalSheet(
        visible = sheetState.isUserUpdateSheet.value,
        onDismiss = { sheetState.isUserUpdateSheet.value = false }) {
        UpdateUserSheet(updateUserViewModel, onUpdateComplete = {
            sheetState.isUserUpdateSheet.value = false
        }, onBack = {
            sheetState.isUserUpdateSheet.value = false
        })
    }

    BaseModalSheet(
        visible = sheetState.isCouponSteet.value,
        onDismiss = { sheetState.isCouponSteet.value = false }) {
        CouponSheet(couponViewModel, onBack = {
            sheetState.isCouponSteet.value = false
        })
    }

    DeviceLangSelectSheet(sheetState.isDeviceLangSheet.value) {
        sheetState.isDeviceLangSheet.value = false
    }


    VoicesSelectSelectSheet(visible = sheetState.isVoiceSheet.value, openSubSheet = {
        sheetState.subscriptionSheetState.value = SubscriptionSheetState(true, null)

    }, onDismiss = {
        sheetState.isVoiceSheet.value = false
    })

    SubscriptionModelSheet(
        subscriptionSheetState = sheetState.subscriptionSheetState.value
    ) {
        sheetState.subscriptionSheetState.value =
            sheetState.subscriptionSheetState.value.copy(isSubscriptionSheetVisible = false)
    }

    BuyCoinModelSheet(profileViewState.coinTypes,sheetState.buyCoinSheetState.value) {
        sheetState.buyCoinSheetState.value =
            sheetState.buyCoinSheetState.value.copy(isVisible = false)
    }

}

@Composable
fun ProfileBox(
    isUserLogin: Boolean = false,
    userNameorEmail: String = "",
    scoreString: String = "",
    progressPercent: Float = 0f,
    level: String = "level 1",
    openLoginSheet: () -> Unit,
    openRegisterSheet: () -> Unit,
    openUpdateUserSheet: () -> Unit,
) {
    Column {
        Text(
            stringResource(R.string.profile),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.onBackground,
            style = LengoSubHeading3().copy(textAlign = TextAlign.Left)
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column {

                if (userNameorEmail.isNotEmpty()) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        VerticleSpace(6.dp)

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            HorizontalSpace(12.dp)

                            Text(
                                modifier = Modifier.weight(1f),
                                text = userNameorEmail,
                                color = MaterialTheme.colors.onBackground,
                                style = MaterialTheme.typography.h5
                            )

                            IconButton(onClick = openUpdateUserSheet) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colors.onBackground
                                )
                            }
                        }
                    }
                } else {

                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp)), onClick = openRegisterSheet
                        ) {
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
                                .clip(RoundedCornerShape(8.dp)), onClick = openLoginSheet
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.LLogin),
                                style = LengoButtonText().copy(textAlign = TextAlign.Center),
                                maxLines = 1
                            )
                        }
                    }
                }
                VerticleSpace(8.dp)
                LevelAndPoints(level, scoreString, progressPercent)
            }

        }
    }
}

@Composable
private fun LevelAndPoints(
    level: String,
    scoreString: String,
    progressPercent: Float
) {
    Row {
        HorizontalSpace(12.dp)
        Text(
            modifier = Modifier.weight(1f),
            text = level,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            modifier = Modifier,
            text = scoreString,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.subtitle1
        )
        HorizontalSpace(12.dp)
    }

    VerticleSpace(12.dp)

    LinearProgressIndicator(
        progress = progressPercent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )

    VerticleSpace(12.dp)
}

@ExperimentalMaterialApi
@Composable
fun SettingBox(
    settingModel: SettingModel,
    flag: Int? = null,
    deviceLang: String? = null,
    changeModel: (SettingModel) -> Unit,
    openLangSheet: () -> Unit,
    openVoiceSheet: () -> Unit
) {

    Column {
        Text(
            stringResource(R.string.Einstellungen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.onBackground,
            style = LengoSubHeading3().copy(textAlign = TextAlign.Left)
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ListItem(
                    modifier = Modifier.clickable { openLangSheet() },
                    icon = {
                        if (flag != null) {
                            Image(
                                painter = painterResource(id = flag),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape),
                            )
                        }
                    },
                    text = {
                        Text(
                            deviceLang ?: stringResource(id = R.string.own_lng),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                )
                checkItem(
                    settingModel.pronounceEnable,
                    Icons.Filled.RecordVoiceOver,
                    stringResource(id = R.string.task_set_VOICE)
                ) {
                    changeModel(settingModel.copy(pronounceEnable = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.audioEnable,
                    Icons.Filled.SpeakerGroup,
                    stringResource(id = R.string.task_set_AUDIO)
                ) {
                    changeModel(settingModel.copy(audioEnable = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.darkThemeEnable ?: false,
                    Icons.Filled.Lightbulb,
                    stringResource(id = R.string.task_set_DARKMODE)
                ) {
                    changeModel(settingModel.copy(darkThemeEnable = it))
                }

                ListItem(
                    modifier = Modifier.clickable { openVoiceSheet() },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.RecordVoiceOver,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    },
                    text = {
                        Text(
                            stringResource(id = R.string.features_voices),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                )

            }
        }

    }

}


@ExperimentalMaterialApi
@Composable
fun Coupons(openCouponSheet: () -> Unit) {
    Column {
        Text(
            stringResource(R.string.general).uppercase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )

        Card(
            onClick = { openCouponSheet() },
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ListItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    },
                    text = {
                        Text(
                            stringResource(R.string.couponText),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                )

                Divider(color = MaterialTheme.colors.surface, thickness = 2.dp)
            }
        }


    }
}


@Composable
fun ChipStack(totalCoins: Int = 0,
              coinTypes: Coins = Coins(0, 0, 0),
              onBuyChip: () -> Unit) {
    Row(modifier = Modifier
        .padding(16.dp)
        .clickable {
            onBuyChip()
        },
        verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(65.dp)
                .height(48.dp)
                .graphicsLayer {
                    alpha = 0.99f // slight alpha to force compositing layer
                },
        ) {

            Coin(
                45.dp,
                CoinBronze,
                coinTypes.bronze.toString(),
                4.dp,
                LengoHeading4()
                    .copy(fontWeight = FontWeight.W600, fontSize = 24.sp),
                modifier = Modifier.offset(x = 10.dp)
            )

            Coin(
                45.dp,
                CoinSilver,
                coinTypes.silver.toString(),
                4.dp,
                LengoHeading4()
                    .copy(fontWeight = FontWeight.W600, fontSize = 24.sp),
                modifier = Modifier.offset(x = 5.dp)
            )

            Coin(
                45.dp,
                CoinGold,
                coinTypes.gold.toString(),
                4.dp,
                LengoHeading4()
                    .copy(fontWeight = FontWeight.W600, fontSize = 24.sp),
            )
        }

        HorizontalSpace(8.dp)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(id = R.string.my_coins),
                color = MaterialTheme.colors.onBackground
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ProvideTextStyle(MaterialTheme.typography.body2) {
                    Text("${totalCoins} ${stringResource(id = R.string.coins)}", color = MaterialTheme.colors.secondary)
                }
            }
        }

        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colors.onBackground
        )

    }

}


@ExperimentalMaterialApi
@Composable
fun BuyCoinsOrSub(
    totalCoins: Int = 0,
    coinTypes: Coins = Coins(0, 0, 0),
    billingError: String,
    subList: List<Subscription>,
    selLang: Lang,
    onSubscription: (sub: Subscription) -> Unit,
    onBuyChip: () -> Unit
) {

    Column {
        Text(
            stringResource(R.string.store),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.onBackground,
            style = LengoSubHeading3().copy(textAlign = TextAlign.Left)
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 0.dp)) {
                if (billingError.isNotEmpty()) {
                    Text(
                        billingError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.subtitle1
                    )
                } else {
                    if (!subList.isNullOrEmpty()) {
                        SubscriptionItems(subList, selLang, onSubscription)
                    } else {
                        LoadingBillingItems()
                    }
                    ChipStack(totalCoins, coinTypes,onBuyChip)
                }
            }
        }


    }
}



@Composable
private fun LoadingBillingItems() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@ExperimentalMaterialApi
@Composable
private fun SubscriptionItems(
    subList: List<Subscription>,
    selLang: Lang,
    onSubscription: (sub: Subscription) -> Unit,
) {
    subList.forEachIndexed { index, skuDetails ->
        if (skuDetails.title.value.isNullOrEmpty()) {
            return@forEachIndexed
        }
        if (selLang.code == skuDetails.lang || skuDetails.lang == "all") {

            val title = if (skuDetails.lang == "all") {
                stringResource(id = R.string.sub_polyglott_pro)
            } else {
                "${selLang.locale.displayLanguage} ${stringResource(id = R.string.pro)}"
            }

            val description = if (skuDetails.lang == "all") {
                "${stringResource(id = R.string.lockoutAll)}/\n${
                    stringResource(id = R.string.xxxLanguages).replace(
                        "xxx",
                        "29"
                    )
                }"
            } else {
                "${stringResource(id = R.string.lockoutAll)}/\n${selLang.locale.displayLanguage}"
            }

            ListItem(
                text = {
                    Text(
                        title,
                        color = MaterialTheme.colors.onBackground
                    )
                },
                secondaryText = {
                    Text(description, color = MaterialTheme.colors.secondary)
                },
                trailing = {
                    Column(horizontalAlignment = Alignment.End) {
                        if (skuDetails.subscribed.value != null) {
                            BuyTextChip(stringRes = if (!skuDetails.subscribed.value!!) R.string.abo else R.string.endAbo) {
                                onSubscription(skuDetails)
                            }
                            VerticleSpace(4.dp)
                        }
                        Text(
                            text = getAnnualMonthlyPrice(
                                skuDetails.yearlyOfferPrice?.value,
                                skuDetails.yearlyPriceAmountMicros?.value
                            ) ?: "",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary
                        )
                    }
                },
                icon = {
                    if (skuDetails.lang == "all") {
                        Coin(
                            48.dp,
                            CoinGold,
                            "∞",
                            style = LengoSubHeading2()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(
                                    4.dp, Color.White.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(
                                    id = getDrawableForLangCode(
                                        skuDetails.lang
                                    )
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                },
                modifier = Modifier.clickable {
                    onSubscription(skuDetails)
                }
            )

            if (index != subList.size - 1) {
                Divider(color = MaterialTheme.colors.surface, thickness = 2.dp)
            }
        }
    }
}





@Composable
fun Coin(
    size: Dp = 65.dp,
    color: Color,
    coins: String = "1",
    border: Dp = 4.dp,
    style: TextStyle = LengoHeading2(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)// clip to the circle shape
            .border(
                border,
                if (color == CoinGold) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                CircleShape
            )
    ) {

        Text(
            coins,
            modifier = Modifier.align(Alignment.Center),
            style = style.copy(Color.White.copy(alpha = 0.8f), fontSize = 20.sp)
        )
    }

}


@Composable
fun MyCoinsDemo() {
    LENGOTheme {
        // MyCoins()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun General(onClick: () -> Unit) {
    Column {
        Text(
            stringResource(R.string.general),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colors.onBackground,
            style = LengoSubHeading3().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            ListItem(
                modifier = Modifier.clickable { onClick() },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.RecordVoiceOver,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                },
                text = {
                    Text(
                        stringResource(id = R.string.features_voices),
                        color = MaterialTheme.colors.onBackground
                    )
                },
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            )
        }
    }
}







