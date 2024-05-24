package com.lengo.database.appdatabase.doa

import androidx.room.*
import com.lengo.database.appdatabase.model.LanguageEntity
import com.lengo.database.appdatabase.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDoa {

    @Query("SELECT * FROM user limit 1")
    suspend fun getAllData(): UserEntity

    @Query("SELECT * FROM user where pushed = :pushed limit 1")
    suspend fun getAllPushedData(pushed: Boolean = false): UserEntity?


    @Query("SELECT sel,own FROM user limit 1")
    fun getUserLang(): Flow<UserLang?>
    data class UserLang(val sel: String,val own: String)

    @Query("SELECT * FROM user limit 1")
    fun observeUserData(): Flow<UserEntity?>

    @Query("SELECT sel,own FROM user limit 1")
    suspend fun getSyncUserLang(): UserLang?

    @Query("SELECT * FROM user")
    suspend fun currentUser(): UserEntity?

    @Query("SELECT * FROM language")
    fun getAllLanguages(): Flow<List<LanguageEntity>?>

    @Query("SELECT COUNT(*) FROM language")
    suspend fun getTotalLanguageCount(): Int?

    @Query("SELECT * FROM language where tkn = :sel")
    fun getLanguage(sel: String): Flow<LanguageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(languageEntitys: List<LanguageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity): Long

    @Query("SELECT coins FROM user")
    suspend fun totalCoins(): Int?

    @Query("SELECT coins FROM user")
    fun totalCoinsObserver(): Flow<Int?>

    @Query("UPDATE user SET coins = :newCoins")
    suspend fun updateCoin(newCoins: Int)

    @Query("UPDATE user SET sel = :selLng")
    suspend fun updateSelLng(selLng: String)

    @Query("UPDATE user SET own = :ownLng")
    suspend fun updateOwnLng(ownLng: String)

    @Transaction
    suspend fun addCoins(newCoins: Int
    ) {
        val currentCoins = totalCoins() ?: 0
        val updatedCoins = currentCoins + newCoins
        updateCoin(updatedCoins)
    }

    @Transaction
    suspend fun removeCoins(newCoins: Int) {
        val currentCoins = totalCoins() ?: 0
        if (currentCoins >= newCoins) {
            val updatedCoins = currentCoins - newCoins
            updateCoin(updatedCoins)
        }
    }

    @Query("SELECT points FROM user")
    suspend fun getCurrentPoints(): Long


    @Query("UPDATE user SET points = :newPoints, pushed = :pushed")
    suspend fun updateCurrentPoints(newPoints: Long,pushed: Boolean = false)


    @Query("SELECT highscore FROM user")
    suspend fun getCurrentHighScore(): Long

    @Query("SELECT highscore FROM user")
    fun observeCurrentHighScore(): Flow<Long?>

    @Query("UPDATE user SET highscore = :newHighScore,pushed = :pushed")
    suspend fun updateHighScore(newHighScore: Long,pushed: Boolean = false)

    @Transaction
    suspend fun addNewPoints(point: Long
    ) {
        val currentPoints = getCurrentPoints()
        val newPoints = currentPoints + point
        if(newPoints > 0) {
            updateCurrentPoints(newPoints)
        }
    }

    @Transaction
    suspend fun updateCurrentHighScore(point: Long) {
        val currentHighScore = getCurrentHighScore()
        if(point > currentHighScore) {
            updateHighScore(point)
        }
    }


}