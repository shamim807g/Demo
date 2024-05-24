package com.lengo.network.model

data class TTSVoicesRequest(val accent: String)

data class TTSVoicesResponse(val voices: List<Voices>?)
data class Voices(
    val languageCodes: List<String>,
    val name: String,
    val key: String,
    val displayName: String,
    val naturalSampleRateHertz: Int,
    val ssmlGender: String,
    val natural: Boolean,
    val pro: Boolean
)


data class TTSRequest(val text: String, val voice: Voice)
data class Voice(val languageCode: String, val voicename: String)

data class TTSResponse(val url: String? = null)