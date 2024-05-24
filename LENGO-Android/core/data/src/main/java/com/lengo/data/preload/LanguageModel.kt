package com.lengo.data.preload

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LanguageModel(
    val error: Any?,
    val msg: String?,
    val structure: Structure
) {
    @JsonClass(generateAdapter = true)
    data class Structure(
        val languages: List<Language>
    ) {
        @JsonClass(generateAdapter = true)
        data class Language(
            val accent: String,
            val firstcolor: String,
            val ios_appid: String,
            val ios_bundleid: String,
            val iso639: String,
            val iso639_3: String,
            val secondcolor: String,
            val tkn: String
        )
    }
}

@JsonClass(generateAdapter = true)
data class VersionModel(
    val structure: Structure
) {
    @JsonClass(generateAdapter = true)
    data class Structure(
        val version: String
    )
}

