package com.lengo.common

import com.android.billingclient.api.ProductDetails
import com.lengo.model.data.BillingInAppItem
import com.lengo.model.data.Lang
import com.lengo.model.data.Subscription
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf

val inAppProductIds = persistentListOf(
    "coin.silver_20_new",
    "coin.bronze_10_new",
    "coin.silver_1_new",
    "coin.bronze_50_new"
)

val subsProductIds = persistentListOf(
    "sub_all_lang",
    "sub_en_lang",
    "sub_us_lang",
    "sub_de_lang",
    "sub_cn_lang",
    "sub_it_lang",
    "sub_pt_lang",
    "sub_se_lang",
    "sub_pl_lang",
    "sub_th_lang",
    "sub_ar_lang",
    "sub_da_lang",
    "sub_el_lang",
    "sub_fi_lang",
    "sub_fr_lang",
    "sub_ja_lang",
    "sub_nl_lang",
    "sub_ru_lang",
    "sub_ua_lang",
    "sub_tr_lang",
    "sub_es_lang",
    "sub_no_lang",
    "sub_ko_lang",
    "sub_cz_lang",
    "sub_sk_lang",
    "sub_ro_lang",
    "sub_bg_lang",
    "sub_sr_lang",
    "sub_vi_lang",
    "sub_hu_lang",
)
//= ∞ Coins/Dutch




private val subList = persistentListOf(
    Subscription("sub_all_lang", "all"),
    Subscription("sub_en_lang", "en"),//English //english
    Subscription("sub_us_lang", "us"),//English USA //englishUS
    Subscription("sub_de_lang", "de"),//German
    Subscription("sub_cn_lang", "cn"),//Chinese
    Subscription("sub_it_lang", "it"),//Italian
    Subscription("sub_pt_lang", "pt"),//Portuguese
    Subscription("sub_se_lang", "se"),//Swedish
    Subscription("sub_pl_lang", "pl"),//Polish
    Subscription("sub_th_lang", "th"),//Thai
    Subscription("sub_ar_lang", "ar"),//Arabic
    Subscription("sub_da_lang", "da"),//Danish
    Subscription("sub_el_lang", "el"),//Greek
    Subscription("sub_fi_lang", "fi"),//Finnish
    Subscription("sub_fr_lang", "fr"),//French
    Subscription("sub_ja_lang", "ja"),//Japanese
    Subscription("sub_nl_lang", "nl"),//Dutch
    Subscription("sub_ru_lang", "ru"),//Russian
    Subscription("sub_ko_lang", "ko"),//Korean
    Subscription("sub_es_lang", "es"),//Spanish
    Subscription("sub_no_lang", "no"),//Norwegian Bokmål
    Subscription("sub_ua_lang", "ua"),//Ukrainian
    Subscription("sub_tr_lang", "tr"),//Turkish
    Subscription("sub_cz_lang", "cz"),//Czech
    Subscription("sub_sk_lang", "sk"),//Slovak
    Subscription("sub_ro_lang", "ro"),//Romanian
    Subscription("sub_bg_lang", "bg"),//Bulgarian
    Subscription("sub_sr_lang", "sr"),//Serbian
    Subscription("sub_vi_lang", "vi"),//Vietnamese
    Subscription("sub_hu_lang", "hu"),//Hungarian
)

val inAppList: ImmutableList<BillingInAppItem> = persistentListOf(
    BillingInAppItem(sku = "coin.silver_20_new"),
    BillingInAppItem(sku = "coin.silver_1_new"),
    BillingInAppItem(sku = "coin.bronze_50_new"),
    BillingInAppItem(sku = "coin.bronze_10_new"),
)

fun BillingInAppItem.updateItem(skuDetails: ProductDetails) {
    this.title.value = skuDetails.title
    this.description.value = skuDetails.description
    this.price.value = skuDetails.oneTimePurchaseOfferDetails?.formattedPrice
}

fun Subscription.updateItem(skuDetails: ProductDetails) {
    this.title.value = skuDetails.title
    this.description.value = skuDetails.description
    skuDetails.subscriptionOfferDetails?.forEach { productDetail ->
        if(productDetail.basePlanId == "p1m") {
            if(productDetail.offerTags.contains("freetrial")) {
                this.isMonthlyFreeTrailAvailable.value = true
                this.monthlyFreeTrialOfferToken.value = productDetail.offerToken
            } else {
                this.monthlyOfferToken.value = productDetail.offerToken
                productDetail.pricingPhases.pricingPhaseList.forEach {
                    if (it.billingPeriod == "P1M") {
                        this.monthlyOfferPrice.value = it.formattedPrice
                        this.monthlyPriceAmountMicros.value = it.priceAmountMicros
                    }
                }
            }
        } else if(productDetail.basePlanId == "p1y") {
            if(productDetail.offerTags.contains("freetrial")) {
                this.isYearlyFreeTrailAvailable.value = true
                this.yearlyFreeTrialOfferToken.value = productDetail.offerToken
            } else {
                this.yearlyOfferToken.value = productDetail.offerToken
                productDetail.pricingPhases.pricingPhaseList.forEach {
                    if (it.billingPeriod == "P1Y") {
                        this.yearlyOfferPrice.value = it.formattedPrice
                        this.yearlyPriceAmountMicros.value = it.priceAmountMicros
                    }
                }
            }

        }
    }
}

fun isUserLangSubscribe(userLang: Lang): Boolean {
    return subList.find { it.lang == userLang.code }?.subscribed?.value ?: false
}

val subscriptionsList: ImmutableList<Subscription> =
    if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
        subList
    } else {
        subList.mutate { list ->
            list.filter { it.lang == BuildConfig.FLAVOR_LANG_CODE }
        }
    }