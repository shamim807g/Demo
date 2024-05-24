package com.lengo.data.datasource

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.di.ApplicationScope
import com.lengo.common.inAppList
import com.lengo.common.inAppProductIds
import com.lengo.common.subsProductIds
import com.lengo.common.subscriptionsList
import com.lengo.common.updateItem
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
data class BillingDataSource @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(LengoDispatchers.Main) val mainDispatcher: CoroutineDispatcher,
    private val lengoPreference: LengoPreference,
    @ApplicationScope val appScope: CoroutineScope,
    private val userDoa: UserDoa
) : PurchasesUpdatedListener, BillingClientStateListener,
    DefaultLifecycleObserver {

    private val TAG = "BillingDataSource"
    private var reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
    private val productDetailsMap: MutableMap<String, ProductDetails?> = HashMap()
    private var billingClient: BillingClient? = null
    private val billingFlowInProcess = MutableStateFlow(false)
    var isNewSubEnable: MutableStateFlow<Int> = MutableStateFlow(0)
    val billingError = MutableStateFlow<String>("")

    fun initBillingClient() {
        if ((billingClient != null && !billingClient!!.isReady) || billingClient == null) {
            logcat(TAG) { "initBillingClient" }
            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build()
        }
        billingClient?.startConnection(this)
    }

    override fun onBillingServiceDisconnected() {
        logcat { "onBillingServiceDisconnected" }
        retryBillingServiceConnectionWithExponentialBackoff()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        logcat { "onBillingSetupFinished ${billingResult.responseCode} ${billingResult.debugMessage}" }
        val response = BillingResponse(billingResult.responseCode)
        when {
            response.isOk -> {
                logcat { "billingError OK" }
                billingError.tryEmit("")
                appScope.launch(ioDispatcher) {
                    querySkuDetailsAsync()
                    querySubDetailsAsync()
                    querySubscriptionsPurchasesAsync()
                    queryInAppPurchasesAsync()
                }
            }
            response.canFailGracefully -> {
                logcat { "billingError ITEM ALREADY OWNED" }
                billingError.tryEmit("ITEM ALREADY OWNED")
            }
            response.isRecoverableError -> {
                logcat { "billingError isRecoverableError" }
                retryBillingServiceConnectionWithExponentialBackoff()
            }
            response.isNonrecoverableError || response.isTerribleFailure -> {
                logcat { "BILLING UNAVAILABLE" }
                billingError.tryEmit("BILLING UNAVAILABLE")
            }
        }
    }

    private fun retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed({ billingClient?.startConnection(this) }, reconnectMilliseconds)
        reconnectMilliseconds =
            min(reconnectMilliseconds * 2, RECONNECT_TIMER_MAX_TIME_MILLISECONDS)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                appScope.launch { handlePurchase(purchase) }
            }
        } else {
            // Handle any other error codes.
        }
    }

    suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (inAppProductIds.contains(purchase.products.stream().findFirst().orElse(""))) {
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                val consumeResult = withContext(ioDispatcher) {
                    billingClient?.consumePurchase(consumeParams)
                }
                if (consumeResult?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    insertCoins(purchase)
                }
            } else if (subsProductIds.contains(purchase.products.stream().findFirst().orElse(""))) {
                if (!purchase.isAcknowledged) {
                    logcat(TAG) { "not isAcknowledged" }
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    val ackPurchaseResult = withContext(ioDispatcher) {
                        billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
                    }
                    if (ackPurchaseResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                        querySubscriptionsPurchasesAsync()
                        lengoPreference.setFreeTrailAvail()
                    }
                }
            }
        }
        billingFlowInProcess.emit(false)
    }


    suspend fun insertCoins(purchase: Purchase) {
        for (sku in purchase.products) {
            when (sku) {
                SKU_COIN_SILVER_1 -> {
                    userDoa.addCoins(100)
                }
                SKU_COIN_BRONZE_50 -> {
                    userDoa.addCoins(50)
                }
                SKU_COIN_BRONZE_10 -> {
                    userDoa.addCoins(10)
                }
                SKU_COIN_SILVER_20 -> {
                    userDoa.addCoins(2000)
                }
            }
        }
    }

    private suspend fun querySubDetailsAsync() {
        val subSkulist = subscriptionsList.map { sku ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(sku.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(subSkulist).build()

        val skuDetailsResult = billingClient?.queryProductDetails(
            queryProductDetailsParams
        )

        val responseCode = skuDetailsResult?.billingResult?.responseCode!!
        val debugMessage = skuDetailsResult.billingResult.debugMessage ?: ""

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val skuDetailsList = skuDetailsResult.productDetailsList ?: emptyList()
                subscriptionsList.forEach { sub ->
                    val skd = skuDetailsList.find { sub.productId == it.productId }
                    if (skd != null) {
                        productDetailsMap[skd.productId] = skd
                        withContext(mainDispatcher) {
                            sub.updateItem(skd)
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> Log.e(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Log.wtf(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            else -> Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
        }
    }

    private suspend fun querySkuDetailsAsync() {
        val inAppSkulist = inAppList.map { sku ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(sku.sku)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(inAppSkulist).build()

        val skuDetailsResult = billingClient?.queryProductDetails(
            queryProductDetailsParams
        )

        val responseCode = skuDetailsResult?.billingResult?.responseCode!!
        val debugMessage = skuDetailsResult.billingResult.debugMessage ?: ""

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val skuDetailsList = skuDetailsResult.productDetailsList ?: emptyList()
                inAppList.forEach { item ->
                    val skuDetail = skuDetailsList.find { it.productId == item.sku }
                    if (skuDetail != null) {
                        productDetailsMap[skuDetail.productId] = skuDetail
                        withContext(mainDispatcher) {
                            item.updateItem(skuDetail)
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> Log.e(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Log.wtf(
                TAG,
                "onSkuDetailsResponse: $responseCode $debugMessage"
            )
            else -> Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
        }

    }


    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        logcat(TAG) { "resume" }
        if (billingClient != null && billingClient!!.isReady && !billingFlowInProcess.value) {
            logcat(TAG) { "billingClient is ready" }
            querySubscriptionsPurchasesAsync()
            queryInAppPurchasesAsync()
        } else {
            logcat(TAG) { "billingClient is not ready" }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient != null && billingClient!!.isReady) {
            logcat(TAG) { "billingClient onDestroy" }
            billingClient?.endConnection()
        }
        super.onDestroy(owner)
    }


    fun launchPurchaseFlow(activity: Activity, sku: String, isSub: Boolean, offerToken: String? = null) {
        updateBillingFlowProcess(true)
        val skuDetails = productDetailsMap[sku]
        if (skuDetails != null) {
            var productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams>? = null
            if (isSub && offerToken != null) {
                val isAllowed = areSubscriptionsSupported()
                if (isAllowed) {
                    if (!offerToken.isNullOrEmpty()) {
                        productDetailsParamsList =
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(skuDetails)
                                    .setOfferToken(offerToken)
                                    .build()
                            )
                    } else {
                        updateBillingFlowProcess(false)
                    }
                } else {
                    updateBillingFlowProcess(false)
                }

            } else {
                productDetailsParamsList =
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(skuDetails)
                            .build()
                    )
            }
            if (productDetailsParamsList != null) {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                val responseCode =
                    billingClient?.launchBillingFlow(activity, billingFlowParams)?.responseCode
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    updateBillingFlowProcess(false)
                }
            } else {
                updateBillingFlowProcess(false)
            }
        } else {
            updateBillingFlowProcess(false)
        }
    }

    private fun updateBillingFlowProcess(isEnable: Boolean) {
        appScope.launch { billingFlowInProcess.emit(isEnable) }
    }

    private fun areSubscriptionsSupported(): Boolean {
        val billingResult = billingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        return billingResult?.responseCode == BillingClient.BillingResponseCode.OK
    }

    fun querySubscriptionsPurchasesAsync() {
        logcat(TAG) { "queryPurchasesAsync ${billingClient}" }
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchaes ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                logcat(TAG) { "queryPurchasesAsync BillingResponseCode.OK ${purchaes}" }
                subscriptionsList.forEach { sub ->
                    val purchase = purchaes.find {
                        val productId = it.products.stream().findFirst().orElse("")
                        sub.productId == productId
                    }
                    sub.subscribed.value = purchase != null
                    if (purchase != null) {
                        appScope.launch {
                            handlePurchase(purchase)
                        }
                    }
                }
                isNewSubEnable.value = isNewSubEnable.value + 1
                logcat { "current sub ${subscriptionsList}" }
            }
        }
    }

    fun queryInAppPurchasesAsync() {
        logcat(TAG) { "queryInAppPurchasesAsync ${billingClient}" }
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchaes ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                logcat(TAG) { "queryPurchasesAsync BillingResponseCode.OK ${purchaes}" }
                inAppList.forEach { item ->
                    val purchase = purchaes.find {
                        val productId = it.products.stream().findFirst().orElse("")
                        item.sku == productId
                    }
                    item.canPurchase.value = purchase == null
                    if (purchase != null) {
                        appScope.launch {
                            handlePurchase(purchase)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val handler = Handler(Looper.getMainLooper())
        private const val RECONNECT_TIMER_START_MILLISECONDS = 1L * 1000L
        private const val RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L // 15 minutes
        private const val SKU_DETAILS_REQUERY_TIME = 1000L * 60L * 60L * 4L // 4 hours

        const val SKU_COIN_SILVER_20 = "coin.silver_20_new"
        const val SKU_COIN_BRONZE_10 = "coin.bronze_10_new"
        const val SKU_COIN_SILVER_1 = "coin.silver_1_new"
        const val SKU_COIN_BRONZE_50 = "coin.bronze_50_new"
    }

}

private class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}