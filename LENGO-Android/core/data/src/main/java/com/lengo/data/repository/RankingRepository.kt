package com.lengo.data.repository

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.R
import com.lengo.common.getDrawableForLangCode
import com.lengo.data.datasource.LengoDataSource
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.TransactionRunnerDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.jsonDatabase.doa.JsonPackDao
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import com.lengo.model.data.Ranking
import com.lengo.model.data.VoiceItem
import com.lengo.network.ApiService
import com.lengo.network.model.request.RankingListRequest
import com.lengo.network.model.request.RankingTableResponse
import com.lengo.network.model.request.UserRankingListRequest
import com.lengo.preferences.LengoPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val userDoa: UserDoa,
    private val lengoDataSource: LengoDataSource,
    private val apiService: ApiService,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
) {
    var userRankingSmallList = MutableStateFlow<List<Ranking>>(emptyList())
    val userRankingFullList = MutableStateFlow<List<Ranking>>(emptyList())
    var topRankingSmallList = MutableStateFlow<List<Ranking>>(emptyList())
    val topRankingFullList = MutableStateFlow<List<Ranking>>(emptyList())
    var userItemAdded = false
    lateinit var request: UserRankingListRequest
    lateinit var topRequest: RankingListRequest

    suspend fun getUserRankingList(refreshData: Boolean = false, isFromLast: Boolean = false) {
        val currentUser = userDoa.currentUser() ?: return

        if(::request.isInitialized && request.points == currentUser.points.toInt() &&
            request.userId == currentUser.userid.toInt() &&
            request.sel == currentUser.sel) {
            return
        }

        val rankingList = mutableListOf<Ranking>()
//        if (refreshData) {
//            userRankingSmallList.value = emptyList()
//            userRankingFullList.value = emptyList()
//            userItemAdded = false
//        }
//        if (isFromLast && userRankingList.isNotEmpty()) {
//            request = UserRankingListRequest(
//                25,
//                points = userRankingList.last().points,
//                sel = userRankingList.last().sel,
//                topLimit = 25,
//                userId = userRankingList.last().userId
//            )
//        } else if (!isFromLast && userRankingList.isNotEmpty()) {
//            request = UserRankingListRequest(
//                25,
//                points = userRankingList.first().points,
//                sel = userRankingList.first().sel,
//                topLimit = 25,
//                userId = userRankingList.first().userId
//            )
//        } else {
//            request = UserRankingListRequest(
//                50,
//                points = currentUser.points.toInt(),
//                sel = currentUser.sel,
//                topLimit = 50,
//                currentUser.userid.toInt()
//            )
//        }

        request = UserRankingListRequest(
            25,
            points = currentUser.points.toInt(),
            sel = currentUser.sel,
            topLimit = 25,
            currentUser.userid.toInt()
        )

        val api = apiService.userRankingList(request)
        val tableArray = api.getAsJsonArray("ranking_table")
        if (tableArray != null) {
            val list = tableArray.sortedBy { it.asJsonObject.get("rank").asInt }
            list.forEach {
                val obj = it.asJsonObject
                val drawable =
                    if (obj.get("own").asString == "en" && obj.get("region_code").asString == "US")
                        getDrawableForLangCode("us") else getDrawableForLangCode(obj.get("own").asString!!)

                val rank = obj.get("rank").asInt
                val ranking = Ranking(
                    countryImage = drawable,
                    name = obj.get("name").asString!!,
                    level = "${getLevel(obj.get("points").asInt!!)}",
                    points = obj.get("points").asInt,
                    rank = rank,
                    isCurrentUser = false,
                    sel = obj.get("sel").asString!!,
                    userId = obj.get("userid").asInt!!,
                    isPro = obj.get("pro").asBoolean!!
                )
                rankingList.add(ranking)
            }
        }


        var lastIndex = rankingList.indexOfLast { it.points == currentUser.points.toInt() }
        if (lastIndex == -1) {
            lastIndex = rankingList.indexOfLast { it.points < currentUser.points.toInt() }
        }
        var lastRank = rankingList[lastIndex].rank

        val userItem = Ranking(
            countryImage = getDrawableForLangCode(currentUser.own),
            name = currentUser.name ?: "${
                ContextCompat.getContextForLanguage(context).getString(R.string.you)
            } (${
                ContextCompat.getContextForLanguage(context)
                    .getString(R.string.not_registered)
            })",
            level = "${getLevel(currentUser.points.toInt())}",
            points = currentUser.points.toInt(),
            rank = -1,
            isCurrentUser = true,
            sel = currentUser.sel,
            userId = currentUser.userid.toInt(),
            isPro = false
        )
        rankingList.add(lastIndex + 1, userItem)

        for(i in lastIndex + 1 .. rankingList.size - 1) {
            rankingList[i] = rankingList[i].copy(rank =  lastRank + 1)
            lastRank += 1
        }

        userRankingFullList.value = rankingList.toList()
        val userRankingList = mutableListOf<Ranking>()
        val userRankItem = rankingList.find { it.isCurrentUser }
        val index = rankingList.indexOf(userRankItem)
        val startIndex = index - 4
        for(n in startIndex..index) {
            if(rankingList.getOrNull(n) != null) {
                userRankingList.add(rankingList[n])
            }
        }
        userRankingSmallList.value = userRankingList.toList()
    }

    suspend fun getTopRankingList(refreshData: Boolean = false, isFromLast: Boolean = false) {
        val currentUser = userDoa.currentUser() ?: return

        if(::topRequest.isInitialized && topRequest.sel == currentUser.sel) {
            return
        }

        val rankingList = mutableListOf<Ranking>()

        topRequest = RankingListRequest(
            50,
            currentUser.sel
        )

        val api = apiService.rankingList(topRequest)
        val tableArray = api.getAsJsonArray("ranking_table")
        if (tableArray != null) {
            val list = tableArray.sortedBy { it.asJsonObject.get("rank").asInt }
            list.forEach {
                val obj = it.asJsonObject
                val drawable =
                    if (obj.get("own").asString == "en" && obj.get("region_code").asString == "US")
                        getDrawableForLangCode("us") else getDrawableForLangCode(obj.get("own").asString!!)

                val rank = obj.get("rank").asInt
                val ranking = Ranking(
                    countryImage = drawable,
                    name = obj.get("name").asString!!,
                    level = "${getLevel(obj.get("points").asInt!!)}",
                    points = obj.get("points").asInt,
                    rank = rank,
                    isCurrentUser = false,
                    sel = obj.get("sel").asString!!,
                    userId = obj.get("userid").asInt!!,
                    isPro = obj.get("pro").asBoolean!!
                )
                rankingList.add(ranking)
            }
        }


        topRankingFullList.value = rankingList.toList()
        topRankingSmallList.value = rankingList.take(5)
    }

    fun getLevel(points: Int): Int {
        var currentPoint = points
        val pointMap = lengoDataSource.getLevelPointMap()
        var currentLevel = 0
        lengoDataSource.getLevelPointsKeys().forEach {
            val step = pointMap[it]
            if(currentPoint < step!!) {
                currentLevel = it
                return currentLevel
            }
        }

        return currentLevel
    }
}