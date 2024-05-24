package com.lengo.network.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class UserRankingListRequest(
    @Json(name = "bottom_limit") val bottomLimit: Int,
    @Json(name = "points") val points: Int,
    @Json(name = "sel") val sel: String,
    @Json(name = "top_limit") val topLimit: Int,
    @Json(name = "userid") val userId: Int
)



data class RankingListRequest(
    @Json(name = "limit") val limit: Int,
    @Json(name = "sel") val sel: String
)

data class RankingTableResponse(
    @Json(name = "error") val error: Any?,
    @Json(name = "msg") val msg: String,
    @Json(name = "ranking_table") val rankingTable: List<RankingTableItem>?
)
@JsonClass(generateAdapter = true)
data class RankingTableItem(
    @Json(name = "name") val name: String?,
    @Json(name = "own") val own: String?,
    @Json(name = "points") val points: Int?,
    @Json(name = "pro") val pro: Boolean?,
    @Json(name = "rank") val rank: Int?,
    @Json(name = "region_code") val regionCode: String?,
    @Json(name = "sel") val sel: String?,
    @Json(name = "userid") val userid: Int?
)