package com.lengo.data.mapper

import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.data.mapper.toLectionUIEntity

fun Lection.toLectionUIEntity(): LectionUIEntity {
    return LectionUIEntity(
        type,
        pck,
        owner,
        lec,
        title,
        nameMap,
        lang,
        example,
        explanation,
        errorDrawable,
        lec_image = "placeholder"
    )
}

fun List<Lection>.toLectionUIList(): List<LectionUIEntity> {
    return this.map { it.toLectionUIEntity() }
}


fun List<Pack>.toUIPackListEntity(): List<PacksUIEntity> {
    return this.map { it.toUIPackEntity() }
}


fun Pack.toUIPackEntity(): PacksUIEntity {
    return PacksUIEntity(
        type = type,
        pck = pck,
        owner = owner,
        pack_title = title,
        packNameMap = packNameMap,
        coins = coins.toLong(),
        emoji = emoji,
        lang = lang,
        badge = badge,
        version = version.toLong(),
        subscribed = if(subscribed) 1L else 0L,
    )
}

fun Lection.toLectionEntity(): LectionsEntity {
    return LectionsEntity(
        type = this.type,
        pck = this.pck,
        owner = this.owner,
        lec = this.lec,
        title = this.nameMap,
        lng = this.lang,
        image = this.image,
        explanation = this.explanation,
        examples = this.example,
        pushed = false
    )
}

fun Pack.toPackEntity(): PacksEntity {
    return PacksEntity(
        pck = this.pck,
        owner = this.owner,
        title = this.packNameMap,
        type = this.type,
        coins = this.coins,
        pushed = false,
        key_pushed = false,
        emoji = this.emoji ?: "",
        lng = this.lang,
        version = this.version,
        subscribed = this.subscribed,
        submitted = this.submitted
    )
}