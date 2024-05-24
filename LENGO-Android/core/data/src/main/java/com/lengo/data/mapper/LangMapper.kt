package com.lengo.data.mapper

import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.extension.HexToJetpackColor
import com.lengo.common.getDrawableForLangCode
import com.lengo.database.appdatabase.model.LanguageEntity
import com.lengo.model.data.LNGColor
import com.lengo.model.data.Lang
import java.util.Locale

fun LanguageEntity.toLang(): Lang {
    return Lang(
        locale = Locale(this.iso639_3),
        code = this.tkn,
        iso639_3 = this.iso639_3,
        accent = this.accent,
        drawable = getDrawableForLangCode(this.tkn),
        colors = LNGColor(
            HexToJetpackColor.getColor(this.firstColor),
            HexToJetpackColor.getColor(this.secondColor)
        )
    )
}


fun List<LanguageEntity>.toListOfLang(): List<Lang> {
    return this.map { it.toLang() }.sortedBy {
        it.locale.getDisplayLanguage(Locale(DEFAULT_OWN_LANG)) }
}


