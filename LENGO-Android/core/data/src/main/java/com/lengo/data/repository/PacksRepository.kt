package com.lengo.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.USER_VOCAB
import com.lengo.common.USER_VOCAB_SUGGESTED
import com.lengo.common.extension.getCurrentDate
import com.lengo.common.fastForEach
import com.lengo.common.mapToSetupStructureLangCode
import com.lengo.common.subscriptionsList
import com.lengo.common.uni_images
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.mapper.fromListPackSuggestionResponseToObjectEntity
import com.lengo.data.mapper.toLection
import com.lengo.data.mapper.toLectionEntity
import com.lengo.data.mapper.toListOfLection
import com.lengo.data.mapper.toListOfObject2
import com.lengo.data.mapper.toPackEntity
import com.lengo.data.mapper.toUIPackWithLectionsEntity3
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.TransactionRunnerDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.ObjectEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import com.lengo.database.jsonDatabase.model.JsonPack
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.database.newuidatabase.model.PacksUIEntity
import com.lengo.model.data.BADGE
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.model.data.UserEditedPack
import com.lengo.network.ApiService
import com.lengo.network.model.ObjSuggestionDownloadRequest
import com.lengo.network.model.PackKey
import com.lengo.network.model.PackPublic
import com.lengo.network.model.PackPublicResponse
import com.lengo.network.model.PackSuggestionRequest
import com.lengo.network.model.PackSuggestionResponse
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import logcat.logcat
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PacksRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val packsDao: PacksDao,
    private val uiPackLecDoa: UIPackLecDoa,
    private val jsonPackDao: JsonPackDao,
    private val userDoa: UserDoa,
    private val lengoPreference: LengoPreference,
    private val lengoDataSource: LengoDataSource,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val apiService: ApiService,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
    private val transactionRunner: TransactionRunnerDao
) {
    val paidPackList = mutableStateListOf<Pack>()
    var jsonPackList: List<JsonPack>? = null

    suspend fun insertUserPackAndLection(packName: String, lectionName: String,onInsert: (packName: String, lec: Lection) -> Unit) {
        val dbUser = userDoa.currentUser()
        val packId = packsDao.insertUserPack(
            packName, dbUser?.sel ?: DEFAULT_SEL_LANG,
            dbUser?.own ?: DEFAULT_OWN_LANG, dbUser?.userid ?: -1
        )
        val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        val lecId = packsDao.insertUserPackLection(
            lectionName,
            selLang.sel,
            selLang.own,
            packId,
            dbUser?.userid ?: -1
        )
        logcat("IMAGE:") { "before textToImage" }
        imageRepository.updateUserLectionImage(
            dbUser?.userid ?: -1,
            USER_VOCAB,
            packId,
            lecId,
            selLang.sel,
            lectionName
        )
        val userLectionName = "$lectionName ${if(lecId > 0) lecId else ""}"
        val userPackName = "$packName ${if(packId > 0) packId else ""}"

        uiPackLecDoa.insertPackUI(
            PacksUIEntity(
                pck = packId,
                owner = dbUser?.userid ?: -1,
                pack_title = userPackName,
                packNameMap = mapOf(Pair(dbUser?.own ?: DEFAULT_OWN_LANG, userPackName)),
                type = USER_VOCAB,
                coins = 0L,
                emoji = "✏️",
                version = -1,
                lang = dbUser?.sel ?: DEFAULT_SEL_LANG,
                badge = BADGE.NONE,
                subscribed = 0
            )
        )

        uiPackLecDoa.insertLectionUI(
            LectionUIEntity(
                lec = lecId,
                lang = selLang.sel,
                pck = packId,
                owner = dbUser?.userid ?: -1,
                type = USER_VOCAB,
                lec_title = lectionName,
                lec_nameMap = mapOf(Pair(selLang.own, lectionName)),
                errorDrawable = -1,
                example = null,
                explanation = null,
                lec_image = "placeholder"
            )
        )

        onInsert(userPackName, Lection(
            type = USER_VOCAB,
            pck = packId,
            owner = dbUser?.userid ?: -1,
            lec = lecId,
            title = lectionName,
            nameMap = mapOf(Pair(selLang.own, userLectionName)),
            lang = selLang.sel
        ))
    }

    suspend fun updateUserPackName(pack: Long, title: String) {
        val currentUser = userDoa.currentUser()
        packsDao.updateUserPackTitle(
            mapOf(Pair(currentUser?.own ?: DEFAULT_OWN_LANG, title)),
            pack,
            USER_VOCAB,
            currentUser?.userid ?: -1
        )
        uiPackLecDoa.updateUIPackTitle(
            mapOf(Pair(currentUser?.own ?: DEFAULT_OWN_LANG, title)),
            title,
            pack,
            USER_VOCAB,
            currentUser?.userid ?: -1
        )
    }

    suspend fun updatePackPublicStatus(pack: Long, lang: String, status: Boolean): PackPublicResponse? {
        return try {
            val currentUser = userDoa.currentUser()
            val response = apiService.updatePackPublicReview(
                PackPublic(
                    USER_VOCAB,
                    currentUser?.userid?.toInt() ?: -1,
                    pack.toInt(),
                    status
                )
            )
            packsDao.updateUserPackPublish(
                submitted = response?.accepted ?: false,
                pck = pack,
                type = USER_VOCAB,
                lang = lang,
                owner = currentUser?.userid ?: -1
            )
            response
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun updatePackEmoji(
        packId: Long,
        type: String,
        owner: Long,
        lang: String,
        emoji: String
    ) {
        packsDao.updatePackEmoji(emoji, packId, type, owner, lang)
        uiPackLecDoa.updateUIPackEmoji(emoji, packId, type, owner, lang)
    }

    suspend fun fetchUserPacks(): List<UserEditedPack> {
        val selLang =
            userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        val packs = packsDao.getUserPacks(USER_VOCAB, selLang.sel)
        return packs?.map { pack ->
            if (!pack.title.containsKey(selLang.own)) {
                return@map null
            }
            val lections = packsDao.userPackLection(pack.pck, pack.owner, pack.type, pack.lng)
            val listOfLections = lections?.toListOfLection(selLang.own, selLang.sel) ?: emptyList()
            val editPac = pack.toPack(selLang.own, selLang.sel, listOfLections)
            var totalObject = 0
            var userObject = 0
            lections?.forEach { lection ->
                val objects = packsDao.getObjects(
                    lection.pck,
                    lection.lec,
                    lection.type,
                    lection.owner,
                    lection.lng
                ) ?: emptyList()
                totalObject += objects.size
                userObject += objects.filter { it.iVal > -1 }.size
            }
            UserEditedPack(editPac, totalCount = totalObject, userCount = userObject)
        }?.filterNotNull() ?: emptyList()
    }

    suspend fun fetchUserPacksPublicStatus(pack: Long, owner: Long, lang: String): Boolean {
        return packsDao.userPackPublicStatus(USER_VOCAB, pack, lang, owner) ?: false
    }

    fun observeDiscoverPack(): Flow<Map<String, List<Pack>>> {
        return userRepository.observeUserEntitySelAndDevice.flatMapLatest { userLang ->
            getPacksAndLectionsType(selLang = userLang.sel, excludedType = USER_VOCAB)
        }.map { pMap ->
            val packMap = pMap.mapValues { it.value }
            filterCoinPack(packMap)
            packMap
        }.catch {
            emit(emptyMap())
        }
    }

    fun getPacksAndLectionsType(
        selLang: String,
        excludedType: String
    ): Flow<Map<String, List<Pack>>> {
        return uiPackLecDoa.getPacksAndLectionsType(excludedType, selLang)
            .distinctUntilChanged().map { packMap ->
                var packs = mutableListOf<Pack>()
                packMap?.forEach {
                    val pack = it.key.toPack(it.value.toLectionList())
                    packs.add(pack)
                }

                val isDiscoverData = lengoPreference.getDiscoverData()
                val date = getCurrentDate()
                var random: Random? = null
                if (isDiscoverData != null && isDiscoverData != date) {
                    val dayofYear = Calendar.getInstance(TimeZone.getDefault())
                        .get(Calendar.DAY_OF_YEAR)
                    random = Random(dayofYear)
                } else {
                    if (isDiscoverData == null) {
                        lengoPreference.setDiscoverData()
                    }
                }

                packs = if (random == null) {
                    packs.sortedBy { it.pck }.toMutableList()
                } else {
                    packs.shuffled(random).toMutableList()
                }
                val vock = packs.filter { it.type == SYS_VOCAB }
                val gram = packs.filter { it.type == SYS_GRAMMER }
                val suggested = packs.filter { it.type == USER_VOCAB_SUGGESTED }
                (vock + gram + suggested).groupBy { it.type }
            }
    }

    fun filterCoinPack(packMap: Map<String, List<Pack>>) {
        val packs = mutableListOf<Pack>()
        packMap.entries.forEach {
            it.value.forEach { pack ->
                if (pack.coins > 0) {
                    packs.add(pack)
                }
            }
        }
        paidPackList.clear()
        paidPackList.addAll(packs.toList())
    }

    suspend fun updatePacksForSubAndUnSub() {
        val subList = subscriptionsList
        val usrLang = userDoa.getSyncUserLang() ?: return
        //logcat { "subList ${subList.size} userLang ${usrLang}" }
        val langToRemove =
            subList.filter { it.subscribed.value == false && it.lang != "all" }.map { it.lang }

        val subItem = subList.find { it.productId == "sub_all_lang" }
        if (subItem != null) {
            logcat { "subItem != null" }
            if (subItem.subscribed.value != null && subItem.subscribed.value == true) {
                logcat { "subscribed true" }
                uiPackLecDoa.updateAllPacksWithCoinsForAll(BADGE.GET, 1)
            } else if (subItem.subscribed.value != null && subItem.subscribed.value == false) {
                logcat { "subscribed false" }
                uiPackLecDoa.updateAllPacksWithCoinsForAll(BADGE.COIN, 0)
            }
        }
        val langItem = subList.find { it.lang == usrLang.sel }
        if (langItem != null) {
            logcat { "langItem != null ${langItem}" }
            if (langItem.subscribed.value != null && langItem.subscribed.value == true) {
                logcat { "langItem subscribed true" }
                uiPackLecDoa.updateAllPacksWithCoinsForSelLang(
                    BADGE.GET,
                    1,
                    usrLang.sel
                )
            } else if (langItem.subscribed.value != null && langItem.subscribed.value == false) {
                logcat { "langItem subscribed false" }
                if (subItem != null && subItem.subscribed.value == false) {
                    uiPackLecDoa.updateAllPacksWithCoinsForSelLang(
                        BADGE.COIN,
                        0,
                        usrLang.sel
                    )
                }
            }
        }

        if (subItem != null && subItem.subscribed.value == false) {
            logcat { "removing all added sub pack for ${usrLang.sel}" }
            logcat { "lang to remove ${langToRemove} size: ${langToRemove.size}" }
            packsDao.removeAllSubPacks(langToRemove)
        }


        //get all coins based packs with sel lang
        //if sel lang is subcribed
        //mark all coin pack with sub
    }

    suspend fun getJsonPack(): List<JsonPack>? {
        if (jsonPackList == null)
            jsonPackList = jsonPackDao.getAllPacks()
        return jsonPackList
    }

    suspend fun updateOrInsertPacks() {
        val lang =
            userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        logcat { "UPDATEORINSERTPACKS!!! " }
        val alreadyAddedPacks = packsDao.getAllAddedPacks()
        val userPacks = packsDao.getAllPacksType(lang.sel, USER_VOCAB)
        val userLections = packsDao.getAllLecType(lang.sel, USER_VOCAB)
        logcat { "userPacks ${userPacks.toString()}" }
        logcat { "userLections ${userLections.toString()}" }
        //val packs = lengoDataSource.getAllPacks()
        val jsonPacks = getJsonPack()
        // logcat { "packs prebuild size ${jsonPacks?.prebuild?.metadata?.size}" }

        val packUiEntities = jsonPacks?.toUIPackWithLectionsEntity2(
            lang.own,
            lang.sel,
            alreadyAddedPacks
        )

        if (!packUiEntities?.first.isNullOrEmpty()) {
            logcat { "packUiEntities?.first ${packUiEntities?.first?.size}" }
            uiPackLecDoa.insertPackUIList(packUiEntities!!.first)
        }
        if (!packUiEntities?.second.isNullOrEmpty()) {
            logcat { "packUiEntities?.second ${packUiEntities?.second?.size}" }
            uiPackLecDoa.insertLectionUIList(packUiEntities!!.second)
        }
        if (!userPacks.isNullOrEmpty()) {
            val userPaks = userPacks.map { packE ->
                packE.toPack(lang.own, lang.sel, emptyList())
            }
            logcat { "userPaks ${userPaks}" }
            logcat { "userPaks ${userPaks.size}" }
            uiPackLecDoa.insertPackUIList(userPaks.toUIPackListEntity())
        }
        if (!userLections.isNullOrEmpty()) {
            val userLec = userLections.map { lecE ->
                lecE.toLection(lang.own, lang.sel)
            }
            logcat { "userLec ${userLec}" }
            logcat { "userLec ${userLec.size}" }
            uiPackLecDoa.insertLectionUIList(userLec.toLectionUIList())
        }

        val suggestedPacks = try {
            apiService.getPackSuggestion(PackSuggestionRequest(lang.own, lang.sel))
        } catch (ex: Exception) {
            null
        }
        if (suggestedPacks != null) {
            val suggestedPackUiEntities = suggestedPacks?.metadata?.toUIPackWithLectionsEntity3(
                lang.own,
                lang.sel,
                alreadyAddedPacks
            )
            if (!suggestedPackUiEntities?.first.isNullOrEmpty()) {
                logcat { "packUiEntities?.first ${suggestedPackUiEntities?.first?.size}" }
                uiPackLecDoa.insertPackUIList(suggestedPackUiEntities!!.first)
            }
            if (!suggestedPackUiEntities?.second.isNullOrEmpty()) {
                logcat { "packUiEntities?.second ${suggestedPackUiEntities?.second?.size}" }
                uiPackLecDoa.insertLectionUIList(suggestedPackUiEntities!!.second)
            }
        }
    }


    suspend fun addOrUpdatePackToDatabase(params: Pack) {
        withContext(ioDispatcher) {
            logcat { "addOrUpdatePackToDatabase" }
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = DEFAULT_OWN_LANG
            )
            val existingPackVersionAndKey = packsDao.packVersion(
                params.pck,
                params.owner,
                params.type,
                params.lang
            )
            if (params.type == USER_VOCAB_SUGGESTED) {

                if (!isPackAlreadyAdded(params) || existingPackVersionAndKey == null ||
                    params.version > existingPackVersionAndKey.version
                ) {

                    uiPackLecDoa.updateUIPackBadge(
                        BADGE.LOADING,
                        params.pck,
                        params.owner,
                        params.type,
                        params.lang
                    )

                    val response = apiService.getDownloadSuggestion(
                        ObjSuggestionDownloadRequest(
                            listOf(
                                PackKey(
                                    func = params.type.replace("-SUGGESTED", ""),
                                    owner = params.owner,
                                    pck = params.pck
                                )
                            )
                        )
                    )

                    val listOfObjecEntity = mutableListOf<ObjectEntity>()
                    val listOfLectEntity = mutableListOf<LectionsEntity>()
                    val packEntity = params.toPackEntity()
                    val existingObj = packsDao.getAllObjData()


                    params.lections.forEach { lec ->
                        val objects = response?.objects?.filter {
                            (it.value != null && it.value!!.containsKey(selLang.sel)
                                    && it.lec == lec.lec && it.pck == params.pck
                                    && it.owner == params.owner && it.func == params.type)
                        } ?: emptyList()

                        fromListPackSuggestionResponseToObjectEntity(
                            response?.objects,
                            existingObj,
                            selLang.sel,
                            selLang.own,
                            selLang.sel
                        )?.let {
                            listOfObjecEntity.addAll(it)
                        }


                        if (selLang.sel == "us" && objects.isEmpty()) {
                            val obj = response?.objects?.filter {
                                (it.value != null && it.value!!.containsKey("en")
                                        && it.lec == lec.lec && it.pck == params.pck
                                        && it.owner == params.owner && it.func == params.type)
                            } ?: emptyList()

                            fromListPackSuggestionResponseToObjectEntity(
                                response?.objects,
                                existingObj,
                                DEFAULT_OWN_LANG,
                                selLang.own,
                                selLang.sel
                            )?.let {
                                listOfObjecEntity.addAll(it)
                            }
                        }

                        listOfLectEntity.add(lec.toLectionEntity())
                    }

                    transactionRunner {
                        packsDao.insertPack(packEntity)
                        packsDao.insertLecttions(listOfLectEntity)
                        packsDao.insertObject(listOfObjecEntity)
                    }

                    logcat { "${response?.objects?.size}" }

                }

            } else if (!isPackAlreadyAdded(params) || existingPackVersionAndKey == null ||
                params.version > existingPackVersionAndKey.version || isPacksAllObjectNotLoaded(
                    params
                )
            ) {

                uiPackLecDoa.updateUIPackBadge(
                    BADGE.LOADING,
                    params.pck,
                    params.owner,
                    params.type,
                    params.lang
                )

                //val packs = lengoDataSource.getAllPacks()
                val jsonObj = jsonPackDao.getObjOfPack(params.pck, params.type, params.owner)
                logcat { "jsonObj SIZE ${jsonObj?.size}" }
                val listOfObjecEntity = mutableListOf<ObjectEntity>()
                val listOfLectEntity = mutableListOf<LectionsEntity>()
                val packEntity = params.toPackEntity()
                val existingObj = packsDao.getAllObjData()

                params.lections.forEach { lec ->
                    val objects = jsonObj?.filter {
                        (it.value != null && it.value!!.containsKey(selLang.sel)
                                && it.lec == lec.lec && it.pck == params.pck
                                && it.owner == params.owner && it.func == params.type)
                    } ?: emptyList()

                    listOfObjecEntity.addAll(
                        objects.toListOfObject2(
                            existingObj,
                            selLang.sel,
                            selLang.own,
                            selLang.sel
                        )
                    )


                    if (selLang.sel == "us" && objects.isEmpty()) {
                        val obj = jsonObj?.filter {
                            (it.value != null && it.value!!.containsKey("en")
                                    && it.lec == lec.lec && it.pck == params.pck
                                    && it.owner == params.owner && it.func == params.type)
                        } ?: emptyList()
                        listOfObjecEntity.addAll(
                            obj.toListOfObject2(
                                existingObj,
                                DEFAULT_OWN_LANG,
                                selLang.own,
                                selLang.sel
                            )
                        )
                    }

                    listOfLectEntity.add(lec.toLectionEntity())
                }

                transactionRunner {
                    packsDao.insertPack(packEntity)
                    packsDao.insertLecttions(listOfLectEntity)
                    packsDao.insertObject(listOfObjecEntity)
                }

            } else {
                val currentDate = System.currentTimeMillis()

                if (isEmptyExplanationOrExampleFound(params)) {
                    val lections = updatedLections(params)
                    if (!lections.isNullOrEmpty()) {
                        packsDao.insertLecttions(lections)
                    }
                }

                packsDao.updatePackTime(
                    currentDate,
                    params.pck,
                    params.owner,
                    params.type,
                    params.lang
                )
            }

            uiPackLecDoa.updateUIPackBadge(
                BADGE.OPEN,
                params.pck,
                params.owner,
                params.type,
                params.lang
            )

        }
    }

    private suspend fun isPacksAllObjectNotLoaded(params: Pack): Boolean {
        val existingObj =
            packsDao.packObjectsCount(params.pck, params.owner, params.type, params.lang)
        val jsonObj = jsonPackDao.getObjOfPack(params.pck, params.type, params.owner)
        return existingObj < (jsonObj?.size ?: 0)
    }


    sealed class PackOrLectionStatus {
        data class PackPurchaseComplete(val pack: Pack) : PackOrLectionStatus()
        data class PurchaseOpen(val pack: Pack) : PackOrLectionStatus()
        data class LectionOpen(val lec: Lection) : PackOrLectionStatus()
        data class PackLectionPurchaseComplete(val lec: Lection) : PackOrLectionStatus()
        data object InsufficientCoin : PackOrLectionStatus()
        data object Default : PackOrLectionStatus()
    }

    suspend fun processPack(pack: Pack): PackOrLectionStatus {
        return when (pack.badge) {
            BADGE.GET -> {
                addOrUpdatePackToDatabase(pack)
                PackOrLectionStatus.PackPurchaseComplete(pack)
            }

            BADGE.OPEN -> {
                addOrUpdatePackToDatabase(pack)
                PackOrLectionStatus.PurchaseOpen(pack)
            }

            BADGE.COIN -> {
                purchasePack(pack)
            }

            else -> {
                PackOrLectionStatus.Default
            }
        }
    }

    suspend fun processPackAndLection(pack: Pack, lec: Lection): PackOrLectionStatus {
        return when (pack.badge) {
            BADGE.GET -> {
                addOrUpdatePackToDatabase(pack)
                PackOrLectionStatus.PackLectionPurchaseComplete(lec)
            }

            BADGE.OPEN -> {
                addOrUpdatePackToDatabase(pack)
                PackOrLectionStatus.LectionOpen(lec)
            }

            BADGE.COIN -> {
                val result = purchasePack(pack)
                if (result is PackOrLectionStatus.PackPurchaseComplete) {
                    PackOrLectionStatus.PackLectionPurchaseComplete(lec)
                } else {
                    result
                }
            }

            else -> {
                PackOrLectionStatus.Default
            }
        }
    }

    fun observePacksForCategory(categoryName: String): Flow<List<Pack>> {
        return userRepository.observeUserEntitySelAndDevice.map { userLang ->
            getPacksAndLections(userLang.sel, categoryName)
        }.catch { emit(emptyList()) }.flowOn(ioDispatcher)
    }

    suspend fun getPacksAndLections(selLang: String, categoryName: String): List<Pack> {
        val packMap = uiPackLecDoa.getPacksAndLections(categoryName, selLang) ?: emptyMap()
        val packs = mutableListOf<Pack>()
        packMap.forEach {
            val pack = it.key.toPack(it.value.toLectionList())
            packs.add(pack)
        }
        return packs
    }


    suspend fun purchasePack(pack: Pack): PackOrLectionStatus {
        return withContext(ioDispatcher) {
            val coins = userDoa.totalCoins() ?: 0
            if (coins >= pack.coins) {
                userDoa.removeCoins(pack.coins)
                addOrUpdatePackToDatabase(pack)
                return@withContext PackOrLectionStatus.PackPurchaseComplete(pack)
            } else {
                return@withContext PackOrLectionStatus.InsufficientCoin
            }
        }
    }


    suspend fun isPackAlreadyAdded(pack: Pack): Boolean {
        val count = packsDao.packCount(
            pack.pck,
            pack.owner,
            pack.type,
            pack.lang
        )
        return count > 0
    }

    suspend fun isEmptyExplanationOrExampleFound(pack: Pack): Boolean {
        val totalEmptyLec = packsDao.getEmptyExampleOrExplantionLections(
            pack.pck,
            pack.owner,
            pack.type,
            pack.lang
        )
        return totalEmptyLec > 0
    }

    fun updatedLections(pack: Pack): List<LectionsEntity> {
        return pack.lections.map { lection ->
            LectionsEntity(
                lec = lection.lec,
                type = lection.type,
                pck = lection.pck,
                owner = lection.owner,
                title = lection.nameMap,
                lng = lection.lang,
                examples = lection.example,
                explanation = lection.explanation
            )
        }
    }


    suspend fun userEditedPacks(): List<UserEditedPack?> {
        val selLang =
            userDoa.getSyncUserLang() ?: UserDoa.UserLang(DEFAULT_SEL_LANG, own = DEFAULT_OWN_LANG)
        val editedPacks = packsDao.getAllPacksExcludeType(selLang.sel, USER_VOCAB)

        return editedPacks?.map { pack ->
            val lectionsEnities = packsDao.getAllLections(pack.pck, pack.owner, pack.type, pack.lng)
            if (lectionsEnities.isEmpty()) {
                return@map null
            }
            val lections = lectionsEnities.toListOfLection(selLang.own, selLang.sel)
            val editPac = pack.toPack(selLang.own, selLang.sel, lections)
            var totalObject = 0
            var userObject = 0
            lections.forEach { lection ->

                logcat("USER EDITED PACKS") { "${lection.lec}, ${lection.pck}" }

                val objects = packsDao.getObjects(
                    lection.pck,
                    lection.lec,
                    lection.type.replace("-SUGGESTED", ""),
                    lection.owner,
                    lection.lang
                ) ?: emptyList()

                logcat("USER EDITED PACKS") { "objects: ${objects.size}" }

                totalObject += objects.size
                userObject += objects.filter { it.iVal > -1 }.size
            }
            UserEditedPack(editPac, totalCount = totalObject, userCount = userObject)
        }?.reversed() ?: emptyList()
    }


    fun PacksEntity.toPack(deviceLngCode: String, selLang: String, lections: List<Lection>): Pack {
        val packNameName = getPackName(this.title, selLang, deviceLngCode)
        return Pack(
            pck = this.pck,
            owner = this.owner,
            title = packNameName ?: "",
            packNameMap = this.title,
            type = this.type,
            coins = this.coins,
            emoji = this.emoji ?: "",
            lang = this.lng,
            lections = lections,
            badge = BADGE.OPEN,
            version = this.version,
            submitted = this.submitted,
            subscribed = this.subscribed
        )
    }


    fun PacksUIEntity.toPack(lections: List<Lection>): Pack {
        return Pack(
            pck,
            owner,
            pack_title,
            packNameMap,
            type,
            coins.toInt(),
            emoji,
            lang,
            version.toInt(),
            subscribed == 1L,
            false,
            lections,
            badge
        )
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
            subscribed = if (subscribed) 1L else 0L,
        )
    }

    fun List<JsonPack>?.toUIPackWithLectionsEntity2(
        deviceLngCode: String,
        selectedLngCode: String,
        addedPacks: List<PacksDao.PackId>
    ): Pair<List<PacksUIEntity>, List<LectionUIEntity>> {

        val finalPacks = mutableListOf<PacksUIEntity>()
        val finalLections = mutableListOf<LectionUIEntity>()

        this?.fastForEach { pack ->

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
                    type = pack.func,
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
                    type = pack.func,
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

    fun List<Pack>.toUIPackListEntity(): List<PacksUIEntity> {
        return this.map { it.toUIPackEntity() }
    }

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

    fun List<LectionUIEntity>.toLectionList(): List<Lection> {
        return this.map { it.toLection() }
    }

    fun LectionUIEntity.toLection(): Lection {
        return Lection(
            type,
            pck,
            owner,
            lec,
            lec_title,
            lec_nameMap,
            lang,
            lec_image
        )
    }


}