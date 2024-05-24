package com.lengo.data.mapper

import com.lengo.common.mapToSetupStructureLangCode
import com.lengo.data.preload.PreloadModel
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.jsonDatabase.model.JsonPack
import com.lengo.model.data.BADGE
import com.lengo.network.model.PackSuggestionResponse

fun getPackName(name: Map<String, String>, selLang: String, ownLang: String): String? {
    val finalOwnLang = mapToSetupStructureLangCode(ownLang)
    return if (name[finalOwnLang].isNullOrEmpty()) {
        if (name[selLang].isNullOrEmpty()) {
            if (name["en"].isNullOrEmpty()) {
                return null
            } else {
                name["en"]
            }
        } else {
            name[selLang]
        }
    } else {
        name[finalOwnLang]
    }
}

fun getLectionName(name: Map<String, String>, selLang: String, ownLang: String): String? {
    val finalOwnLang = mapToSetupStructureLangCode(ownLang)
    return if (name[finalOwnLang].isNullOrEmpty()) {
        if (name[selLang].isNullOrEmpty()) {
            if (name["en"].isNullOrEmpty()) {
                return null
            } else {
                name["en"]
            }
        } else {
            name[selLang]
        }
    } else {
        name[finalOwnLang]
    }
}

fun getBadge(
    existingPack: List<PacksDao.PackId>,
    pack: PreloadModel.Prebuild.Metadata,
    selLang: String
): BADGE {
    return if (existingPack.find {
            it.pck == pack.id &&
                    it.owner == pack.owner &&
                    it.type == pack.func &&
                    it.lng == selLang
        } != null) {
        BADGE.OPEN
    } else {
        when {
            pack.coins < 1 -> {
                BADGE.GET
            }
            else -> {
                BADGE.COIN
            }
        }
    }
}

fun getBadge(
    existingPack: List<PacksDao.PackId>,
    pack: PackSuggestionResponse.Prebuild.Metadata,
    selLang: String
): BADGE {
    return if (existingPack.find {
            it.pck == pack.id &&
                    it.owner == pack.owner &&
                    it.type == pack.func &&
                    it.lng == selLang
        } != null) {
        BADGE.OPEN
    } else {
        when {
            pack.coins < 1 -> {
                BADGE.GET
            }
            else -> {
                BADGE.COIN
            }
        }
    }
}

fun getBadge(
    existingPack: List<PacksDao.PackId>,
    pack: JsonPack,
    selLang: String
): BADGE {
    return if (existingPack.find {
            it.pck == pack.id &&
                    it.owner == pack.owner &&
                    it.type == pack.func &&
                    it.lng == selLang
        } != null) {
        BADGE.OPEN
    } else {
        when {
            pack.coins < 1 -> {
                BADGE.GET
            }
            else -> {
                BADGE.COIN
            }
        }
    }
}