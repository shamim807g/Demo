package com.lengo.uni

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.lengo.uni.FlavorDimension
import com.lengo.uni.libs

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class LengoFlavor(
    val dimension: FlavorDimension = FlavorDimension.contentType,
    val versionNameSuffix: String? = null,
    val flavorType: String = "lng",
    val flavorLangCode: String,
    val applicationIdSuffix: String? = ".${flavorLangCode}",
    val appIcon: String = "@mipmap/${flavorLangCode}_ic_launcher",
    val appIconRound: String = "@mipmap/${flavorLangCode}_ic_launcher_round"

) {
    allLang(
        flavorType = "all",
        flavorLangCode = "es",
        applicationIdSuffix = null,
        appIcon = "@mipmap/ic_launcher", appIconRound = "@mipmap/ic_launcher_round"
    ),
    english(flavorLangCode = "en", versionNameSuffix = "-English"),
    englishUS(flavorLangCode = "us", versionNameSuffix = "-EnglishUS"),
    german(
        flavorLangCode = "de",
        versionNameSuffix = "-German",
    ),
    Chinese(
        flavorLangCode = "cn",
        versionNameSuffix = "-Chinese",
    ),
    Italian(
        flavorLangCode = "it",
        versionNameSuffix = "-Italian",
    ),
    Portuguese(
        flavorLangCode = "pt",
        versionNameSuffix = "-Portuguese",
    ),
    Swedish(
        flavorLangCode = "se",
        versionNameSuffix = "-Swedish",
    ),
    Polish(
        flavorLangCode = "pl",
        versionNameSuffix = "-Polish",
    ),
    Thai(
        flavorLangCode = "th",
        versionNameSuffix = "-Thai",
    ),
    Arabic(
        flavorLangCode = "ar",
        versionNameSuffix = "-Arabic",
    ),
    Danish(
        flavorLangCode = "da",
        versionNameSuffix = "-Danish",
    ),
    Greek(
        flavorLangCode = "el",
        versionNameSuffix = "-Greek",
    ),
    Finnish(
        flavorLangCode = "fi",
        versionNameSuffix = "-Finnish",
    ),
    French(
        flavorLangCode = "fr",
        versionNameSuffix = "-French",
    ),
    Japanese(
        flavorLangCode = "ja",
        versionNameSuffix = "-Japanese",
    ),
    Dutch(
        flavorLangCode = "nl",
        versionNameSuffix = "-Dutch",
    ),
    Russian(
        flavorLangCode = "ru",
        versionNameSuffix = "-Russian",
    ),
    Ukrainian(
        flavorLangCode = "ua",
        versionNameSuffix = "-Ukrainian",
    ),
    Turkish(
        flavorLangCode = "tr",
        versionNameSuffix = "-Turkish",
    ),
    Spanish(
        flavorLangCode = "es",
        versionNameSuffix = "-Spanish",
    ),
    Norwegian(
        flavorLangCode = "no",
        versionNameSuffix = "-Norwegian-Bokmål",
    ),
    Korean(
        flavorLangCode = "ko",
        versionNameSuffix = "-Korean",
    ),
    Czech(
        flavorLangCode = "cz",
        versionNameSuffix = "-Czech",
    ),
    Slovak(
        flavorLangCode = "sk",
        versionNameSuffix = "-Slovak",
    ),
    Romanian(
        flavorLangCode = "ro",
        versionNameSuffix = "-Romanian",
    ),
    Bulgarian(
        flavorLangCode = "bg",
        versionNameSuffix = "-Bulgarian",
    ),
    Serbian(
        flavorLangCode = "sr",
        versionNameSuffix = "-Serbian",
    ),
    Vietnamese(
        flavorLangCode = "vi",
        versionNameSuffix = "-Vietnamese",
    ),
    Hungarian(
        flavorLangCode = "hu",
        versionNameSuffix = "-Hungarian",
    ),
}


fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: LengoFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.contentType.name
        productFlavors {
            LengoFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    resValue("string", "app_name", "LENGO")
                    buildConfigField("String", "FLAVOR_TYPE", "\"${it.flavorType}\"")
                    buildConfigField("String", "FLAVOR_LANG_CODE", "\"${it.flavorLangCode}\"")
                    manifestPlaceholders["appIcon"] = it.appIcon
                    manifestPlaceholders["appIconRound"] = it.appIconRound
                    if(it.name != "allLang") {
                        if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                            if (it.applicationIdSuffix != null) {
                                applicationIdSuffix = it.applicationIdSuffix
                                versionNameSuffix = it.versionNameSuffix
                            }
                        }
                    }
                    flavorConfigurationBlock(this, it)
                }
            }
        }
    }
}
