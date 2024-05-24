package com.lengo.model.data.quiz

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Word(
    val pck: Long,
    val owner: Long,
    val lec: Long,
    val obj: Long,
    val type: String,
    val deviceLngWord: String,
    val selectedLngWord: String,
    val selectedLngWordTransliterator: String = "",
    val selectedLngPlaceHolderWord: String,
    val selectedLngPlaceHolderWordTransliterator: String = "",
    val isGram: Boolean,
    val isChecked: Boolean = false,
    val color: Color
)

@Immutable
data class ScoreCard(
    val greenPercent: String = "0%",
    val greenPer: Float = 0.0f,
    val greenWords: Int = 0,
    val yellowPercent: String = "0%",
    val yellowPer: Float = 0.0f,
    val yellowWords: Int = 0,
    val redPercent: String = "0%",
    val redPer: Float = 0.0f,
    val redWords: Int = 0,
)


fun List<Word>.toScoreCards(): ScoreCard {
    val totalIteam = this.size
    val redCount = this.filter { it.color == Red }.size
    val greenCount = this.filter { it.color == Green }.size
    val yellowCount = this.filter { it.color == Orange }.size

    return if (totalIteam == 0) {
        ScoreCard()
    } else {
        val redPercent = "${((redCount.toFloat() / totalIteam.toFloat()) * 100).toInt()}"
        val redPer = (redPercent.toFloat() / 100)
        val yellowPercent = "${((yellowCount.toFloat() / totalIteam.toFloat()) * 100).toInt()}"
        val yellowPer = (yellowPercent.toFloat() / 100)
        val greenPercent = "${((greenCount.toFloat() / totalIteam.toFloat()) * 100).toInt()}"
        val greenPer = (greenPercent.toFloat() / 100)
        ScoreCard(
            "${greenPercent}%",
            greenPer,
            greenCount,
            "${yellowPercent}%",
            yellowPer,
            yellowCount,
            "${redPercent}%",
            redPer,
            redCount
        )
    }
}

val Red = Color(0xFFE4554F)
val Orange = Color(0xFFfecb2e)
val Green = Color(0xFF22D94A)
