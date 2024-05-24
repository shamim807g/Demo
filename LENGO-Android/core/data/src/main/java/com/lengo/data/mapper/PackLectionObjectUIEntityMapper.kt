package com.lengo.data.mapper

import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack


fun List<LectionUIEntity>.toLectionList(): List<Lection> {
    return this.map { it.toLection() }
}

fun LectionUIEntity.toLection(): Lection {
    return Lection(
        type,
        pck,
        owner,
        lec,
        lec_title,
        lec_nameMap,
        lang,
        lec_image
    )
}

fun PacksUIEntity.toPack(lections: List<Lection>): Pack {
    return Pack(
        pck,
        owner,
        pack_title,
        packNameMap,
        type,
        coins.toInt(),
        emoji,
        lang,
        version.toInt(),
        subscribed == 1L,
        false,
        lections,
        badge)
}