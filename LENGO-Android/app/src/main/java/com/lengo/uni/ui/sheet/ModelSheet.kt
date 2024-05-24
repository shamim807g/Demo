package com.lengo.uni.ui.sheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.PlayStoreAppContract
import com.lengo.common.extension.Coins
import com.lengo.uni.BuildConfig
import com.lengo.common.ui.bottomsheet.BottomSheetWithCloseDialog
import com.lengo.model.data.LectionId
import com.lengo.model.data.Subscription
import com.lengo.uni.ui.bottomsheet.*
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.utils.LocaleHelper


//@Composable
//fun LangSelectSheet(visible: Boolean, onDismiss: () -> Unit) {
//    val launcher = rememberLauncherForActivityResult(PlayStoreAppContract()) {
//    }
//    val activity = (LocalContext.current as MainActivity)
//    val mainViewModel = activity.mainViewModel
//    val appState = LocalAppState.current
//
//    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
//        LanguageSelectionSheet(appState.allLanguage, {
//            if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
//                activity.mainViewModel.syncDataWithServer()
//                mainViewModel.selLanguageSelected(it)
//            } else {
//                launcher.launch(it.code)
//            }
//            onDismiss()
//        }) {
//            onDismiss()
//        }
//    }
//}

@Composable
fun DeviceLangSelectSheet(visible: Boolean, onDismiss: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel

    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        DeviceLanguageSelectionSheet({
            LocaleHelper.setLocale(activity,it.code)
            mainViewModel.ownLanguageSelected(it.code)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(it.code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }) {
            onDismiss()
        }
    }
}

@Composable
fun VoicesSelectSelectSheet(visible: Boolean, onDismiss: () -> Unit,openSubSheet: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    val appState = LocalAppState.current

    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        LaunchedEffect(key1 = Unit, block = {
            mainViewModel.getAllVoices()
        })
        VoiceSelectionSheet(offlineVoice = appState.offlineVoice,voices = appState.voices,
            onItemSelected = { voice ->
            mainViewModel.updateVoice(voice)
        },openSubSheet = openSubSheet,
            playVoice = { mainViewModel.playVoice(it) }) {
            onDismiss()
        }
    }
}

@Immutable
data class SubscriptionSheetState(
    val isSubscriptionSheetVisible: Boolean,
    val subscription: Subscription? = null,
)

@Immutable
data class BuyCoinSheetState(
    val isVisible: Boolean = false
)


@Stable
data class BottomSheetVisibleState(
    var isLoginSheet: MutableState<Boolean> = mutableStateOf(false),
    var isRegisterSheet: MutableState<Boolean> = mutableStateOf(false),
    var isUserUpdateSheet: MutableState<Boolean> = mutableStateOf(false),
    var isCouponSteet: MutableState<Boolean> = mutableStateOf(false),
    var isDeviceLangSheet: MutableState<Boolean> = mutableStateOf(false),
    var isVoiceSheet: MutableState<Boolean> = mutableStateOf(false),
    var subscriptionSheetState: MutableState<SubscriptionSheetState> = mutableStateOf(SubscriptionSheetState(false,null)),
    var isFreeTrailSheet: MutableState<Boolean> = mutableStateOf(false),
    var buyCoinSheetState: MutableState<BuyCoinSheetState> = mutableStateOf(BuyCoinSheetState()),
)



@Composable
fun DashboardReviewSheet(visible: Boolean, onDismiss: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        DashboardReviewSheet({
            onDismiss()
            activity.initReviews()
        }, {
            mainViewModel.reviewSubmitted()
            onDismiss()
        })
    }
}

@Composable
fun DownloadLangModelSheet(visible: Boolean, onDismiss: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        BottomSheetWithCloseDialog(onClosePressed = onDismiss) {
            DownloadLangSheet(ok = {
                onDismiss()
                activity.launchDownloadTTS()
            }, cancel = {
                onDismiss()
            })
        }
    }
}

@Composable
fun PackReviewModalSheet(lectionId: LectionId?, visible: Boolean, onDismiss: () -> Unit) {
    if (lectionId == null) return
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        BottomSheetWithCloseDialog(onClosePressed = onDismiss) {
            PackReviewSheet(lectionId.packName, close = onDismiss,
                submit = { rating, review ->
                    onDismiss()
                    mainViewModel.submitPackReviewRating(
                        lectionId,
                        rating,
                        review
                    )
                })
        }
    }
}


@Composable
fun SubscriptionModelSheet(subscriptionSheetState: SubscriptionSheetState, onDismiss: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    val appState = LocalAppState.current
    BaseModalSheet(visible = subscriptionSheetState.isSubscriptionSheetVisible, onDismiss = onDismiss) {
        SubscriptionSheet(
            sub = subscriptionSheetState.subscription,
            userSelectedLang = appState.userSelectedLang,
            deviceLang = appState.deviceLang,
            onSubscription = { sku, offerToken ->
                activity.billingDataSource.launchPurchaseFlow(
                    activity,
                    sku,
                    true,
                    offerToken
                )
            }
        ) {
            onDismiss()
        }
    }

}


@Composable
fun BuyCoinModelSheet(coinType: Coins, buyCoinSheetState: BuyCoinSheetState, onDismiss: () -> Unit) {
    val activity = (LocalContext.current as MainActivity)
    val appState = LocalAppState.current
    BaseModalSheet(visible = buyCoinSheetState.isVisible, onDismiss = onDismiss) {
        BuyCoinSheet(
            coinType = coinType, onPurchase = { sku ->
                activity.billingDataSource.launchPurchaseFlow(
                    activity,
                    sku,
                    false,
                    null
                )
            }) {
            onDismiss()
        }
    }

}