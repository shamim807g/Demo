package com.lengo.database.newuidatabase.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity
import com.lengo.model.data.BADGE
import kotlinx.coroutines.flow.Flow

@Dao
interface UIPackLecDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackUI(packs: PacksUIEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackUIList(dates: List<PacksUIEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectionUI(packs: LectionUIEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectionUIList(dates: List<LectionUIEntity>): LongArray

    //updatePackEmoji:
    //UPDATE packs_ui SET emoji = ? WHERE pck = ? AND type = ? AND owner = ? AND lang = ?;
    @Query("UPDATE packs_ui SET emoji = :emoji WHERE pck = :pck AND type = :type AND owner = :owner AND lang = :lang")
    suspend fun updateUIPackEmoji(
        emoji: String,
        pck: Long,
        type: String,
        owner: Long,
        lang: String
    )

    @Query("UPDATE packs_ui SET badge = :badge WHERE pck = :pck AND type = :type AND owner = :owner AND lang = :lang")
    suspend fun updateUIPackBadge(
        badge: BADGE,
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    )


    //updatePackTitle:
    //UPDATE packs_ui SET packNameMap = ?,pack_title = ? WHERE pck = ? AND type = ? AND owner = ?;

    @Query("UPDATE packs_ui SET packNameMap = :packNameMap, pack_title = :pack_title  WHERE pck = :pck AND type = :type AND owner = :owner")
    suspend fun updateUIPackTitle(
        packNameMap: Map<String,String>,
        pack_title: String,
        pck: Long,
        type: String,
        owner: Long,
    )

    @Query("UPDATE lections_ui SET lec_nameMap = :lec_nameMap, lec_title = :lec_title  WHERE lec = :lec AND pck = :pck AND type = :type AND owner = :owner")
    suspend fun updateUILectionTitle(
        lec_nameMap: Map<String,String>,
        lec_title: String,
        pck: Long,
        lec: Long,
        owner: Long,
        type: String
    )

    @Query("UPDATE lections_ui SET lec_image = :lec_image WHERE lec = :lec AND pck = :pck AND type = :type AND owner = :owner")
    suspend fun updateLectionImage2(
        lec_image: String,
        pck: Long,
        lec: Long,
        owner: Long,
        type: String
    )

    //getPacksAndLectionsType:
    //SELECT * FROM packs_ui INNER JOIN lections_ui ON (packs_ui.pck = lections_ui.pck
    // AND packs_ui.owner = lections_ui.owner
    // AND packs_ui.type = lections_ui.type AND packs_ui.lang = lections_ui.lang)
    // WHERE packs_ui.lang = ? AND packs_ui.type != ?;
    //
    @Query("SELECT * FROM packs_ui INNER JOIN lections_ui ON packs_ui.pck = lections_ui.pck " +
            "AND packs_ui.owner = lections_ui.owner AND packs_ui.type = lections_ui.type " +
            "AND packs_ui.lang = lections_ui.lang " +
            "WHERE packs_ui.lang = :lang AND packs_ui.type != :type")
    fun getPacksAndLectionsType(
        type: String,
        lang: String
    ): Flow<Map<PacksUIEntity,List<LectionUIEntity>>?>


    //updateAllPacksWithCoinsForSelLang:
    //UPDATE packs_ui SET badge = ?,subscribed = ? WHERE coins > 0 AND lang = ?;
    //
    @Query("UPDATE packs_ui SET badge = :badge, subscribed = :subscribed WHERE coins > 0 AND lang = :lang")
    suspend fun updateAllPacksWithCoinsForSelLang(
        badge: BADGE,
        subscribed: Int,
        lang: String
    )

    //updateAllPacksWithCoinsForAll:
//UPDATE packs_ui SET badge = ?,subscribed = ? WHERE coins > 0;
//
    @Query("UPDATE packs_ui SET badge = :badge, subscribed = :subscribed WHERE coins > 0")
    suspend fun updateAllPacksWithCoinsForAll(
        badge: BADGE,
        subscribed: Int
    )

    //getPacksAndLections:
//SELECT * FROM packs_ui INNER JOIN lections_ui ON (packs_ui.pck = lections_ui.pck AND packs_ui.owner = lections_ui.owner AND packs_ui.type = lections_ui.type
// AND packs_ui.lang = lections_ui.lang) WHERE packs_ui.lang = ? AND packs_ui.type = ?;
//

    @Query("SELECT * FROM packs_ui INNER JOIN lections_ui ON packs_ui.pck = lections_ui.pck " +
            "AND packs_ui.owner = lections_ui.owner AND packs_ui.type = lections_ui.type " +
            "AND packs_ui.lang = lections_ui.lang " +
            "WHERE packs_ui.lang = :lang AND packs_ui.type = :type")
    suspend fun getPacksAndLections(
        type: String,
        lang: String
    ): Map<PacksUIEntity,List<LectionUIEntity>>?


    //getLections:
//SELECT * FROM lections_ui WHERE pck = ? AND type = ? AND owner = ? AND lang = ?;
//
    @Query("SELECT * FROM lections_ui WHERE pck = :pck AND type = :type AND owner = :owner AND lang = :lang")
    fun getLections(
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): Flow<List<LectionUIEntity>>

    //getLection:
    //SELECT * FROM lections_ui WHERE lec = ? AND pck = ? AND owner = ? AND type = ? AND lang = ?;
    @Query("SELECT * FROM lections_ui WHERE lec = :lec AND pck = :pck AND type = :type AND owner = :owner AND lang = :lang")
    suspend fun getLection(
        lec: Long,
        pck: Long,
        owner: Long,
        type: String,
        lang: String
    ): LectionUIEntity?

    @Query("SELECT * FROM packs_ui")
    suspend fun allPacks(): List<PacksUIEntity>?

    @Query("SELECT * FROM lections_ui")
    suspend fun allLections(): List<LectionUIEntity>?

}