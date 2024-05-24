package com.lengo.common

import com.lengo.common.R
import kotlinx.collections.immutable.persistentListOf

fun getDrawables(devLng: String,selectLng: String): List<Int> {
    return listOf(getDrawableForLangCode(selectLng),getDrawableForLangCode(devLng))
}

fun mapToSetupStructureLangCode(own: String): String {
    return when(own) {
        "zh" -> "cn"
        "sv" -> "se"
        "uk" -> "ua"
        else -> own
    }
}

fun mapToLocalLangCode(userSelLang: String): String {
    return when(userSelLang) {
        "cn" -> "zh"
        "se" -> "sv"
        "ua" -> "uk"
        else -> userSelLang
    }
}

fun getWelcomeString(userSelLang: String): String {
    return when(userSelLang) {
        "en" -> "Welcome"
        "us" -> "Welcome"
        "es" -> "Bienvenido"
        "ar" -> "أهلاً و سهلاً"
        "zh","cn" -> "欢迎光临"
        "da" -> "Velkommen"
        "de" -> "Willkommen"
        "el" -> "Καλώς Ήρθες"
        "fi" -> "Tervetuloa"
        "fr" -> "Bienvenue"
        "it" -> "Benvenuto"
        "th" -> "ยินดีต้อนรับ"
        "ja" -> "ようこそ "
        "nl" -> "Welkom"
        "pt" -> "Bem-vindo"
        "no" -> "Velkommen"
        "pl" -> "Witaj"
        "ru" -> "Добро пожаловать"
        "sv","se" -> "Välkommen"
        "tr" -> "Hoş geldin"
        "uk","ua" -> "Ласкаво просимо"
        "ko" -> "환영합니다 "
        "cz" -> "Vítej"
        "sk" -> "vitajte"
        "ro" -> "bine ai venit"
        "bg" -> "добре дошли"
        "sr" -> "добро дошли"
        "vi" -> "chào mừng"
        "hu" -> "üdvözlünk"
        else -> "Welcome"
    }
}




fun getDrawableForLangCode(lng: String): Int {
    return when (lng) {
        "en" -> R.drawable.united_kingdom
        "us" -> R.drawable.america
        "es" -> R.drawable.spain
        "ar" -> R.drawable.f_saudi_arabia
        "zh","cn" -> R.drawable.china
        "da" -> R.drawable.denmark
        "de" -> R.drawable.germany
        "el" -> R.drawable.greece
        "fi" -> R.drawable.finland
        "fr" -> R.drawable.france
        "it" -> R.drawable.italy
        "th" -> R.drawable.thailand
        "ja" -> R.drawable.japan
        "nl" -> R.drawable.netherlands
        "pt" -> R.drawable.portugal
        "no" -> R.drawable.norway
        "pl" -> R.drawable.poland
        "ru" -> R.drawable.russia
        "sv","se" -> R.drawable.sweden
        "tr" -> R.drawable.turkey
        "uk","ua" -> R.drawable.ukraine
        "ko" -> R.drawable.ko
        "cz" -> R.drawable.czech
        "sk" -> R.drawable.slovak
        "ro" -> R.drawable.romanian
        "bg" -> R.drawable.bulgaria
        "sr" -> R.drawable.serbian
        "vi" -> R.drawable.vietnamese
        "hu" -> R.drawable.hungarian
        else -> R.drawable.america
    }
}

val maleNames = persistentListOf(
    "Brayden",
    "Ethen",
    "Drew",
    "Isaiah",
    "Antony",
    "Bronson",
    "Jessie",
    "Cordell",
    "Giancarlo",
    "Antoine",
    "Marvin",
    "Braxton",
    "Troy",
    "Jamison",
    "Anderson",
    "Brandon",
    "Talon",
    "Darren",
    "Graham",
    "Francisco"
)

val femaleNames = persistentListOf(
    "Brenna",
    "Janiah",
    "Isabela",
    "Charlotte",
    "Shayla",
    "Elena",
    "Caroline",
    "Rebekah",
    "Lauryn",
    "Amari",
    "Tessa",
    "Nyla",
    "Alice",
    "Xiomara",
    "Kennedi",
    "Lyric",
    "Zoey",
    "Jacqueline",
    "Yuliana",
    "Emmy"
)

val neutralNames = persistentListOf(
    "Arden",
    "Bellamy",
    "Briar",
    "Brighton",
    "Callaway",
    "Cove",
    "Cypress",
    "Ever",
    "Halston",
    "Hollis",
    "Honor",
    "Jupiter",
    "Kingsley",
    "Kit",
    "Landry",
    "Lexington",
    "Lux",
    "Merritt",
    "Ocean",
    "Revel"
)