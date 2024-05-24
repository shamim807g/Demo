package com.lengo.database.appdatabase.doa

import androidx.room.*
import com.lengo.database.appdatabase.model.DateStatsEntity

@Dao
interface DateStatsDoa {

    @Query("SELECT * FROM DateStats")
    suspend fun getAllData(): List<DateStatsEntity>

    @Query("SELECT * FROM DateStats where pushed = :pushed")
    suspend fun getAllPushedDateData(pushed: Boolean = false): List<DateStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateState(dates: List<DateStatsEntity>): LongArray

    @Query("Select COUNT(*) FROM DateStats WHERE date = :date AND lng = :lang")
    suspend fun countDateState(
        date: String,
        lang: String
    ): Int?

    @Query("Select * FROM DateStats WHERE date = :date AND lng = :lang")
    suspend fun getDateState(
        date: String,
        lang: String
    ): DateStatsEntity?

    @Query("Select date FROM DateStats WHERE lng = :lang")
    suspend fun getDates(
        lang: String
    ): List<String>?

    data class DateWithEditedGram(val edited_gram: Long, val date: String)

    @Query("Select edited_gram,date FROM DateStats WHERE lng = :lang ORDER BY date ASC LIMIT :limit")
    suspend fun getEditedGram(
        lang: String,
        limit: Int = 7
    ): List<DateWithEditedGram>?

    data class DateWithEditedVocab(val edited_vocab: Long, val date: String)

    @Query("Select edited_vocab,date FROM DateStats WHERE lng = :lang ORDER BY date ASC LIMIT :limit")
    suspend fun getEditedVocabs(
        lang: String,
        limit: Int = 7
    ): List<DateWithEditedVocab>?


//    @Query("Select AVG(edited_gram) FROM DateStats WHERE lng = :lang ORDER BY date DESC LIMIT 7")
//    suspend fun getEditedGrammerAverage(
//        lang: String
//    ): Long?

    data class SecoundsWithDate(val seconds: Long, val date: String)

    @Query("Select seconds,date FROM DateStats WHERE lng = :lang AND seconds > 59 ORDER BY date ASC LIMIT :limit")
    suspend fun getSecounds(
        lang: String,
        limit: Int = 5
    ): List<SecoundsWithDate>?

    @Query("Select AVG(seconds) FROM DateStats WHERE lng = :lang AND seconds > 59 ORDER BY date DESC LIMIT 6")
    suspend fun getSecoundsAverage(
        lang: String
    ): Long?

    @Query("Select SUM(edited_vocab) FROM DateStats WHERE lng = :lang")
    suspend fun sumOfEditedVocabs(
        lang: String
    ): Int?

    @Query("Select SUM(edited_gram) FROM DateStats WHERE lng = :lang")
    suspend fun sumOfEditedGram(
        lang: String
    ): Int?

    @Query("Select SUM(seconds) FROM DateStats WHERE lng = :lang")
    suspend fun sumOfSecound(
        lang: String
    ): Long?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateState(packs: DateStatsEntity)


    @Query("UPDATE DateStats SET right_edited_vocab = :right_edited_vocab,pushed = :pushed,right_edited_gram = :right_edited_gram,edited_gram = :edited_gram,edited_vocab = :edited_vocab WHERE date = :date AND lng = :lang")
    suspend fun updatePackTime(
        date: String,
        lang: String,
        right_edited_vocab: Long,
        right_edited_gram: Long,
        edited_vocab: Long,
        edited_gram: Long,
        pushed: Boolean = false
    )

    @Query("UPDATE DateStats SET seconds = :secound,pushed = :pushed WHERE date = :date AND lng = :lang")
    suspend fun updateSecounds(
        date: String,
        lang: String,
        secound: Long,
        pushed: Boolean = false
    )

    @Transaction
    suspend fun updateOrInsertSecounds(
        date: String,
        lang: String,
        sec: Long,
    ) {
        val currentDateState = getDateState(date, lang)
        if (currentDateState == null) {
            insertDateState(DateStatsEntity(date = date, lng = lang, seconds = sec, pushed = false))
        } else {
            val newSecounds = currentDateState.seconds + sec
            updateSecounds(date = date, lang = lang, secound = newSecounds)
        }
    }

    @Transaction
    suspend fun updateDateStateCorrectVocb(
        date: String,
        lang: String,
    ) {
        val currentDateState = getDateState(date, lang)
        if (currentDateState == null) {
            insertDateState(
                DateStatsEntity(
                    date = date,
                    lng = lang,
                    right_edited_vocab = 1,
                    edited_vocab = 1,
                    pushed = false
                )
            )
        } else {
            val new_right_edited_vocab = currentDateState.right_edited_vocab + 1
            val new_edited_vocab = currentDateState.edited_vocab + 1

            updatePackTime(
                date = date, lang = lang,
                right_edited_vocab = new_right_edited_vocab,
                right_edited_gram = currentDateState.right_edited_gram,
                edited_vocab = new_edited_vocab,
                edited_gram = currentDateState.edited_gram
            )
        }
    }

    @Transaction
    suspend fun updateDateStateCorrectGram(
        date: String,
        lang: String,
    ) {
        val currentDateState = getDateState(date, lang)
        if (currentDateState == null) {
            insertDateState(
                DateStatsEntity(
                    date = date,
                    lng = lang,
                    right_edited_gram = 1,
                    edited_gram = 1,
                    pushed = false
                )
            )
        } else {
            val new_right_edited_gram = currentDateState.right_edited_gram + 1
            val new_edited_gram = currentDateState.edited_gram + 1

            updatePackTime(
                date = date, lang = lang,
                right_edited_vocab = currentDateState.right_edited_vocab,
                right_edited_gram = new_right_edited_gram,
                edited_vocab = currentDateState.edited_vocab,
                edited_gram = new_edited_gram
            )
        }
    }

    @Transaction
    suspend fun updateDateStateVocab(
        date: String,
        lang: String,
    ) {
        val currentDateState = getDateState(date, lang)
        if (currentDateState == null) {
            insertDateState(
                DateStatsEntity(
                    date = date,
                    lng = lang,
                    edited_vocab = 1,
                    pushed = false
                )
            )
        } else {
            val new_edited_vocab = currentDateState.edited_vocab + 1
            updatePackTime(
                date = date, lang = lang,
                right_edited_vocab = currentDateState.right_edited_vocab,
                right_edited_gram = currentDateState.right_edited_gram,
                edited_vocab = new_edited_vocab,
                edited_gram = currentDateState.edited_gram
            )
        }
    }

    @Transaction
    suspend fun updateDateStateGram(
        date: String,
        lang: String,
    ) {
        val currentDateState = getDateState(date, lang)
        if (currentDateState == null) {
            insertDateState(
                DateStatsEntity(
                    date = date,
                    lng = lang,
                    edited_gram = 1,
                    pushed = false
                )
            )
        } else {
            val new_edited_gram = currentDateState.edited_gram + 1
            updatePackTime(
                date = date, lang = lang,
                right_edited_vocab = currentDateState.right_edited_vocab,
                right_edited_gram = currentDateState.right_edited_gram,
                edited_vocab = currentDateState.edited_vocab,
                edited_gram = new_edited_gram
            )
        }
    }

}