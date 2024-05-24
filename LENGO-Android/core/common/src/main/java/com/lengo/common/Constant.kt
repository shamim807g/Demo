package com.lengo.common


import com.lengo.model.data.DeviceLang
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


val BASE_URL = "https://api.lengo.io/"
val PLACEHOLDER = "placeholder"
val IMAGE_LOADING_ERROR = "image_error"

val SYS_GRAMMER = "SysGram"
val SYS_VOCAB = "SysVok"
val USER_VOCAB = "UsrVok"
val USER_VOCAB_SUGGESTED = "UsrVok-SUGGESTED"
val DEFAULT_SEL_LANG = BuildConfig.FLAVOR_LANG_CODE
var DEFAULT_OWN_LANG = ""

val FLAVOUR_TYPE_ALL = "all"
val FLAVOUR_TYPE_LANG = "lng"

val RED_WORDS = 22
val GREEN_WORDS = 33
val YELLOW_WORDS = 44
val ALL_WORDS_WITHOUT_GREY = 55

val images = listOf(
    R.drawable.typewriter,
    R.drawable.woman,
    R.drawable.journal,
    R.drawable.library,
    R.drawable.school
)


@Suppress("BanInlineOptIn")
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    contract { callsInPlace(action) }
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}


val deviceLang: ImmutableList<DeviceLang> = persistentListOf(
    DeviceLang(locale = Locale("en"), code = "en", drawable = getDrawableForLangCode("en")),
    DeviceLang(locale = Locale("ar"), code = "ar", drawable = getDrawableForLangCode("ar")),
    DeviceLang(locale = Locale("da"), code = "da", drawable = getDrawableForLangCode("da")),
    DeviceLang(locale = Locale("de"), code = "de", drawable = getDrawableForLangCode("de")),
    DeviceLang(locale = Locale("el"), code = "el", drawable = getDrawableForLangCode("el")),
    DeviceLang(locale = Locale("es"), code = "es", drawable = getDrawableForLangCode("es")),
    DeviceLang(locale = Locale("fi"), code = "fi", drawable = getDrawableForLangCode("fi")),
    DeviceLang(locale = Locale("fr"), code = "fr", drawable = getDrawableForLangCode("fr")),
    DeviceLang(locale = Locale("it"), code = "it", drawable = getDrawableForLangCode("it")),
    DeviceLang(locale = Locale("ja"), code = "ja", drawable = getDrawableForLangCode("ja")),
    DeviceLang(locale = Locale("nl"), code = "nl", drawable = getDrawableForLangCode("nl")),
    DeviceLang(locale = Locale("no"), code = "no", drawable = getDrawableForLangCode("no")),
    DeviceLang(locale = Locale("pl"), code = "pl", drawable = getDrawableForLangCode("pl")),
    DeviceLang(locale = Locale("pt"), code = "pt", drawable = getDrawableForLangCode("pt")),
    DeviceLang(locale = Locale("ru"), code = "ru", drawable = getDrawableForLangCode("ru")),
    DeviceLang(locale = Locale("sv"), code = "sv", drawable = getDrawableForLangCode("sv")),
    DeviceLang(locale = Locale("th"), code = "th", drawable = getDrawableForLangCode("th")),
    DeviceLang(locale = Locale("tr"), code = "tr", drawable = getDrawableForLangCode("tr")),
    DeviceLang(locale = Locale("uk"), code = "uk", drawable = getDrawableForLangCode("uk")),
    DeviceLang(locale = Locale("zh"), code = "zh", drawable = getDrawableForLangCode("zh"))
)



val uni_images = persistentListOf(
    R.drawable.uni_meta_image_0,
    R.drawable.uni_meta_image_1,
    R.drawable.uni_meta_image_2,
    R.drawable.uni_meta_image_3,
    R.drawable.uni_meta_image_4,
    R.drawable.uni_meta_image_5,
    R.drawable.uni_meta_image_6,
    R.drawable.uni_meta_image_7,
)


fun get12MonthsString(userSelLang: String): String {
    return when(userSelLang) {
        "en" ->         "12 Months"
        "us" ->         "12 Months"
        "es" ->         "12 meses"
        "ar" ->         "12 شهر"
        "zh","cn" ->    "12个月"
        "da" ->         "12 måneder"
        "de" ->         "12 Monate"
        "el" ->         "12 μήνες"
        "fi" ->         "12 kuukautta"
        "fr" ->         "12 mois"
        "it" ->         "12 mesi"
        "th" ->         "12 เดือน"
        "ja" ->         "12か月"
        "nl" ->         "12 maanden"
        "pt" ->         "12 meses"
        "no" ->         "12 måneder"
        "pl" ->         "12 miesięcy"
        "ru" ->         "12 месяцев"
        "sv","se" ->    "12 månader"
        "tr" ->         "12 ay"
        "uk","ua" ->    "12 місяців"
        "ko" ->         "12 개월"
        "cz" ->         "12 měsíců"
        "sk" ->         "12 mesiacov"
        "ro" ->         "12 luni"
        "bg" ->         "12 месеца"
        "sr" ->         "12 месеци"
        "vi" ->         "12 tháng"
        "hu" ->         "12 hónap"
        else ->         "12 Months"
    }
}

fun get1MonthString(userSelLang: String): String {
    return when(userSelLang) {
        "en" ->       "1 Month"
        "us" ->       "1 Month"
        "es" ->       "1 mes"
        "ar" ->       "1 شهر"
        "zh","cn" ->  "1个月"
        "da" ->       "1 måned"
        "de" ->       "1 Monat"
        "el" ->       "1 μήνα"
        "fi" ->       "1 kuukausi"
        "fr" ->       "1 mois"
        "it" ->       "1 mese"
        "th" ->       "1 เดือน"
        "ja" ->       "1ヶ月"
        "nl" ->       "1 maand"
        "pt" ->       "1 mês"
        "no" ->       "1 måned"
        "pl" ->       "1 miesiąc"
        "ru" ->       "1 месяц"
        "sv","se" ->  "1 månad"
        "tr" ->       "1 ay"
        "uk","ua" ->  "1 місяць"
        "ko" ->       "1 개월"
        "cz" ->       "1 měsíc"
        "sk" ->       "1 mesiac"
        "ro" ->       "1 lună"
        "bg" ->       "1 месец"
        "sr" ->       "1 месец"
        "vi" ->       "1 tháng"
        "hu" ->       "1 hónap"
        else ->       "1 Month"
    }
}