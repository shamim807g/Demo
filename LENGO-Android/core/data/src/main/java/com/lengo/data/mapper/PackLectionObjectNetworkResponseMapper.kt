package com.lengo.data.mapper

import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.USER_VOCAB
import com.lengo.common.USER_VOCAB_SUGGESTED
import com.lengo.common.fastForEach
import com.lengo.common.uni_images
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity
import com.lengo.network.model.PackSuggestionResponse
import com.lengo.network.model.UserPackResponse
import com.lengo.data.mapper.getPackName
import com.lengo.data.mapper.getBadge
import com.lengo.data.mapper.getLectionName

fun fromListPackSuggestionResponseToObjectEntity(
    obj: List<PackSuggestionResponse.Prebuild.Object>?,
    existingObj: List<ObjectEntity>,
    sel: String,
    deviceLng: String,
    originalSelLang: String
): List<ObjectEntity>? {
    return obj?.map { it.toObjectEntity(existingObj, sel, deviceLng, originalSelLang) }
}




fun UserPackResponse.PacksJson.Metadata.toPackEntity(selectedLngCode: String): PacksEntity {
    return PacksEntity(
        pck = id,
        owner = owner,
        title = name,
        type = func,
        coins = coins,
        emoji = emoji ?: "",
        key_pushed = false,
        lng = selectedLngCode,
        version = version,
        submitted = submitted,
    )
}

fun List<PackSuggestionResponse.Prebuild.Metadata>.toUIPackWithLectionsEntity3(
    deviceLngCode: String,
    selectedLngCode: String,
    addedPacks: List<PacksDao.PackId>
): Pair<List<PacksUIEntity>, List<LectionUIEntity>> {
    val finalPacks = mutableListOf<PacksUIEntity>()
    val finalLections = mutableListOf<LectionUIEntity>()

    this.fastForEach { pack ->

        val errorImageList = uni_images.shuffled()

        val badge = getBadge(addedPacks, pack, selectedLngCode)

        if (!pack.available_sel_lng.isNullOrEmpty()) {
            if (!pack.available_sel_lng.contains(selectedLngCode)) {
                return@fastForEach
            }
        }

        val packName = getPackName(pack.name, selectedLngCode, deviceLngCode)

        if (packName.isNullOrEmpty()) {
            return@fastForEach
        }


        val lections = pack.lections.mapIndexed { lecIndex, lec ->

            val lecName = getLectionName(lec.name, selectedLngCode, deviceLngCode)

            if (lecName.isNullOrEmpty()) {
                return@mapIndexed null
            }

            val examples =
                lec.examples?.getOrDefault(selectedLngCode, null)?.mapNotNull { it.example }
            val explanation = lec.explanation?.getOrDefault(selectedLngCode, null)
            LectionUIEntity(
                type = USER_VOCAB_SUGGESTED,
                pck = pack.id,
                owner = pack.owner,
                lec = lec.id,
                lec_title = lecName,
                lec_nameMap = lec.name,
                lang = selectedLngCode,
                example = examples,
                explanation = explanation,
                errorDrawable = errorImageList[lecIndex % 8],
                lec_image = "placeholder"
            )
        }.filterNotNull()


        if (lections.isEmpty()) {
            return@fastForEach
        }

        finalLections.addAll(lections)

        finalPacks.add(
            PacksUIEntity(
                pck = pack.id,
                owner = pack.owner,
                pack_title = packName,
                packNameMap = pack.name,
                type = USER_VOCAB_SUGGESTED,
                coins = pack.coins.toLong(),
                emoji = pack.emoji ?: "",
                lang = selectedLngCode,
                badge = badge,
                version = pack.version.toLong(),
                subscribed = 0L
            )
        )
    }

    return Pair(finalPacks, finalLections)
}


fun PackSuggestionResponse.Prebuild.Object.toObjectEntity(
    existingObj: List<ObjectEntity>,
    sel: String,
    deviceLng: String,
    originalSelLang: String
): ObjectEntity {
    val currentDate = System.currentTimeMillis()
    var ownMap: Map<String, List<String>>? = null
    var selList: List<String>? = null
    if (func.contains(SYS_GRAMMER)) {
        val userSelectedMap = value?.get(sel) as? Map<String, Any>
        val question = userSelectedMap?.getOrDefault("question", null) as? String
        val answers = userSelectedMap?.getOrDefault("answers", null) as? List<String>
        if (question != null && !answers.isNullOrEmpty()) {
            ownMap = mapOf(Pair(deviceLng, listOf(question)))
            selList = answers
        }

    } else if (func.contains(SYS_VOCAB) || func.contains(USER_VOCAB)) {
        val ownLangWord = value?.mapValues {
            (it.value as Map<String, List<String>>)["as"] ?: emptyList()
        }
        ownMap = ownLangWord

        val selectedLngmap = value?.getOrDefault(sel, null) as? Map<String, Any>
        val lsOrasMap = (selectedLngmap?.get("ls") ?: selectedLngmap?.get("as")) as? List<String>

        if (lsOrasMap != null) {
            selList = lsOrasMap
        }
    }
    //"lec", "obj", "pck", "owner", "type","lng"
    val existObj = existingObj.firstOrNull {
        lec == it.lec && obj == it.obj && pck == it.pck
                && owner == it.owner
                && func == it.type && originalSelLang == it.lng
    }
    if (existObj != null) {
        return existObj.copy(own = ownMap, sel = selList)
    } else {
        return ObjectEntity(
            obj = obj,
            pck = pck,
            pushed = false,
            iVal_pushed = false,
            owner = owner,
            lec = lec,
            type = func,
            iVal = -1,
            last_retrieval = currentDate,
            own = ownMap,
            sel = selList,
            lng = originalSelLang
        )
    }

}


fun UserPackResponse.PacksJson.Metadata.toLectionEntity(selectedLngCode: String): List<LectionsEntity>? {
    return lections?.map { lec ->
        val examples = lec.examples?.getOrDefault(selectedLngCode, null)?.mapNotNull { it.example }
        val explanation = lec.explanation?.getOrDefault(selectedLngCode, null)
        LectionsEntity(
            pck = id,
            lec = lec.id,
            owner = owner,
            title = lec.name,
            type = func,
            lng = selectedLngCode,
            examples = examples,
            explanation = explanation
        )
    }
}


fun UserPackResponse.PacksJson.Object.toObjectEntity(sel: String, deviceLng: String, iVal: Int): ObjectEntity {
    val currentDate = System.currentTimeMillis()
    var ownMap: Map<String, List<String>>? = null
    var selList: List<String>? = null
    if (func == SYS_GRAMMER) {
        val userSelectedMap = value?.get(sel) as? Map<String, Any>
        val question = userSelectedMap?.getOrDefault("question", null) as? String
        val answers = userSelectedMap?.getOrDefault("answers", null) as? List<String>
        if (question != null && !answers.isNullOrEmpty()) {
            ownMap = mapOf(Pair(deviceLng, listOf(question)))
            selList = answers
        }
    } else if (func == SYS_VOCAB || func == USER_VOCAB) {
        val ownLangWord = value?.mapValues {
            (it.value as Map<String, List<String>>)["as"] ?: emptyList()
        }
        val selectedLngmap = value?.get(sel) as Map<String, Any>
        val lsOrasMap = (selectedLngmap?.get("ls") ?: selectedLngmap?.get("as")) as? List<String>
        ownMap = ownLangWord
        if (lsOrasMap != null) {
            selList = lsOrasMap
        }
    }
    return ObjectEntity(
        obj = obj,
        pck = pck,
        owner = owner,
        lec = lec,
        type = func,
        iVal = iVal,
        last_retrieval = currentDate,
        own = ownMap,
        sel = selList,
        lng = sel
    )
}