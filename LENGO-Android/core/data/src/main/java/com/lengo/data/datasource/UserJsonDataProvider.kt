package com.lengo.data.datasource

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.USER_VOCAB
import com.lengo.data.mapper.toLectionEntity
import com.lengo.data.mapper.toObjectEntity
import com.lengo.data.mapper.toPacksEntity
import com.lengo.database.appdatabase.doa.DateStatsDoa
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.DateStatsEntity
import com.lengo.database.appdatabase.model.LectionsEntity
import com.lengo.database.appdatabase.model.PacksEntity
import com.lengo.database.appdatabase.model.UserEntity
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import com.lengo.model.data.SettingModel
import com.lengo.network.model.LoginResponse
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserJsonDataProvider @Inject constructor(
    private val userDoa: UserDoa,
    private val dateStatsDoa: DateStatsDoa,
    private val packsDao: PacksDao,
    private val lengoPreference: LengoPreference,
    @Dispatcher(LengoDispatchers.IO) val dispatchers: CoroutineDispatcher,
    private val jsonPackDao: JsonPackDao,
    @ApplicationContext val context: Context,
) {
    suspend fun preparePushUserData(userId: Long): JsonObject? {
        val root = JsonObject()

        //MetaData
        val metadataArray = JsonArray()
        val packs = packsDao.getAllPushedUserPacks()
        val packMap = mutableMapOf<PacksEntity,List<LectionsEntity>>()
        if(packs.isNullOrEmpty()) {
            val lectionsToPushed = packsDao.getAllPushedUserLections()
            lectionsToPushed?.forEach { lections ->
                val packEnt = packsDao.packsForLection(type = lections.type,
                    pck = lections.pck, owner = lections.owner, lang = lections.lng)
                if(packEnt != null) {
                    if (packMap.contains(packEnt)) {
                        val lectionList = packMap.get(packEnt)?.toMutableList()
                        lectionList?.add(lections)
                        lectionList?.let { lec -> packMap.put(packEnt, lec.toList()) }
                    } else {
                        val lectionList = mutableListOf<LectionsEntity>()
                        lectionList.add(lections)
                        packMap.put(packEnt, lectionList.toList())
                    }
                }
            }

            packMap.forEach { pack, lectionsEntities ->
                val metadataobj = JsonObject()
                val available_sel_lng = JsonArray()
                available_sel_lng.add(pack.lng)
                val metadataNameobj = JsonObject()
                pack.title.forEach { (s, s2) ->
                    metadataNameobj.addProperty(s,s2)
                }
                metadataobj.add("name",metadataNameobj)
                metadataobj.add("available_sel_lng",available_sel_lng)
                metadataobj.addProperty("coins",pack.coins)
                metadataobj.addProperty("emoji",pack.emoji)
                metadataobj.addProperty("func",pack.type)
                metadataobj.addProperty("id",pack.pck)
                metadataobj.addProperty("owner",pack.owner)
                val lectionsArray = JsonArray()
                lectionsEntities.forEach { lec ->
                    val lecObj = JsonObject()
                    lecObj.addProperty("id",lec.lec)
                    val lecNameObj = JsonObject()
                    lec.title.forEach { s, s2 ->
                        lecNameObj.addProperty(s,s2)
                    }
                    lecObj.add("name",lecNameObj)
                    lectionsArray.add(lecObj)
                }
                if(!lectionsArray.isEmpty) {
                    metadataobj.add("lections", lectionsArray)
                }
                metadataArray.add(metadataobj)
            }
            if(metadataArray.size() > 0)
             root.add("metadata",metadataArray)

        } else {
            packs.let {  userPacks ->
                userPacks.forEach { pack ->
                    val metadataobj = JsonObject()
                    val available_sel_lng = JsonArray()
                    available_sel_lng.add(pack.lng)
                    val metadataNameobj = JsonObject()
                    pack.title.forEach { (s, s2) ->
                        metadataNameobj.addProperty(s,s2)
                    }
                    metadataobj.add("name",metadataNameobj)
                    metadataobj.add("available_sel_lng",available_sel_lng)
                    metadataobj.addProperty("coins",pack.coins)
                    metadataobj.addProperty("emoji",pack.emoji)
                    metadataobj.addProperty("func",pack.type)
                    metadataobj.addProperty("id",pack.pck)
                    metadataobj.addProperty("owner",pack.owner)
                    val userLections = packsDao.getAllPushedUserLectionsForPack(pack.pck,pack.owner,pack.lng,false, USER_VOCAB)
                    val lectionsArray = JsonArray()
                    userLections?.forEach { lec ->
                        val lecObj = JsonObject()
                        lecObj.addProperty("id",lec.lec)
                        val lecNameObj = JsonObject()
                        lec.title.forEach { s, s2 ->
                            lecNameObj.addProperty(s,s2)
                        }
                        lecObj.add("name",lecNameObj)
                        lectionsArray.add(lecObj)
                    }
                    if(!lectionsArray.isEmpty) {
                        metadataobj.add("lections", lectionsArray)
                    }
                    metadataArray.add(metadataobj)
                }
                if(metadataArray.size() > 0)
                    root.add("metadata",metadataArray)
            }

        }

        //Objects
        val objecData = packsDao.getAllPushedUserObjData()
        val objecDataArray = JsonArray()
        objecData?.forEach { objEntity ->
            val objecDataObj = JsonObject()
            val valueObj = JsonObject()

            objEntity.own?.forEach { s, strings ->
              val ownObj = JsonObject()
              val ownObjASArray = JsonArray()
              strings.forEach { value ->
               ownObjASArray.add(value)
              }
              ownObj.add("as",ownObjASArray)
              valueObj.add(s,ownObj)
            }

            val selObj = JsonObject()
            val selObjASArray = JsonArray()
            objEntity.sel?.forEach { s ->
                selObjASArray.add(s)
            }
            selObj.add("as",selObjASArray)
            valueObj.add(objEntity.lng,selObj)

            objecDataObj.add("value",valueObj)
            objecDataObj.addProperty("func",objEntity.type)
            objecDataObj.addProperty("lec",objEntity.lec)
            objecDataObj.addProperty("obj",objEntity.obj)
            objecDataObj.addProperty("owner",objEntity.owner)
            objecDataObj.addProperty("pck",objEntity.pck)

            objecDataArray.add(objecDataObj)
        }
        if(objecDataArray.size() > 0) {
            root.add("objects", objecDataArray)
        }
        //Objects

        if(metadataArray.isEmpty && objecDataArray.isEmpty) {
            return null
        } else {
            root.addProperty("userid", userId)
        }
        return root
    }

    suspend fun fetchPushedData(userId: Long): JsonObject? {
        val root = JsonObject()
        var userData: UserEntity? = null
        userDoa.getAllPushedData()?.let { uData ->
            root.addProperty("coins", uData.coins)
            root.addProperty("highscore", uData.highscore)
            root.addProperty("own_lng", uData.own)
            root.addProperty("points", uData.points)
            root.addProperty("region_code", "US")
            root.addProperty("sel_lng", uData.sel)
            userData = uData
        }
        val userdata = JsonObject()

        //date_stats//
        val date_statsArray = JsonArray()
            val dateStats = dateStatsDoa.getAllPushedDateData()
            dateStats.forEach { ds ->
                val datestats = JsonObject()
                datestats.addProperty("date", ds.date)
                datestats.addProperty("edited_gram", ds.edited_gram)
                datestats.addProperty("edited_vocab", ds.edited_vocab)
                datestats.addProperty("right_edited_gram", ds.right_edited_gram)
                datestats.addProperty("right_edited_vocab", ds.right_edited_vocab)
                datestats.addProperty("seconds", ds.seconds)
                date_statsArray.add(datestats)
        }
        if (!date_statsArray.isEmpty) {
            userdata.add("date_stats", date_statsArray)
        }
        //date_stats//

        //int_values//
        val int_valuesArray = JsonArray()
        val int_values = packsDao.getAllPushedObjData()
        int_values?.forEach { ds ->
            val intvalues = JsonObject()
            intvalues.addProperty("func", ds.type)
            intvalues.addProperty("intvalue", ds.iVal)
            intvalues.addProperty("lastretrieval", ds.last_retrieval)
            intvalues.addProperty("lec", ds.lec)
            intvalues.addProperty("lng", ds.lng)
            intvalues.addProperty("obj", ds.obj)
            intvalues.addProperty("owner", ds.owner)
            intvalues.addProperty("pck", ds.pck)
            int_valuesArray.add(intvalues)
        }
        if (!int_valuesArray.isEmpty) {
            userdata.add("int_values", int_valuesArray)
        }
        //int_values//

        //loaded_packs//
        val loaded_packsArray = JsonArray()
        val loaded_packs = packsDao.getAllPushedPacks()
        loaded_packs?.forEach { packs ->
            val loadedpacks = JsonObject()
            loadedpacks.addProperty("func", packs.type)
            loadedpacks.addProperty("lastretrieval", packs.last_retrieval)
            loadedpacks.addProperty("lng", packs.lng)
            loadedpacks.addProperty("owner", packs.owner)
            loadedpacks.addProperty("pck", packs.pck)
            loadedpacks.addProperty("subscribed", false)
            loaded_packsArray.add(loadedpacks)
        }
        if (!loaded_packsArray.isEmpty) {
            userdata.add("loaded_packs", loaded_packsArray)
        }
        //loaded_packs//

        //Setting//
        val settingObj = JsonObject()
        val settingModel = lengoPreference.syncSettingModel()
        if(!settingModel.isSync) {
            settingObj.addProperty("audio", settingModel.audioEnable)
            settingObj.addProperty("listening", settingModel.listeningTask)
            settingObj.addProperty("memorize", settingModel.memorizeTask)
            settingObj.addProperty("quiz", settingModel.quizTask)
            settingObj.addProperty("speaking", settingModel.speakingTask)
            settingObj.addProperty("voice", settingModel.pronounceEnable)
            settingObj.addProperty("userid", userData?.userid)
            //Setting//
            userdata.add("settings", settingObj)
        }
        if(userdata.size() > 0) {
            root.add("userdata", userdata)
        }
        if(root.size() > 0) {
            root.addProperty("userid", userId)
            return root;
        }
        return null
    }



    suspend fun provideData(email: String, name: String, password: String): JsonObject {
        val root = JsonObject()
        val userData = userDoa.getAllData()
        root.addProperty("coins", userData.coins)
        root.addProperty("email", email)
        root.addProperty("highscore", userData.highscore)
        root.addProperty("name", name)
        root.addProperty("own_lng", userData.own)
        root.addProperty("password", password)
        root.addProperty("points", userData.points)
        root.addProperty("region_code", "US")
        root.addProperty("sel_lng", userData.sel)
        val userdata = JsonObject()

        //date_stats//
        val date_statsArray = JsonArray()
        val dateStats = dateStatsDoa.getAllData()
        dateStats.forEach { ds ->
            val datestats = JsonObject()
            datestats.addProperty("date", ds.date)
            datestats.addProperty("edited_gram", ds.edited_gram)
            datestats.addProperty("edited_vocab", ds.edited_vocab)
            datestats.addProperty("lng", ds.lng)
            datestats.addProperty("right_edited_gram", ds.right_edited_gram)
            datestats.addProperty("right_edited_vocab", ds.right_edited_vocab)
            datestats.addProperty("seconds", ds.seconds)
            date_statsArray.add(datestats)
        }
        userdata.add("date_stats", if (date_statsArray.isEmpty) null else date_statsArray)
        //date_stats//

        //int_values//
        val int_valuesArray = JsonArray()
        val int_values = packsDao.getAllObjData()
        int_values.forEach { ds ->
            val intvalues = JsonObject()
            intvalues.addProperty("func", ds.type)
            intvalues.addProperty("intvalue", ds.iVal)
            intvalues.addProperty("lastretrieval", ds.last_retrieval)
            intvalues.addProperty("lec", ds.lec)
            intvalues.addProperty("lng", ds.lng)
            intvalues.addProperty("obj", ds.obj)
            intvalues.addProperty("owner", ds.owner)
            intvalues.addProperty("pck", ds.pck)
            int_valuesArray.add(intvalues)
        }
        userdata.add("int_values", if (int_valuesArray.isEmpty) null else int_valuesArray)
        //int_values//

        //loaded_packs//
        val loaded_packsArray = JsonArray()
        val loaded_packs = packsDao.getAllPacks()
        loaded_packs.forEach { packs ->
            val loadedpacks = JsonObject()
            loadedpacks.addProperty("func", packs.type)
            loadedpacks.addProperty("lastretrieval", packs.last_retrieval)
            loadedpacks.addProperty("lng", packs.lng)
            loadedpacks.addProperty("owner", packs.owner)
            loadedpacks.addProperty("pck", packs.pck)
            loaded_packsArray.add(loadedpacks)
        }
        userdata.add("loaded_packs", if (loaded_packsArray.isEmpty) null else loaded_packsArray)
        //loaded_packs//

        //Setting//
        val settingObj = JsonObject()
        val settingModel = lengoPreference.syncSettingModel()
        settingObj.addProperty("audio", settingModel.audioEnable)
        settingObj.addProperty("listening", settingModel.listeningTask)
        settingObj.addProperty("memorize", settingModel.memorizeTask)
        settingObj.addProperty("quiz", settingModel.quizTask)
        settingObj.addProperty("speaking", settingModel.speakingTask)
        settingObj.addProperty("voice", settingModel.pronounceEnable)
        settingObj.addProperty("userid", userData.userid)
        //Setting//
        userdata.add("settings", settingObj)

        root.add("userdata", userdata)
        return root
    }
    suspend fun updateUserData(userId: Int,name: String,email: String,password: String,) {
        withContext(dispatchers) {
            var dbUser = userDoa.currentUser()
            if (dbUser == null) {
                userDoa.insertUser(UserEntity(userid = -1, own = DEFAULT_OWN_LANG, name = name, email = email, password = password, pushed = false))
                dbUser = userDoa.currentUser()
            }
            dbUser?.let { currentUser ->
                currentUser.userid = userId.toLong() ?: -1L
                currentUser.name = name
                currentUser.email = email
                currentUser.password = password
                currentUser.pushed = true
                userDoa.insertUser(dbUser)
            }
        }
    }

    fun getPackKeys(loginResponse: LoginResponse): JsonObject {
        val jsonObject = JsonObject()
        val packArray = JsonArray()
        loginResponse.userdata?.loaded_packs?.forEach { pack ->
            val item = JsonObject()
            pack.func?.let { func ->
                item.addProperty("func", func)
            }
            pack.owner?.let { owner ->
                item.addProperty("owner", owner)
            }
            pack.pck?.let { pck ->
                item.addProperty("pck", pck)
            }
            packArray.add(item)
        }
        jsonObject.add("pack_keys",packArray)
        return jsonObject
    }

    suspend fun setUserLoginData(loginResponse: LoginResponse, existingPass: String?,isLoginRes: Boolean) {
        withContext(dispatchers) {
            var dbUser = userDoa.currentUser()
            if (dbUser == null) {
                userDoa.insertUser(UserEntity(userid = -1, own = DEFAULT_OWN_LANG, pushed = false))
                dbUser = userDoa.currentUser()
            }
            dbUser?.let { currentUser ->
                currentUser.userid = loginResponse.userid?.toLong() ?: -1L
                loginResponse.name?.let { name ->
                    currentUser.name = name
                }
                if(!isLoginRes) {
                    loginResponse.activity_id?.let { activity_id ->
                        currentUser.activity_id = activity_id
                    }
                }
                loginResponse.email?.let { email ->
                    currentUser.email = email
                }
                loginResponse.coins?.let { coins ->
                    currentUser.coins = coins
                }
                loginResponse.points?.let { points ->
                    currentUser.points = points.toLong()
                }
                loginResponse.highscore?.let { highscore ->
                    currentUser.highscore = highscore.toLong()
                }
                loginResponse.own_lng?.let { ownLng ->
                    currentUser.own = ownLng
                }
                loginResponse.sel_lng?.let { selLng ->
                    currentUser.sel = selLng
                }
                if(existingPass != null) {
                    currentUser.password = existingPass
                }
                currentUser.pushed = false
                userDoa.insertUser(currentUser)
            }
        }
    }

    suspend fun updateSetting(loginResponse: LoginResponse) {
        withContext(dispatchers) {
            val existingSettingModel = lengoPreference.syncSettingModel()
            loginResponse.userdata?.settings?.let { setting ->
                val settingMod = SettingModel(
                    memorizeTask = setting.memorize ?: existingSettingModel.memorizeTask,
                    quizTask = setting.quiz ?: existingSettingModel.quizTask,
                    listeningTask = setting.listening ?: existingSettingModel.listeningTask,
                    speakingTask = setting.speaking ?: existingSettingModel.speakingTask,
                    testTask = setting.speaking ?: existingSettingModel.testTask,
                    audioEnable = setting.audio ?: existingSettingModel.audioEnable,
                    pronounceEnable = setting.voice ?: existingSettingModel.pronounceEnable,
                    darkThemeEnable = existingSettingModel.darkThemeEnable,
                    isUnlockCardVisible = existingSettingModel.isUnlockCardVisible,
                    isSync = false
                )
                lengoPreference.updateSetting(settingMod)
            }

        }
    }

    //Get Pack id already added
    //Get Packs and Lection for UI form UIdatabse based on alreadyAddedPackId
    //Insert Updated Pack and lection to UI Database
    //Insert Data to pack lec and obj
    suspend fun updateData(loginResponse: LoginResponse,onComplete: () -> Unit) {
        withContext(dispatchers) {
            val devicelng = DEFAULT_OWN_LANG
            packsDao.removeAllData()
            var packsEntities: List<PacksEntity> = emptyList()
            val listOfLectEntity = mutableListOf<LectionsEntity>()

            loginResponse.userdata?.loaded_packs?.let { packs ->
                packsEntities = packs.filter { it.func == SYS_GRAMMER || it.func == SYS_VOCAB }.mapNotNull { pak ->
                        val jsonPack = jsonPackDao.getPacks(
                            pak.pck?.toLong() ?: -1,
                            pak.func ?: "",
                            pak.owner?.toLong() ?: -1
                        )
                        val lections = jsonPack!!.toLectionEntity(pak.lng ?: "")
                        lections.let { lecs -> listOfLectEntity.addAll(lecs) }
                        jsonPack.toPacksEntity(pak.lng ?: "")
                    }
                }

            packsDao.insertPacks(packsEntities)
            packsDao.insertLecttions(listOfLectEntity)

            val objList = loginResponse.userdata?.int_values?.filter { it.func == SYS_GRAMMER || it.func == SYS_VOCAB }?.mapNotNull { obj ->
                val jsonObj = jsonPackDao.getObj(obj.obj?.toLong()?: -1,obj.lec?.toLong()?: -1,obj.pck?.toLong()?: -1,obj.func ?: "",obj.owner?.toLong()?: -1)
                jsonObj?.toObjectEntity(obj.lng ?: "",devicelng,obj.intvalue ?: -1)
            }
            objList?.let { packsDao.insertObject(it) }
            onComplete()
        }
    }

    suspend fun setDateState(loginResponse: LoginResponse) {
        withContext(dispatchers) {
            packsDao.removeAllDateStats()
            val dateStatelist = loginResponse.userdata?.date_stats?.map { data ->
                DateStatsEntity(
                    date = data.date ?: "",
                    edited_gram = data.edited_gram?.toLong() ?: 0L,
                    edited_vocab = data.edited_vocab?.toLong() ?: 0L,
                    lng = data.lng ?: "",
                    right_edited_gram = data.right_edited_gram?.toLong() ?: 0L,
                    right_edited_vocab = data.right_edited_vocab?.toLong() ?: 0L,
                    seconds = data.seconds?.toLong() ?: 0L,
                    pushed = false
                )
            }
            dateStatelist?.let { dateStatsDoa.insertDateState(it) }
        }
    }
}