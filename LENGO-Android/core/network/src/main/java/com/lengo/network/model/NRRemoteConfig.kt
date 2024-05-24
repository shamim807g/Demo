package com.lengo.network.model
import com.lengo.model.data.network.Recommendedresources
import com.squareup.moshi.Json

data class RemoteConfigModel(

    val askForReviewAfterValidCouponPercentage: String,

    @Json(name = "ask_again_for_sub")
    val askAgainForSub: String,

    @Json(name = "ask_for_abo")
    val askForAbo: String,

    @Json(name = "ask_for_abo_first_start")
    val askForAboFirstStart: String,

    @Json(name = "asking_for_review")
    val askingForReview: String,

    @Json(name = "asking_for_review_under_profile")
    val askingForReviewUnderProfile: String,

    @Json(name = "autoset_compliance_in_areas")
    val autosetComplianceInAreas: String,

    @Json(name = "compliance_for_all_apps")
    val complianceForAllApps: String,

    @Json(name = "compliance_for_bundleIds")
    val complianceForBundleIDS: Any? = null,

    @Json(name = "continue_SignInWithApple_With_FakeMail")
    val continueSignInWithAppleWithFakeMail: String,

    val coupons: String,
    val couponsAvailableInLng: String,
    val dailyArrangeDiscover: String,
    val news: String,

    @Json(name = "onboarding_askForPushNot")
    val onboardingAskForPushNot: String,

    @Json(name = "onboarding_login")
    val onboardingLogin: String,

    val profile: String,

    @Json(name = "pull_replication_allowed")
    val pullReplicationAllowed: String,

    @Json(name = "push_replication_allowed")
    val pushReplicationAllowed: String,

    @Json(name = "show_freeTrial_and_directPay_Sub")
    val showFreeTrialAndDirectPaySub: String,

    @Json(name = "show_image_for_Sub")
    val showImageForSub: String,

    @Json(name = "show_img_on_present_sub_alert")
    val showImgOnPresentSubAlert: String,

    @Json(name = "show_link_to_other_apps")
    val showLinkToOtherApps: String,

    @Json(name = "show_sub_legal_components")
    val showSubLegalComponents: String,

    @Json(name = "show_subscription_add_banner")
    val showSubscriptionAddBanner: String,

    @Json(name = "source")
    val source: String?,

    @Json(name = "sub_large_price_for_review_center")
    val subLargePriceForReviewCenter: String,

    var android_use_wavenet_tts: String,

    val usrpcks: String
)


data class NRRemoteConfig(
    @Json(name = "config")
    val config: RemoteConfigModel,
    val error: String,
    val msg: String,
    val session_id: Int,
    val recommended_resources: List<Recommendedresources>?,
)