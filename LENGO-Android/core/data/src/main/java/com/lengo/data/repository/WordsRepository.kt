package com.lengo.data.repository

import androidx.compose.ui.graphics.Color
import com.lengo.common.ALL_WORDS_WITHOUT_GREY
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.GREEN_WORDS
import com.lengo.common.LengoDispatchers
import com.lengo.common.RED_WORDS
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.YELLOW_WORDS
import com.lengo.common.extension.getDate
import com.lengo.common.extension.replacePlaceholder
import com.lengo.common.extension.toTransliteratorString
import com.lengo.common.ui.theme.Green
import com.lengo.common.ui.theme.Orange
import com.lengo.common.ui.theme.Red
import com.lengo.common.ui.theme.lightGrey
import com.lengo.data.BuildConfig
import com.lengo.data.datasource.TransliteratorProvider
import com.lengo.database.appdatabase.doa.DateStatsDoa
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.DateStatsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.model.data.quiz.Word
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class WordsRepository @Inject constructor(
    private val userDoa: UserDoa,
    private val packsDao: PacksDao,
    private val dataStatsDoa: DateStatsDoa,
    private val transliteratorProvider: TransliteratorProvider,
    private val userRepository: UserRepository,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun addWord(
        packId: Long,
        lectionID: Long,
        owner: Long,
        type: String,
        lang: String,
        ownlang: String,
        ownWord: String,
        selWord: String
    ) {
        val existingObjs = packsDao.getObjects(packId, lectionID, type, owner, lang)?.size?.toLong() ?: 0L
        val objEnitiy = ObjectEntity(
            iVal = -1,
            iVal_pushed = false,
            last_retrieval = 0L,
            lec = lectionID,
            lng = lang,
            obj = existingObjs,
            type = type,
            owner = owner,
            pck = packId,
            pushed = false,
            own = mapOf(Pair(ownlang, listOf(ownWord))),
            sel = listOf(selWord)
        )
        packsDao.insertObject(objEnitiy)
    }

    suspend fun updateWord(
        packId: Long,
        lectionID: Long,
        owner: Long,
        type: String,
        lang: String,
        ownlang: String,
        ownWord: String,
        selWord: String
    ) {
        packsDao.updateUserObjects(packId, lectionID, type, owner, lang, mapOf(Pair(ownlang, listOf(ownWord))),
            listOf(selWord))
    }


    suspend fun getWords(
        packId: Long,
        lectionID: Long,
        owner: Long,
        type: String,
        lang: String
    ): ImmutableList<Word> {
        val userLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        val unSortedWords = packsDao.getObjects(packId, lectionID, type, owner, lang)
        val sortedWords = unSortedWords?.sortedWith(Comparator { t, t2 ->
            if(t.spacedRepetitionRank() == t2.spacedRepetitionRank()) {
                if(t.knowledgeRank() == t2.knowledgeRank()) {
                    return@Comparator t.obj.compareTo(t2.obj)
                }
                return@Comparator t.knowledgeRank().compareTo(t2.knowledgeRank())
            }
            return@Comparator t.spacedRepetitionRank().compareTo(t2.spacedRepetitionRank())
        })

        val limit = if((sortedWords?.size ?: 0) > 7)  7 else sortedWords?.size?: 0

        val words = sortedWords?.mapIndexed { index, obj ->

            val isChecked = index < limit

            if (obj.type.contains(SYS_GRAMMER)) {

                val dWord = (obj.own?.values?.first() as List<String>)[0]
                val sPlacholderWord = obj.sel?.getOrNull(0) ?: ""
                val sWord = dWord.replacePlaceholder(sPlacholderWord)

                Word(
                    obj.pck,
                    obj.owner,
                    obj.lec,
                    obj.obj,
                    obj.type,
                    dWord,
                    sWord,
                    transliteratorProvider.getTransliteration(userLang.sel)
                        .toTransliteratorString(sWord),
                    sPlacholderWord,
                    transliteratorProvider.getTransliteration(userLang.sel)
                        .toTransliteratorString(sPlacholderWord),
                    true,
                    isChecked,
                    obj.color()
                )

            } else {

                val dWord = (obj.own?.get(userLang.own)
                    ?: obj.own?.get(DEFAULT_OWN_LANG)
                    ?: obj.own?.values?.first() ?: emptyList()).getOrNull(0) ?: ""
                val sword = obj.sel?.getOrNull(0) ?: ""

                Word(
                    obj.pck,
                    obj.owner,
                    obj.lec,
                    obj.obj,
                    obj.type,
                    dWord,
                    sword,
                    transliteratorProvider.getTransliteration(userLang.sel)
                        .toTransliteratorString(sword),
                    "",
                    "",
                    false,
                    isChecked,
                    obj.color()
                )
            }
        }?.toImmutableList()

        return words ?: persistentListOf()
    }


    fun userWords(wordColor: Int = ALL_WORDS_WITHOUT_GREY): Flow<List<Word>> {
        return userRepository.observeUserEntitySelAndDevice.flatMapLatest { lang ->
            packsDao.getAllObjects(lang.sel).filterNotNull().map { objs ->
                var isChecked = 0
                objs.mapIndexed { index, obj ->

                    if (obj.type.contains(SYS_GRAMMER) || obj.iVal < 0) {
                        return@mapIndexed null
                    }

                    val color: Color = when {
                        obj.iVal in 0..1 -> {
                            Red
                        }
                        obj.iVal in 2..4 -> {
                            Orange
                        }
                        obj.iVal >= 5 -> {
                            Green
                        }
                        else -> {
                            lightGrey
                        }
                    }

                    if (wordColor == RED_WORDS) {
                        if (color != Red) {
                            return@mapIndexed null
                        }
                    }
                    if (wordColor == YELLOW_WORDS) {
                        if (color != Orange) {
                            return@mapIndexed null
                        }
                    }

                    if (wordColor == GREEN_WORDS) {
                        if (color != Green) {
                            return@mapIndexed null
                        }
                    }


                    val dWord = (obj.own?.get(lang.own)
                        ?: obj.own?.get(DEFAULT_OWN_LANG)
                        ?: obj.own?.values?.first() ?: emptyList()).getOrNull(0) ?: ""
                    val sword = obj.sel?.getOrNull(0) ?: ""

                    if(dWord.isEmpty() || sword.isEmpty()) {
                        return@mapIndexed null
                    }
                    Word(
                        obj.pck,
                        obj.owner,
                        obj.lec,
                        obj.obj,
                        obj.type,
                        dWord,
                        sword,
                        transliteratorProvider.getTransliteration(lang.sel)
                            .toTransliteratorString(sword),
                        "",
                        "",
                        false,
                        isChecked++ < 8,
                        color
                    )

                }.filterNotNull()
            }
        }.flowOn(ioDispatcher)

    }

    suspend fun addFakeData() {
        val scores = listOf(0,2,6)
        val editedVocabs = listOf(22L,30L,45L,50L,100L)
        val lang = BuildConfig.FLAVOR_LANG_CODE
        val objects = packsDao.getAllObjectsSync(lang)
        val currentDate = System.currentTimeMillis()
        val deviceLocale = DEFAULT_OWN_LANG
        objects?.map { obj ->
            packsDao.updateScore(obj.obj,obj.pck,obj.lec,obj.type,obj.owner,scores.random(),lang,currentDate)
        }
        for (n in -11..0) {
            val date = getDate(n, deviceLocale)
            dataStatsDoa.insertDateState(DateStatsEntity(date = date, lng = lang, edited_vocab = editedVocabs.random(), pushed = false))
        }
        //TestIdlingResource.decrement()

    }

    fun ObjectEntity.knowledgeRank(): Int {
        return iVal + 2
    }

    fun ObjectEntity.color(): Color {
        return when {
            iVal in 0 .. 1 -> { Red }
            iVal in 2 .. 4 -> { Orange }
            iVal >= 5 -> { Green }
            else -> { lightGrey }
        }
    }

    fun ObjectEntity.spacedRepetitionRank(): Int {
        val currentDate = System.currentTimeMillis()
        val minutesSinceLastUse: Long = (currentDate - last_retrieval) / 60000
        return when(iVal) {
            0 -> 1
            1 -> if (minutesSinceLastUse >= 10) { 2 }   else 9
            2 -> if (minutesSinceLastUse >= 60) { 3 }   else 9
            3 -> if (minutesSinceLastUse >= 1440) { 4 } else 9
            4 -> if (minutesSinceLastUse >= 2880) { 5 } else 9
            5 -> if (minutesSinceLastUse >= 4320) { 6 } else 9
            6 -> if (minutesSinceLastUse >= 8640) { 7 } else 9
            -1 ->  8
            else -> 9
        }
    }

}