package com.lengo.uni.ui.sheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.PLACEHOLDER
import com.lengo.common.R
import com.lengo.common.UnSubAppContract
import com.lengo.common.get12MonthsString
import com.lengo.common.get1MonthString
import com.lengo.common.getDrawableForLangCode
import com.lengo.common.subscriptionsList
import com.lengo.common.ui.ImageCard
import com.lengo.common.ui.LengoButton
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.theme.CoinGold
import com.lengo.common.ui.theme.LengoBold20
import com.lengo.common.ui.theme.LengoHeading5
import com.lengo.common.ui.theme.LengoSemiBold16h4
import com.lengo.model.data.Pack
import com.lengo.model.data.Subscription
import com.lengo.uni.BuildConfig
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.bottomsheet.OutlineBox
import com.lengo.uni.ui.bottomsheet.SubBenifitList
import com.lengo.uni.ui.bottomsheet.getAnnualMonthlyPrice
import com.lengo.uni.ui.bottomsheet.getOriginalYearlyPrice
import com.lengo.uni.ui.profile.Coin


@Composable
fun FreeTrailSheet() {
    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel
    val navigator = LocalNavigator.current
    val mainState = LocalAppState.current

    val launcher = rememberLauncherForActivityResult(UnSubAppContract()) {
    }
    var subscription: Subscription? by remember { mutableStateOf(null) }

    var isAllLang by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    val titleTemp = stringResource(id = R.string.languagePlan)
    val allSubTitle = stringResource(id = R.string.sub_polyglott_pro)
    var isSelectedPlanFreeTrailAvailable by remember { mutableStateOf(false) }
    var isYearlyPlanSelected by remember { mutableStateOf(true) }
    val savePercentage by remember(subscription) {
        derivedStateOf {
            if(subscription != null) {
                //(35600 / (4450 * 12) * 100) - 100
                val monthly = subscription?.monthlyPriceAmountMicros?.value?.times(12)
                val yearly = subscription?.yearlyPriceAmountMicros?.value
                if(yearly != null && monthly != null) {
                    "${(100 - ((yearly / monthly.toDouble()) * 100).toInt())}"
                } else { "" }
            } else { "" }
        }
    }

    LaunchedEffect(Unit) {
        if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
            subscription = subscriptionsList.find { it.lang == FLAVOUR_TYPE_ALL }
            isAllLang = true
            title = allSubTitle
            isSelectedPlanFreeTrailAvailable =
                subscription!!.isYearlyFreeTrailAvailable.value ?: false
        } else {
            isAllLang = false
            val name = mainState.userSelectedLang?.locale?.displayLanguage ?: ""
            title = titleTemp.replace("_LAN_", name)
            subscription = subscriptionsList.find { it.lang == mainState.userSelectedLang?.code }
            isSelectedPlanFreeTrailAvailable =
                subscription!!.isYearlyFreeTrailAvailable.value ?: false
        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SheetAppBar(title) {
            if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
                navigator.navigate(Screen.OnboardingSelectLang.route)
            } else {
                navigator.navigate(Screen.Dashboard.route)
            }
            mainViewModel.markSubSheetShown()
        }

        Box(Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VerticleSpace(20.dp)

                if (isAllLang) {
                    Coin(
                        75.dp,
                        CoinGold,
                        "∞",
                        6.5.dp,
                        style = LengoHeading5()
                    )
                } else {
                    if (mainState.userSelectedLang != null) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .border(
                                    6.5.dp, Color.White.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(
                                    id = getDrawableForLangCode(mainState?.userSelectedLang.code)
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }

                VerticleSpace(16.dp)

                Text(
                    if (isSelectedPlanFreeTrailAvailable) stringResource(id = R.string.seven_days_free) else stringResource(
                        id = R.string.sub_serious
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground,
                    letterSpacing = 0.4.sp,
                    style = LengoBold20().copy(fontSize = 24.sp), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                )

                SubBenifitList(isAllLang)

                OutlineBox(
                    mainState?.deviceLang?.let { lang -> get12MonthsString(lang) },
                    getAnnualMonthlyPrice(
                        subscription?.yearlyOfferPrice?.value,
                        subscription?.yearlyPriceAmountMicros?.value
                    )?.let { price -> stringResource(id = R.string.price_per_month).replace(
                        "_PRICE_",
                        price
                    )},
                    mainState?.deviceLang?.let { lang ->
                        "${subscription?.yearlyOfferPrice?.value} ${
                            stringResource(id = R.string.billed_every_x_months).replace(
                                "_XMTHS_",
                                get12MonthsString(lang)
                            )
                        }"
                    },
                    getOriginalYearlyPrice(
                        subscription?.monthlyOfferPrice?.value,
                        subscription?.monthlyPriceAmountMicros?.value,
                    ),
                    isYearlyPlanSelected,
                    savePercentage
                ) {
                    isYearlyPlanSelected = true
                    isSelectedPlanFreeTrailAvailable =
                        subscription?.isYearlyFreeTrailAvailable?.value ?: false
                }


                VerticleSpace(8.dp)

                OutlineBox(
                    mainState?.deviceLang?.let { lang -> get1MonthString(lang) },
                    subscription?.monthlyOfferPrice?.value?.let { price ->
                        stringResource(id = R.string.price_per_month).replace(
                            "_PRICE_",
                            price
                        ) },
                    stringResource(id = R.string.billed_monthly),
                    null,
                    !isYearlyPlanSelected,
                    null
                ) {
                    isYearlyPlanSelected = false
                    isSelectedPlanFreeTrailAvailable =
                        subscription?.isMonthlyFreeTrailAvailable?.value ?: false
                }

            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
            ) {
                LengoButton(
                    Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(id = R.string.letsGo)
                ) {
                    subscription?.let {
                        if(it.subscribed.value == true) {
                            launcher.launch(UnSubAppContract.UnSubAppContractInput(mainState?.userSelectedLang?.code!!,subscription?.productId!!))
                            return@let
                        }
                        if (isYearlyPlanSelected) {
                            if (isSelectedPlanFreeTrailAvailable) {
                                if (it.yearlyFreeTrialOfferToken.value != null) {
                                    subscription?.let {
                                        activity.billingDataSource.launchPurchaseFlow(
                                            activity,
                                            it.productId,
                                            true,
                                            it.yearlyFreeTrialOfferToken.value!!
                                        )
                                    }
                                }
                            } else {
                                if(it.yearlyOfferToken.value != null) {
                                    subscription?.let {
                                        activity.billingDataSource.launchPurchaseFlow(
                                            activity,
                                            it.productId,
                                            true,
                                            it.yearlyOfferToken.value!!
                                        )
                                    }
                                }
                            }
                        } else {
                            if (isSelectedPlanFreeTrailAvailable) {
                                if (it.monthlyFreeTrialOfferToken.value != null) {
                                    subscription?.let {
                                        activity.billingDataSource.launchPurchaseFlow(
                                            activity,
                                            it.productId,
                                            true,
                                            it.monthlyFreeTrialOfferToken.value!!
                                        )
                                    }
                                }
                            } else {
                                if(it.monthlyOfferToken.value != null) {
                                    subscription?.let {
                                        activity.billingDataSource.launchPurchaseFlow(
                                            activity,
                                            it.productId,
                                            true,
                                            it.monthlyOfferToken.value!!
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PaidPacks(
    packs: SnapshotStateList<Pack>,
    imageMap: SnapshotStateMap<String, String>
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val hScrollState = rememberScrollState()
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(hScrollState)
            .padding(horizontal = 8.dp)
    ) {
        packs.forEach { pack ->
            pack.lections.forEach { lec ->
                key(
                    lec.lang,
                    lec.lec,
                    lec.owner,
                    lec.pck,
                    lec.type,
                    imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                        ?: PLACEHOLDER
                ) {
                    ImageCard(
                        modifier = Modifier
                            .width(screenWidth.dp.minus(32.dp))
                            .aspectRatio(16 / 9f),
                        name = lec.title,
                        image = imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                            ?: PLACEHOLDER
                    ) {

                    }
                }
            }
        }

    }
}

@Composable
private fun BenifitList() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BenifitItem(stringResource(id = R.string.sub_allVocabulary))
            BenifitItem(stringResource(id = R.string.sub_allGrammar))
            BenifitItem(stringResource(id = R.string.sub_noAdvertising))
            BenifitItem(stringResource(id = R.string.unlimPacks))
            BenifitItem(stringResource(id = R.string.sub_monitorProgress))
        }

    }
}

@Composable
fun BenifitItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp),
            tint = MaterialTheme.colors.primary
        )

        Text(
            text,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onBackground,
            style = LengoSemiBold16h4(), modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .weight(1f)
        )
    }
}