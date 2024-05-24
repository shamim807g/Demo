package com.lengo.data.mapper

import com.lengo.common.PLACEHOLDER
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.model.data.BADGE
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.data.mapper.getLectionName
import com.lengo.data.mapper.getPackName

fun List<LectionsEntity>.toListOfLection(
    deviceLngCode: String,
    selLang: String
): List<Lection> {
    return this.mapIndexed { index, it ->
        it.toLection(
            deviceLngCode,
            selLang
        )
    }
}

fun LectionsEntity.toLection(deviceLngCode: String, selLang: String): Lection {
    val lectionName = getLectionName(this.title,selLang,deviceLngCode)
    return Lection(
        type = this.type,
        pck = this.pck,
        owner = this.owner,
        lec = this.lec,
        image = this.image ?: PLACEHOLDER,
        title = lectionName ?: "",
        nameMap = this.title,
        lang = this.lng,
        explanation = this.explanation,
        example = this.examples
    )
}

fun PacksEntity.toPack(deviceLngCode: String, selLang: String, lections: List<Lection>): Pack {
    val packNameName = getPackName(this.title, selLang, deviceLngCode)
    return Pack(
        pck = this.pck,
        owner = this.owner,
        title = packNameName ?: "",
        packNameMap = this.title,
        type = this.type,
        coins = this.coins,
        emoji = this.emoji ?: "",
        lang = this.lng,
        lections = lections,
        badge = BADGE.OPEN,
        version = this.version,
        subscribed = this.subscribed
    )
}