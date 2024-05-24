package com.lengo.data.preload

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointsStepsModel(
    val error: Any?,
    val msg: String?,
    val structure: Structure
) {
    @JsonClass(generateAdapter = true)
    data class Structure(
        val points_steps_list: List<Step>
    ) {
        @JsonClass(generateAdapter = true)
        data class Step(
            val id: Int,
            val lvl: Int,
            val points: Int
        )
    }

}