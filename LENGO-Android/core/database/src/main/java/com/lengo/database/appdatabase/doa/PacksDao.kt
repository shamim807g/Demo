package com.lengo.database.appdatabase.doa

import androidx.room.*
import com.lengo.common.USER_VOCAB
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.model.data.ObjParam
import kotlinx.coroutines.flow.Flow
import logcat.logcat


@Dao
interface PacksDao {

    @Query("SELECT * FROM packs")
    suspend fun getAllData(): List<PacksEntity>

    @Query("DELETE FROM packs")
    suspend fun removePacks()

    @Query("DELETE FROM lections")
    suspend fun removeAllLections()

    @Query("DELETE FROM object")
    suspend fun removeAllObjects()

    @Query("DELETE FROM datestats")
    suspend fun removeAllDateStats()

    @Query("SELECT * FROM object where iVal > -1")
    suspend fun getAllObjData(): List<ObjectEntity>

    @Query("SELECT * FROM object where ((iVal > -1 AND type != 'UsrVok') OR (type = 'UsrVok')) AND pushed = :pushed")
    suspend fun getAllPushedObjData(pushed: Boolean = false): List<ObjectEntity>?

    @Query("SELECT * FROM object where pushed = :pushed and type = :type")
    suspend fun getAllPushedUserObjData(
        pushed: Boolean = false,
        type: String = USER_VOCAB
    ): List<ObjectEntity>?

    @Query("SELECT * FROM packs")
    suspend fun getAllPacks(): List<PacksEntity>

    @Query("SELECT * FROM packs where pushed = :pushed")
    suspend fun getAllPushedPacks(pushed: Boolean = false): List<PacksEntity>?

    //SELECT packs.coins,packs.emoji,packs.type,packs.pck as packId,packs.title,packs.owner,lections.lec as lecId,lections.title as lecName FROM packs  LEFT JOIN lections on lections.pck = packs.pck and lections.owner = packs.owner and lections.type = packs.type  where packs.pushed = '0' or lections.pushed = '0'
    /*@Query("SELECT packs.coins,packs.emoji,packs.type,packs.pck as packId,packs.title,packs.owner,lections.lec as lecId,lections.title as lecName FROM packs  LEFT JOIN lections on lections.pck = packs.pck and lections.owner = packs.owner and lections.type = packs.type  where packs.pushed = :pushed or lections.pushed = :pushed")
    suspend fun getAllPushedPacksLections(pushed: Boolean = false): List<PackWithLections>?
    data class PackWithLections(
        val coins: Int,
        var emoji: String? = null,
        var type: String,
        var packId: Long,
        var title: Map<String,String>,
        var owner: Long,
        val lecId: Long,
        var lecName: Map<String,String>,
    )*/
    @Query("SELECT * FROM packs where type = :type AND pushed = :pushed")
    suspend fun getAllPushedUserPacks(
        pushed: Boolean = false,
        type: String = USER_VOCAB
    ): List<PacksEntity>?

    @Query("SELECT * FROM packs where type = :type AND pck = :pck AND owner = :owner AND lng = :lang")
    suspend fun packsForLection(
        pck: Long,
        owner: Long,
        lang: String,
        type: String = USER_VOCAB,
    ): PacksEntity?

    @Query("SELECT * FROM lections where type = :type AND pushed = :pushed")
    suspend fun getAllPushedUserLections(
        pushed: Boolean = false,
        type: String = USER_VOCAB
    ): List<LectionsEntity>?

    @Query("SELECT * FROM lections where pck = :pck AND owner = :owner AND lng = :lang AND type = :type AND pushed = :pushed")
    suspend fun getAllPushedUserLectionsForPack(
        pck: Long,
        owner: Long,
        lang: String,
        pushed: Boolean = false,
        type: String = USER_VOCAB
    ): List<LectionsEntity>?


    @Query("SELECT * FROM packs LEFT JOIN lections on lections.pck = packs.pck and lections.owner = packs.owner and lections.type = packs.type where packs.type = :type AND lections.type = :type AND (packs.pushed = :pushed or lections.pushed = :pushed)")
    suspend fun getAllPushedPacksLections(
        pushed: Boolean = false,
        type: String
    ): Map<PacksEntity, List<LectionsEntity>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacks(packs: List<PacksEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecttions(lections: List<LectionsEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObject(objects: List<ObjectEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObject(objects: ObjectEntity)


    @Query("DELETE FROM packs WHERE lng = :lang AND coins > 0 AND subscribed = 1")
    suspend fun removeAllSubPacksForLang(lang: String)


    @Query("DELETE FROM packs WHERE coins > 0 AND subscribed = 1 AND lng IN(:ids)")
    suspend fun removeAllSubPacks(ids: List<String>)


    @Query("SELECT * FROM packs WHERE lng = :lang AND type != :type")
    suspend fun getAllPacksExcludeType(
        lang: String,
        type: String
    ): List<PacksEntity>?


    @Query("SELECT * FROM packs WHERE lng = :lang AND type = :type")
    suspend fun getAllPacksType(
        lang: String,
        type: String
    ): List<PacksEntity>?

    @Query("SELECT * FROM lections WHERE lng = :lang AND type = :type")
    suspend fun getAllLecType(
        lang: String,
        type: String
    ): List<LectionsEntity>?


    @Query("SELECT COUNT(*) FROM lections WHERE pck = :pck AND owner = :owner AND type = :type AND lng = :lang AND (examples is NULL OR explanation is NULL)")
    suspend fun getEmptyExampleOrExplantionLections(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): Int

    @Query("SELECT * FROM object WHERE type = :type AND lng = :lang AND iVal  > -1")
    suspend fun getTotalPackEdited(type: String, lang: String): List<ObjectEntity>?


    @Query("SELECT * FROM lections WHERE pck = :pck AND owner = :owner AND type = :type AND lng = :lang")
    suspend fun getAllLections(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): List<LectionsEntity>


    @Query("SELECT * FROM lections WHERE lec = :lec AND pck = :pck AND owner = :owner AND type = :type AND lng = :lang LIMIT 1")
    fun getLection(
        lec: Long,
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): Flow<LectionsEntity>


    @Query("SELECT * FROM object WHERE lec = :lec AND pck = :pck AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun getObjects(
        pck: Long,
        lec: Long,
        type: String,
        owner: Long,
        lang: String
    ): List<ObjectEntity>?

    @Query("UPDATE object SET own = :own, sel = :sel WHERE lec = :lec AND pck = :pck AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun updateUserObjects(
        pck: Long,
        lec: Long,
        type: String,
        owner: Long,
        lang: String,
        own: Map<String, List<String>>?,
        sel: List<String>?,
    )

    @Query("SELECT * FROM object WHERE lng = :lang")
    fun observeAllObjects(
        lang: String
    ): Flow<List<ObjectEntity>>


    @Query("SELECT * FROM object WHERE lng = :lang")
    fun getAllObjects(
        lang: String
    ): Flow<List<ObjectEntity>?>

    @Query("SELECT * FROM object WHERE lng = :lang")
    suspend fun getAllObjectsSync(lang: String): List<ObjectEntity>?

    @Query("SELECT COUNT(*) from object where owner = :owner AND pck = :pck AND type = :type AND lng = :lang")
    suspend fun packObjectsCount(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): Int

    @Query("SELECT COUNT(*) from packs where packs.owner= :owner AND packs.pck = :pck AND packs.type = :type AND packs.lng = :lang")
    suspend fun packCount(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): Int

    @Query("SELECT version,key_pushed from packs where packs.owner = :owner AND packs.pck = :pck AND packs.type = :type AND packs.lng = :lang")
    suspend fun packVersion(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): PackVersionAndKeyPushed?

    data class PackVersionAndKeyPushed(val version: Int, val key_pushed: Boolean)

    @Query("SELECT pck,type,owner,lng from packs")
    suspend fun getAllAddedPacks(): List<PackId>

    class PackId(
        var pck: Long,
        var type: String,
        var owner: Long,
        val lng: String
    )


    @Query("SELECT iVal FROM object WHERE obj = :objId AND pck = :pck AND lec = :lec AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun getScore(
        objId: Long,
        pck: Long,
        lec: Long,
        type: String,
        owner: Long,
        lang: String
    ): Int?


    @Query("UPDATE object SET iVal = :newScore, last_retrieval = :currentDate,pushed = :pushed WHERE obj = :objId AND pck = :pck AND lec = :lec AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun updateScore(
        objId: Long,
        pck: Long,
        lec: Long,
        type: String,
        owner: Long,
        newScore: Int,
        lang: String,
        currentDate: Long,
        pushed: Boolean = false
    )


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(packs: PacksEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLection(lectionsEntity: LectionsEntity)


    @Query("UPDATE packs SET last_retrieval = :time,pushed = :pushed WHERE pck = :pck AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun updatePackTime(
        time: Long,
        pck: Long,
        owner: Long,
        type: String,
        lang: String,
        pushed: Boolean = false
    )


    @Query("UPDATE LECTIONS SET image = :image,pushed = :pushed WHERE lec = :lec and pck = :pck and type = :type and owner = :owner")
    suspend fun updateLectionImage(
        image: String,
        lec: Long,
        pck: Long,
        type: String,
        owner: Long,
        pushed: Boolean = false
    )


    @Query("SELECT image FROM lections WHERE lec = :lec and pck = :pck and type = :type and owner = :owner")
    suspend fun getLectionImage(
        lec: Long,
        pck: Long,
        type: String,
        owner: Long
    ): LecImage?

    data class LecImage(
        val image: Map<String, String>? = null,
    )

    @Query("UPDATE packs SET pushed = :pushed")
    suspend fun updatePushedPack(
        pushed: Boolean = true
    )

    @Query("UPDATE lections SET pushed = :pushed")
    suspend fun updatePushedLections(
        pushed: Boolean = true
    )

    @Query("UPDATE object SET pushed = :pushed")
    suspend fun updatePushedObj(
        pushed: Boolean = true
    )

    @Query("UPDATE user SET pushed = :pushed")
    suspend fun updatePushedUser(
        pushed: Boolean = true
    )

    @Query("UPDATE datestats SET pushed = :pushed")
    suspend fun updatePushedDateStats(
        pushed: Boolean = true
    )

    @Transaction
    suspend fun pushedAllData() {
        updatePushedPack()
        updatePushedLections()
        updatePushedObj()
        updatePushedUser()
        updatePushedDateStats()
    }

    @Transaction
    suspend fun removeAllData() {
        removePacks()
        removeAllLections()
        removeAllObjects()
    }


//    @Transaction
//    suspend fun updateLectionImage(
//        lec: Long,
//        pck: Long,
//        type: String,
//        owner: Long,
//        selLangCode: String,
//        newImage: String
//    ) {
//        var imageMap = getLectionImage(lec, pck, type, owner)?.image
//
//        if (imageMap == null) {
//            imageMap = mapOf(Pair(selLangCode, newImage))
//        } else {
//            imageMap = imageMap + Pair(selLangCode, newImage)
//        }
//
//        _updateLectionImage(imageMap, lec, pck, type, owner)
//    }


    @Query("SELECT image FROM LECTIONS")
    suspend fun allLecImages(): List<LecImage>


    @Query("SELECT COUNT(*) FROM packs")
    suspend fun totalPacks(): Int

    @Query("SELECT COUNT(*) FROM packs where type = :type")
    suspend fun userPack(type: String): Long?

    @Query("SELECT COUNT(*) FROM lections where type = :type AND pck = :pack")
    suspend fun userPackLections(type: String, pack: Long): Long?

    @Query("SELECT * FROM packs where type = :type AND lng = :lang")
    suspend fun getUserPacks(type: String, lang: String): List<PacksEntity>?

    @Query("SELECT submitted FROM packs where type = :type AND pck = :pack AND lng = :lang AND owner = :owner")
    suspend fun userPackPublicStatus(type: String, pack: Long, lang: String, owner: Long): Boolean?

    @Query("SELECT * FROM lections where pck = :pck AND owner = :owner AND type = :type AND lng = :lang")
    suspend fun userPackLection(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): List<LectionsEntity>?


    @Query("UPDATE packs SET title = :title,pushed = :pushed WHERE pck = :pck and type = :type and owner = :owner")
    suspend fun updateUserPackTitle(
        title: Map<String, String>,
        pck: Long,
        type: String,
        owner: Long,
        pushed: Boolean = false
    )

    @Query("UPDATE packs SET submitted = :submitted, pushed = :pushed WHERE pck = :pck and type = :type and owner = :owner and lng = :lang")
    suspend fun updateUserPackPublish(
        submitted: Boolean,
        pck: Long,
        lang: String,
        type: String,
        owner: Long,
        pushed: Boolean = false
    )

    @Query("UPDATE packs SET emoji = :emoji,pushed = :pushed WHERE pck = :pck AND type = :type AND owner = :owner AND lng = :lang")
    suspend fun updatePackEmoji(
        emoji: String,
        pck: Long,
        type: String,
        owner: Long,
        lang: String,
        pushed: Boolean = false
    )

    @Query("UPDATE lections SET title = :title,pushed = :pushed WHERE pck = :pck and lec = :lec and type = :type and owner = :owner")
    suspend fun updateUserPackLection(
        title: Map<String, String>,
        pck: Long,
        lec: Long,
        type: String,
        owner: Long,
        pushed: Boolean = false
    )

    @Transaction
    suspend fun insertUserPack(packName: String = "",selLangCode: String, own: String, ownerId: Long): Long {
        val totalPacks = userPack(USER_VOCAB) ?: 0L
        val userPackName = "$packName ${if(totalPacks > 0) totalPacks else ""}"
        val currentDate = System.currentTimeMillis()
        val pack = PacksEntity(
            emoji = "✏️",
            coins = 0,
            key_pushed = false,
            last_retrieval = currentDate,
            lng = selLangCode,
            owner = ownerId,
            pck = totalPacks,
            pushed = false,
            type = USER_VOCAB,
            title = mapOf(Pair(own, "$userPackName"))
        )
        insertPack(pack)
        return totalPacks
    }

    @Transaction
    suspend fun insertUserPackLection(
        lectionName: String,
        selLangCode: String,
        own: String,
        pack: Long,
        packOwner: Long
    ): Long {
        val totalLections = userPackLections(USER_VOCAB, pack) ?: 0L
        val userLectionName = "$lectionName ${if(totalLections > 0) totalLections else ""}"
        insertLection(
            LectionsEntity(
                lec = totalLections,
                lng = selLangCode,
                pck = pack,
                owner = packOwner,
                type = USER_VOCAB,
                title = mapOf(Pair(own, "$userLectionName")),
                pushed = false
            )
        )
        return totalLections
    }

    @Transaction
    suspend fun increaseOrDecreaseScroe(
        obj: ObjParam, point: Int, selLangCode: String
    ) {

        logcat("increaseOrDecreaseScroe") { "obj ID ${obj.objId} pck ${obj.pck} lec ${obj.lec} type ${obj.type} owner ${obj.owner} ${selLangCode}" }
        val currentDate = System.currentTimeMillis()
        logcat("increaseOrDecreaseScroe") { "currentDate $currentDate" }
        val currentLngScore =
            getScore(obj.objId, obj.pck, obj.lec, obj.type.replace("-SUGGESTED",""), obj.owner, selLangCode) ?: -1
        logcat("increaseOrDecreaseScroe") { "currentLngScore $currentLngScore" }
        val newScore = if ((point == -1 || point == 0) && currentLngScore == -1) {
            0
        } else if(point == 1 && currentLngScore == -1) {
            2
        } else {
            currentLngScore + point
        }
        logcat("increaseOrDecreaseScroe") { "newScore $newScore" }
        updateScore(
            obj.objId,
            obj.pck,
            obj.lec,
            obj.type.replace("-SUGGESTED",""),
            obj.owner,
            if (newScore < 0) 0 else newScore,
            selLangCode,
            currentDate
        )
    }


}


