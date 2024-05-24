package com.lengo.database.jsonDatabase.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lengo.database.jsonDatabase.model.JsonObj
import com.lengo.database.jsonDatabase.model.JsonPack

@Dao
interface JsonPackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packs: List<JsonPack>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllObj(packs: List<JsonObj>): LongArray

    @Query("SELECT * FROM json_pack")
    suspend fun getAllPacks(
    ): List<JsonPack>?

    @Query("SELECT * FROM json_obj WHERE func = :type AND pck = :packId AND owner = :owner")
    suspend fun getObjOfPack(
        packId: Long,
        type: String,
        owner: Long
    ): List<JsonObj>?


    @Query("SELECT * FROM json_pack WHERE id = :packId AND func = :type AND owner = :owner")
    suspend fun getPacks(
        packId: Long,
        type: String,
        owner: Long
    ): JsonPack?

    @Query("SELECT * FROM json_obj WHERE obj = :obj AND func = :type AND pck = :packId AND lec = :lec AND owner = :owner")
    suspend fun getObj(
        obj: Long,
        lec: Long,
        packId: Long,
        type: String,
        owner: Long
    ): JsonObj?



    @Query("SELECT COUNT(*) FROM json_pack")
    suspend fun count(): Int
}