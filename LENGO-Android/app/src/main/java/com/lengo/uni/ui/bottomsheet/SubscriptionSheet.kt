package com.lengo.uni.ui.bottomsheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.UnSubAppContract
import com.lengo.common.get12MonthsString
import com.lengo.common.get1MonthString
import com.lengo.common.getDrawableForLangCode
import com.lengo.common.subscriptionsList
import com.lengo.common.ui.HorizontalSpace
import com.lengo.common.ui.LengoButton
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.Lang
import com.lengo.model.data.Subscription
import com.lengo.uni.ui.profile.Coin
import com.lengo.common.ui.theme.CoinGold
import com.lengo.common.ui.theme.LengoBold20
import com.lengo.common.ui.theme.LengoHeading5
import com.lengo.common.ui.theme.LengoRegular18h4
import com.lengo.common.ui.theme.LengoSemiBold16h4
import com.lengo.common.ui.theme.LengoSemiBold18h4
import com.lengo.uni.ui.sheet.BenifitItem
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

@Composable
fun SubscriptionSheet(
    sub: Subscription? = null,
    userSelectedLang: Lang? = null,
    deviceLang: String? = null,
    onSubscription: (sku: String, offerToken: String) -> Unit,
    onBack: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(UnSubAppContract()) {
    }
    var subscription by remember(sub) { mutableStateOf(sub) }
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


    LaunchedEffect(subscription, userSelectedLang) {
        if (subscription != null) {
            title = if (subscription!!.lang == "all") {
                isAllLang = true
                allSubTitle
            } else {
                isAllLang = false
                val name = userSelectedLang?.locale?.displayLanguage ?: ""
                titleTemp.replace("_LAN_", name)
            }
            isSelectedPlanFreeTrailAvailable =
                subscription!!.isYearlyFreeTrailAvailable.value ?: false
        } else {
            isAllLang = false
            val name = userSelectedLang?.locale?.displayLanguage ?: ""
            title = titleTemp.replace("_LAN_", name)
            subscription = subscriptionsList.find { it.lang == userSelectedLang!!.code }
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
        SheetAppBar(title, onBack)

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
                    if (userSelectedLang != null) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .border(
                                    6.5.dp, Color.White.copy(alpha = 0.6f),
                                    CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(
                                    id = getDrawableForLangCode(userSelectedLang.code)
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
                    style = LengoBold20().copy(fontSize = 25.sp), modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                )

                SubBenifitList(isAllLang)

                OutlineBox(
                    deviceLang?.let { lang -> get12MonthsString(lang) },
                    getAnnualMonthlyPrice(
                        subscription?.yearlyOfferPrice?.value,
                        subscription?.yearlyPriceAmountMicros?.value
                    )?.let { price -> stringResource(id = R.string.price_per_month).replace(
                        "_PRICE_",
                        price
                    )},
                    deviceLang?.let { lang ->
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
                    deviceLang?.let { lang -> get1MonthString(lang) },
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
                            launcher.launch(UnSubAppContract.UnSubAppContractInput(userSelectedLang?.code!!,subscription?.productId!!))
                            return@let
                        }
                        if (isYearlyPlanSelected) {
                            if (isSelectedPlanFreeTrailAvailable) {
                                if (it.yearlyFreeTrialOfferToken.value != null) {
                                    onSubscription(
                                        it.productId,
                                        it.yearlyFreeTrialOfferToken.value!!
                                    )
                                }
                            } else {
                                if(it.yearlyOfferToken.value != null) {
                                    onSubscription(
                                        it.productId,
                                        it.yearlyOfferToken.value!!
                                    )
                                }
                            }
                        } else {
                            if (isSelectedPlanFreeTrailAvailable) {
                                if (it.monthlyFreeTrialOfferToken.value != null) {
                                    onSubscription(
                                        it.productId,
                                        it.monthlyFreeTrialOfferToken.value!!
                                    )
                                }
                            } else {
                                if(it.monthlyOfferToken.value != null) {
                                    onSubscription(
                                        it.productId,
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


@Composable
fun OutlineBox(text1: String?,
               text2: String?,
               text3: String?,
               originalPrice: String?,
               isSelected: Boolean,
               savePercentage: String? = null,
               onClick: () -> Unit) {
    Box(modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(),
            border = BorderStroke(
                 5.dp,
                if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                text1?.let { lang ->
                    Text(
                        text1,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colors.onBackground,
                        style = LengoSemiBold18h4().copy(fontWeight = FontWeight.W700,fontSize = 20.sp), modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                text2?.let { text2 ->
                    Text(
                        text2,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colors.onBackground,
                        style = LengoSemiBold16h4().copy(fontSize = 13.sp), modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    originalPrice?.let {
                        Text(
                            originalPrice,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colors.secondary,
                            style = LengoRegular18h4().copy(fontSize = 12.sp, textDecoration = TextDecoration.LineThrough),
                            )

                        HorizontalSpace(width = 4.dp)

                    }

                    text3?.let { text3 ->
                        Text(
                            text3,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colors.onBackground,
                            style = LengoRegular18h4().copy(fontSize = 12.sp),

                        )
                    }
                }

            }
        }
        if(!savePercentage.isNullOrEmpty()) {
            Text(
                stringResource(id = R.string.spare_x_percent).replace(
                    "_XPERCENT_",
                    "${savePercentage}%"
                ), modifier = Modifier
                    .background(
                        if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                        RoundedCornerShape(20)
                    )
                    .padding(vertical = 4.dp, horizontal = 6.dp)
                    .align(Alignment.TopEnd),
                color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onBackground
            )
        }
    }
}


@Composable
fun SubBenifitList(isAllLang: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 70.dp)
    ) {
        if (isAllLang)
            BenifitItem(stringResource(id = R.string.xxxLanguages).replace("xxx", "29"))
        BenifitItem(stringResource(id = R.string.sub_allVocabulary))
        BenifitItem(stringResource(id = R.string.sub_allGrammar))
        BenifitItem(stringResource(id = R.string.all_features))
    }
}

fun getAnnualMonthlyPrice(price: String?, priceInMicros: Long?): String? {
    if (price == null || priceInMicros == null) return null
    return try {
        val formatter = DecimalFormat("###,###,##0.00")
        val priceSplit = price.split("\\s".toRegex())
        val priceMicro = priceInMicros / (1_000_000f * 12f)
        if(priceSplit[0][0].isDigit()) {
           "${formatter.format(priceMicro)} ${priceSplit[1]}"
        } else {
           "${priceSplit[0]} ${formatter.format(priceMicro)}"
        }
    } catch (ex: Exception) {
        null
    }
}

fun getOriginalYearlyPrice(price: String?, priceInMicros: Long?): String? {
    if (price == null || priceInMicros == null) return null
    return try {
        val formatter = DecimalFormat("###,###,##0.00")
        val priceSplit = price.split("\\s".toRegex())
        val priceMicro = (priceInMicros / (1_000_000f)) * 12f
        if(priceSplit[0][0].isDigit()) {
            "${formatter.format(priceMicro)} ${priceSplit[1]}"
        } else {
            "${priceSplit[0]} ${formatter.format(priceMicro)}"
        }
    } catch (ex: Exception) {
        null
    }
}

fun main() {
    var string = "Rs   +-2,9sss  0 0.00"
    var res = string.filter { it.isDigit() || it == '.' }.toDouble() / 12.0
    // val format = NumberFormat.getCurrencyInstance(Locale("en", "US")).format(res)
    val nf = NumberFormat.getCurrencyInstance()
    val decimalFormatSymbols: DecimalFormatSymbols = (nf as DecimalFormat).decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = ""
    nf.decimalFormatSymbols = decimalFormatSymbols
    println(nf.format(res).trim { it <= ' ' })
    //println(format)
}