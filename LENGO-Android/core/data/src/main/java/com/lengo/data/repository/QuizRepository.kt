package com.lengo.data.repository

import android.icu.text.Transliterator
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.di.ApplicationScope
import com.lengo.common.extension.randomString
import com.lengo.common.extension.removeAngleBraces
import com.lengo.common.extension.toTransliteratorString
import com.lengo.common.getDrawables
import com.lengo.data.datasource.TransliteratorProvider
import com.lengo.data.interactors.FetchTextImage
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.model.data.ObjParam
import com.lengo.model.data.SettingModel
import com.lengo.model.data.quiz.CharItem
import com.lengo.model.data.quiz.Game
import com.lengo.model.data.quiz.Memorize
import com.lengo.model.data.quiz.Quiz
import com.lengo.model.data.quiz.QuizChars
import com.lengo.model.data.quiz.QuizFourQues
import com.lengo.model.data.quiz.QuizListening
import com.lengo.model.data.quiz.QuizSpeaking
import com.lengo.model.data.quiz.QuizThreeQues
import com.lengo.model.data.quiz.ResultType
import com.lengo.model.data.quiz.StringWithTran
import com.lengo.model.data.quiz.Test
import com.lengo.model.data.quiz.TestType
import com.lengo.model.data.quiz.Word
import com.lengo.model.data.quiz.WordItem
import com.lengo.preferences.LengoPreference
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val fetchTextImage: FetchTextImage,
    @ApplicationScope val appScope: CoroutineScope,
    @Dispatcher(LengoDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val imageRepository: ImageRepository,
    private val lengoPreference: LengoPreference,
    private val userDoa: UserDoa,
    private val trProvider: TransliteratorProvider
) {
    var words: ImmutableList<Word> = persistentListOf()
    val mutex = Mutex()
    val quizImage = mutableStateMapOf<String, String?>()

    fun submitWords(words: List<Word>) {
        this.words = words.toImmutableList()
    }


    suspend fun prepareQuizSync(): ImmutableList<Game> {
        val settingModel = lengoPreference.syncSettingModel()
        val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        if(this.words.isNullOrEmpty()) {
            return persistentListOf()
        } else {
            val checkedWord = this.words.filter { it.isChecked }
            if (checkedWord.isNullOrEmpty()) {
                return persistentListOf()
            } else {
                if (checkedWord.first().isGram) {
                    return prepareGramerQuiz(
                        this.words,
                        settingModel,
                        trProvider.getExistingTransliterator(),
                        selLang.own,
                        selLang.sel
                    )
                } else {
                    val gameList = prepareVocbQuiz(
                        this.words,
                        settingModel,
                        trProvider.getExistingTransliterator(),
                        selLang.own,
                        selLang.sel
                    )
                    appScope.launch {
                        fetchTextImage.execute(
                            gameList,
                            quizImage,
                            selLang.sel
                        )
                    }
                    return gameList
                }
            }
        }
    }

    fun prepareQuiz(): Flow<List<Game>> {
        return combine(
            userRepository.observeSettingModel,
            userRepository.observeUserEntitySelAndDevice
        ) { settingModel, lang ->
            Triple(settingModel,words, lang)
        }.transformLatest {
            if(it.second.isNullOrEmpty()) {
              emit(emptyList())
            } else {
                val checkedWord = it.second.filter { it.isChecked }
                if (checkedWord.isNullOrEmpty()) {
                    emit(emptyList())
                } else {
                    if (checkedWord.first().isGram) {
                        emit(
                            prepareGramerQuiz(
                                it.second,
                                it.first,
                                trProvider.getExistingTransliterator(),
                                it.third.own,
                                it.third.sel
                            )
                        )
                    } else {
                        val gameList = prepareVocbQuiz(
                            it.second,
                            it.first,
                            trProvider.getExistingTransliterator(),
                            it.third.own,
                            it.third.sel
                        )
                        emit(gameList)

                        appScope.launch {
                            fetchTextImage.execute(
                                gameList,
                                quizImage,
                                it.third.sel
                            )
                        }
                    }
                }
            }
        }
    }


    fun prepareVocbQuiz(
        wordList: List<Word>,
        settingModel: SettingModel,
        transliterator: Transliterator?,
        deviceLanguageCode: String,
        selectedLanguageCode: String
    ): ImmutableList<Game> {
        logcat { "prepareVocbQuiz = " }
        val finalList = mutableListOf<Game>()
        val quizList = mutableListOf<Quiz>()
        val quizListeningList = mutableListOf<QuizListening>()
        val quizSpeakingList = mutableListOf<QuizSpeaking>()
        val testList = mutableListOf<Test>()
        val memorizeList = mutableListOf<Memorize>()

        words.forEachIndexed { index, obj ->
            if (obj.isChecked) {

                val wordsToFilterOut = mutableListOf<String>()
                val currentQuesWord = obj.deviceLngWord

                val currentWord = StringWithTran(
                    text = obj.selectedLngWord,
                    tranText = obj.selectedLngWordTransliterator,
                    objId =  obj.obj
                )
                wordsToFilterOut.add(currentWord.text)
                logcat { "currentWord = : ${currentWord.text} tran: ${currentWord.tranText}" }

                val firstWord = getStringWithTran(
                    transliterator, words, obj.selectedLngWord,
                    wordsToFilterOut
                )
                wordsToFilterOut.add(firstWord.text)
                logcat { "firstWord = : ${firstWord.text} tran: ${firstWord.tranText}" }

                val secWord = getStringWithTran(
                    transliterator, words, obj.selectedLngWord,
                    wordsToFilterOut
                )
                wordsToFilterOut.add(secWord.text)
                logcat { "secWord = : ${secWord.text} tran: ${secWord.tranText}" }

                val thirdWord = getStringWithTran(
                    transliterator, words, obj.selectedLngWord,
                    wordsToFilterOut
                )
                wordsToFilterOut.add(thirdWord.text)
                logcat { "thirdWord = : ${thirdWord.text} tran: ${thirdWord.tranText}" }


                val fouroptions = listOf(
                    firstWord,
                    secWord,
                    thirdWord,
                    currentWord
                ).shuffled()

                val threeOption = listOf(
                    firstWord,
                    secWord,
                    currentWord
                ).shuffled()


                if (settingModel.quizTask || settingModel.allSettingareFalse) {
                    val quizTypeid = if (index == 0 || index == 1) (1..2).random() else (1..3).random()

                    when (quizTypeid) {
                        1 -> {
                            val quiz = QuizFourQues(
                                ObjParam(
                                    obj.obj,
                                    obj.pck,
                                    obj.lec,
                                    obj.type,
                                    obj.owner,
                                ),
                                question = currentQuesWord,
                                fouroptions,
                                correctAnswers = listOf(currentWord)
                            )
                            quizList.add(quiz)
                            appScope.launch {
                                fouroptions.map { option ->
                                    withContext(ioDispatcher) {
                                        val path = imageRepository.getObjectImageFilePath(
                                            selectedLanguageCode, option.text, quiz.objParam.type,
                                            option.objId, quiz.objParam.owner, quiz.objParam.pck, quiz.objParam.lec
                                        )
                                        mutex.withLock {
                                            quizImage.put(
                                                "IMG_${quiz.objParam.owner}_${quiz.objParam.type}_${quiz.objParam.pck}_${quiz.objParam.lec}_${option.objId}",
                                                path
                                            )
                                        }
                                    }
                                }
                            }

                        }
                        2 -> {
                            quizList.add(
                                QuizThreeQues(
                                    ObjParam(
                                        obj.obj,
                                        obj.pck,
                                        obj.lec,
                                        obj.type,
                                        obj.owner,
                                    ),
                                    question = currentQuesWord,
                                    threeOption,
                                    correctAnswers = listOf(currentWord)
                                )
                            )
                        }
                        else -> {
                            quizList.add(
                                QuizChars(
                                    ObjParam(
                                        obj.obj,
                                        obj.pck,
                                        obj.lec,
                                        obj.type,
                                        obj.owner,
                                    ),
                                    currentQuesWord,
                                    StringWithTran(
                                        getNormalizeOption(currentWord.text),
                                        getNormalizeOption(currentWord.tranText),
                                        objId =  obj.obj
                                    ),
                                    correctAnsWithPlaceHolder = StringWithTran(objId = -1),
                                    isGram = false,
                                    getCharList(
                                        currentWord.text,
                                        currentWord.tranText,
                                        transliterator
                                    ),
                                    getSpaceIndex(currentWord.text)
                                )
                            )
                        }
                    }
                }

                if (settingModel.testTask || settingModel.allSettingareFalse) {
                    testList.add(
                        TestType(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            currentWord
                        )
                    )
                }

                if (settingModel.memorizeTask || settingModel.allSettingareFalse) {
                    memorizeList.add(
                        Memorize(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            getDrawables(deviceLanguageCode, selectedLanguageCode),
                            StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(objId =  -1),
                            isGram = false
                        )
                    )
                }

                if (settingModel.listeningTask || settingModel.allSettingareFalse) {
                    quizListeningList.add(
                        QuizListening(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(objId =  -1),
                            isGram = false,
                            getCharList(
                                currentWord.text,
                                currentWord.tranText,
                                transliterator
                            ),
                            getSpaceIndex(currentWord.text),
                            emptyList()
                        )
                    )
                }

                if (settingModel.speakingTask || settingModel.allSettingareFalse) {
                    quizSpeakingList.add(
                        QuizSpeaking(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(objId =  -1),
                            isGram = false,
                        )
                    )
                }
            }
        }
        finalList.addAll(memorizeList + quizList + quizListeningList + quizSpeakingList + testList + ResultType())
        return finalList.toImmutableList()
    }

    fun prepareGramerQuiz(
        wordList: List<Word>,
        settingModel: SettingModel,
        transliterator: Transliterator?,
        deviceLanguageCode: String,
        selectedLanguageCode: String
    ): ImmutableList<Game> {
        val finalList = mutableListOf<Game>()
        val quizList = mutableListOf<Quiz>()
        val quizListeningList = mutableListOf<QuizListening>()
        val quizSpeakingList = mutableListOf<QuizSpeaking>()
        val testList = mutableListOf<Test>()
        val memorizeList = mutableListOf<Memorize>()

        val words = wordList.toMutableList()
        words.forEachIndexed { index, obj ->
            if (obj.isChecked) {

                val wordsToFilterOut = mutableListOf<String>()

                val currentQuesWord = obj.deviceLngWord

                val currentWordWithPlaceHolder = StringWithTran(
                    text = obj.selectedLngWord,
                    tranText = obj.selectedLngWordTransliterator,
                    objId =  obj.obj
                )

                val currentWord = StringWithTran(
                    text = obj.selectedLngPlaceHolderWord,
                    tranText = obj.selectedLngPlaceHolderWordTransliterator,
                    objId =  obj.obj
                )
                wordsToFilterOut.add(currentWord.text)

                val firstOption = getStringPlaceHolderWithTran(
                    transliterator,
                    words, obj.selectedLngPlaceHolderWord,
                    wordsToFilterOut
                )
                wordsToFilterOut.add(firstOption.text)
                logcat { "firstOption: ${firstOption.text} tran: ${firstOption.tranText}" }

                val secOption = getStringPlaceHolderWithTran(
                    transliterator,
                    words,
                    obj.selectedLngPlaceHolderWord,
                    wordsToFilterOut
                )
                wordsToFilterOut.add(secOption.text)

                logcat { "secOption: ${firstOption.text} tran: ${firstOption.tranText}" }



                val threeOption = listOf(
                    firstOption,
                    secOption,
                    currentWord
                ).shuffled()

                if (settingModel.quizTask || settingModel.allSettingareFalse) {
                    when ((1..2).random()) {
                        1 -> {
                            quizList.add(
                                QuizChars(
                                    ObjParam(
                                        obj.obj,
                                        obj.pck,
                                        obj.lec,
                                        obj.type,
                                        obj.owner,
                                    ),
                                    currentQuesWord,
                                    correctAnsText = StringWithTran(
                                        getNormalizeOption(currentWord.text),
                                        getNormalizeOption(currentWord.tranText),
                                        objId =  obj.obj
                                    ),
                                    correctAnsWithPlaceHolder = StringWithTran(
                                        currentWordWithPlaceHolder.text,
                                        currentWordWithPlaceHolder.tranText,
                                        objId =  obj.obj
                                    ),
                                    isGram = true,
                                    getCharList(
                                        currentWord.text,
                                        currentWord.tranText,
                                        transliterator
                                    ),
                                    getSpaceIndex(currentWord.text)
                                )
                            )
                        }
                        2 -> {
                            quizList.add(
                                QuizThreeQues(
                                    ObjParam(
                                        obj.obj,
                                        obj.pck,
                                        obj.lec,
                                        obj.type,
                                        obj.owner,
                                    ),
                                    question = currentQuesWord,
                                    threeOption,
                                    correctAnswers = listOf(currentWord),
                                    correctAnswersWithPlaceHolder = currentWordWithPlaceHolder,
                                    isGram = true
                                )
                            )
                        }
                    }
                }
                if (settingModel.testTask || settingModel.allSettingareFalse) {
                    testList.add(
                        TestType(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(
                                currentWordWithPlaceHolder.text,
                                currentWordWithPlaceHolder.tranText,
                                objId =  obj.obj
                            ),
                            isGram = true,
                        )
                    )
                }

                if (settingModel.memorizeTask || settingModel.allSettingareFalse) {
                    memorizeList.add(
                        Memorize(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            getDrawables(deviceLanguageCode, selectedLanguageCode),
                            StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(
                                currentWordWithPlaceHolder.text,
                                currentWordWithPlaceHolder.tranText,
                                objId =  obj.obj
                            ),
                            isGram = true
                        )
                    )
                }

                if (settingModel.listeningTask || settingModel.allSettingareFalse) {
                    quizListeningList.add(
                        QuizListening(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            correctAnsText = StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(
                                currentWordWithPlaceHolder.text.removeAngleBraces(),
                                currentWordWithPlaceHolder.tranText.removeAngleBraces(),
                                objId =  obj.obj
                            ),
                            isGram = true,
                            getCharList(
                                currentWord.text,
                                currentWord.tranText,
                                transliterator
                            ),
                            getSpaceIndex(currentWord.text),
                            getWordList(currentWordWithPlaceHolder.text.removeAngleBraces(),
                                currentWordWithPlaceHolder.tranText.removeAngleBraces(),
                                transliterator
                            )
                        )
                    )
                }

                if (settingModel.speakingTask || settingModel.allSettingareFalse) {
                    quizSpeakingList.add(
                        QuizSpeaking(
                            ObjParam(
                                obj.obj,
                                obj.pck,
                                obj.lec,
                                obj.type,
                                obj.owner,
                            ),
                            currentQuesWord,
                            correctAnsText = StringWithTran(
                                getNormalizeOption(currentWord.text),
                                getNormalizeOption(currentWord.tranText),
                                objId =  obj.obj
                            ),
                            correctAnsWithPlaceHolder = StringWithTran(
                                currentWordWithPlaceHolder.text,
                                currentWordWithPlaceHolder.tranText,
                                objId =  obj.obj
                            ),
                            isGram = true
                        )
                    )
                }
            }
        }
        finalList.addAll(memorizeList + quizList + quizListeningList + quizSpeakingList + testList + ResultType())
        return finalList.toImmutableList()
    }

    fun getStringWithTran(
        transliterator: Transliterator?,
        words: List<Word>,
        currentWord: String,
        wordsToFilterOut: List<String>,
    ): StringWithTran {
        logcat { "getStringWithTran = wrods = : ${words} currentWord: ${currentWord} filter words = ${wordsToFilterOut}" }
        var text = ""
        var textWithTran = ""
        var wordId = -1L

        val wordsLeft = words.filter { !wordsToFilterOut.contains(it.selectedLngWord) }.distinctBy { it.selectedLngWord }
        val word = try {
            if (!wordsLeft.isNullOrEmpty()) {
                wordsLeft.randomOrNull()
            } else {
                null
            }
        } catch (ex: Exception) {
            null
        }
        if (word != null) {
           text = word.selectedLngWord
           textWithTran = word.selectedLngWordTransliterator
           wordId = word.obj
        } else {
            val chars = currentWord.toCharArray().distinct()
            if (chars.isNotEmpty()) {
                if (chars.size >= 4) {
                    var random = ""
                    while (random == "" || wordsToFilterOut.contains(random)) {
                        val mChars = chars.toMutableList()
                        val firstChar = chars[0]
                        mChars.shuffle()
                        random = mChars.joinToString("")
                        random += firstChar
                        textWithTran = transliterator.toTransliteratorString(random)
                    }
                    text = random
                } else {
                    var random = ""
                    while (random == "" || wordsToFilterOut.contains(random)) {
                        random = randomString(chars.size)
                    }
                    text = random
                    textWithTran = transliterator.toTransliteratorString(random)
                }
            } else {
                var random = ""
                while (random == "" || wordsToFilterOut.contains(random)) {
                    random = randomString(4)
                }
                text = random
                textWithTran = transliterator.toTransliteratorString(random)
            }
        }

        return StringWithTran(
            text = text,
            tranText = textWithTran,
            objId = wordId
        )
    }


    fun getStringPlaceHolderWithTran(
        transliterator: Transliterator?,
        words: List<Word>,
        currentWord: String,
        wordsToFilterOut: List<String>
    ): StringWithTran {
        var text = ""
        var textWithTran = ""
        var wordId = -1L

        val wordsLeft = words.filter { !wordsToFilterOut.contains(it.selectedLngPlaceHolderWord) }.distinctBy { it.selectedLngPlaceHolderWord }
        val word = try {
            if (!wordsLeft.isNullOrEmpty()) {
                wordsLeft.randomOrNull()
            } else {
                null
            }
        } catch (ex: Exception) {
            null
        }
        if (word != null) {
            text = word.selectedLngPlaceHolderWord
            textWithTran = word.selectedLngPlaceHolderWordTransliterator
            wordId = word.obj
        } else {
            val chars = currentWord.toCharArray().distinct()
            if (chars.isNotEmpty()) {
                if (chars.size >= 4) {
                    var random = ""
                    while (random == "" || wordsToFilterOut.contains(random)) {
                        val mChars = chars.toMutableList()
                        val firstChar = chars[0]
                        mChars.shuffle()
                        random = mChars.joinToString("")
                        random += firstChar
                    }
                    text = random
                    textWithTran = transliterator.toTransliteratorString(random)
                } else {
                    var random = ""
                    while (random == "" || wordsToFilterOut.contains(random)) {
                        random = randomString(chars.size)
                    }
                    text = random
                    textWithTran = transliterator.toTransliteratorString(random)
                }
            } else {
                var random = ""
                while (random == "" || wordsToFilterOut.contains(random)) {
                    random = randomString(4)
                }
                text = random
                textWithTran = transliterator.toTransliteratorString(random)
            }
        }

        return StringWithTran(
            text = text,
            tranText = textWithTran,
            objId = wordId
        )
    }


    fun getNormalizeOption(option: String): String {
        return option.trim().replace("\\s+".toRegex(), " ")
    }

    fun getSpaceIndex(option: String): List<Int> {
        val normalizeOptionText = getNormalizeOption(option)
        val spaceIndex = mutableListOf<Int>()
        normalizeOptionText.toCharArray().forEachIndexed { index, c ->
            if (c == ' ') {
                spaceIndex.add(index)
            }
        }
        return spaceIndex
    }


    fun getWordList(option: String,
                     optionTran: String,
                     transliterator: Transliterator?) : List<WordItem> {
        if (optionTran.isNullOrEmpty()) {
            val normalizeOptionText = getNormalizeOption(option)
            val array = normalizeOptionText.split(" ")
            val list = array.map {
                WordItem(it, "", mutableStateOf(true))
            }
            return list.shuffled()
        } else {
            val normalizeOptionText = getNormalizeOption(option)
            val array = normalizeOptionText.split(" ")
            val list = mutableListOf<WordItem>()
            for (i in array.indices) {
                list.add(
                    WordItem(
                        array[i],
                        transliterator.toTransliteratorString(array[i]),
                        mutableStateOf(true)
                    )
                )
            }
            return list.shuffled()
        }

    }

    fun getCharList(
        option: String,
        optionTran: String,
        transliterator: Transliterator?
    ): List<CharItem> {
        if (optionTran.isNullOrEmpty()) {
            val normalizeOptionText = getNormalizeOption(option)
            val array = normalizeOptionText.replace("\\s".toRegex(), "").toCharArray()
            array.shuffle()
            val list = array.map {
                CharItem(it, "", mutableStateOf(true))
            }
            return list
        } else {
            val normalizeOptionText = getNormalizeOption(option)
            val array = normalizeOptionText.replace("\\s".toRegex(), "").toCharArray()
            val list = mutableListOf<CharItem>()
            for (i in array.indices) {
                list.add(
                    CharItem(
                        array[i],
                        transliterator.toTransliteratorString(array[i].toString()),
                        mutableStateOf(true)
                    )
                )
            }
            list.shuffle()
            return list
        }

    }

}