package com.lengo.data.preload

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AchievementsModel(
    val error: Any?,
    val msg: String?,
    val structure: Structure
) {
    @JsonClass(generateAdapter = true)
    data class Structure(
        val achievements: List<Achievements>,
        val points_steps_list: List<pointsStepsList>
    ) {
        @JsonClass(generateAdapter = true)
        data class Achievements(
            val id: String,
            val start_step: Int,
            val value: Int?
        )
        @JsonClass(generateAdapter = true)
        data class pointsStepsList(
            val id: Int,
            val lvl: Int,
            val points: Int
        )
    }

}