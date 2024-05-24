package com.lengo.data.repository


import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.extension.getCurrentDate
import com.lengo.common.extension.getDate
import com.lengo.common.extension.getDayOfWeek
import com.lengo.common.R
import com.lengo.common.extension.formattedMinChartDate
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.preload.AchievementsModel
import com.lengo.database.appdatabase.doa.DateStatsDoa
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.model.data.Achievements
import com.lengo.model.data.WeekModel
import com.lengo.preferences.LengoPreference
import com.lengo.common.ui.graph.bar.BarChartData
import com.lengo.common.ui.line.data.ChartColors
import com.lengo.common.ui.line.data.DrawAxis
import com.lengo.common.ui.line.data.LineChartData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Immutable
data class ScoreModel(
    val vocab: String = "0",
    val expression: String = "0",
    val minutes: String = "0"
)


class ProgressRepository @Inject constructor(
    private val userDoa: UserDoa,
    private val dataStatsDoa: DateStatsDoa,
    @Dispatcher(LengoDispatchers.IO) val ioDispatchers: CoroutineDispatcher,
    private val lengoPreference: LengoPreference,
    private val packsDao: PacksDao,
    private val lengoDataSource: LengoDataSource
) {

    suspend fun updateSecounds(secound: Long, selLang: String) {
        withContext(ioDispatchers) {
            val todayDate = getCurrentDate()
            dataStatsDoa.updateOrInsertSecounds(todayDate, selLang, secound)
        }
    }

    suspend fun updateEditPack(point: Int, type: String, selLang: String) {
        withContext(ioDispatchers) {
            val todayDate = getCurrentDate()
            if (point <= 0 && type == SYS_VOCAB) {
                dataStatsDoa.updateDateStateVocab(todayDate, selLang)
            } else if (point > 0 && type == SYS_VOCAB) {
                dataStatsDoa.updateDateStateCorrectVocb(todayDate, selLang)
            } else if (point <= 0 && type == SYS_GRAMMER) {
                dataStatsDoa.updateDateStateGram(todayDate, selLang)
            } else if (point > 0 && type == SYS_GRAMMER) {
                dataStatsDoa.updateDateStateCorrectGram(todayDate, selLang)
            }
        }
    }

    suspend fun getScoreModel(): ScoreModel {
        return withContext(ioDispatchers) {
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = DEFAULT_OWN_LANG
            )
            val totalVocab = dataStatsDoa.sumOfEditedVocabs(selLang.sel) ?: 0
            val totalExpression = dataStatsDoa.sumOfEditedGram(selLang.sel) ?: 0
            val totalSec = dataStatsDoa.sumOfSecound(selLang.sel) ?: 0
            val minutes = TimeUnit.SECONDS.toMinutes(totalSec)
            return@withContext ScoreModel(
                totalVocab.toString(),
                totalExpression.toString(),
                minutes.toInt().toString()
            )
        }
    }

    suspend fun getAchivementsData(): List<Achievements> {
        return withContext(ioDispatchers) {
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = DEFAULT_OWN_LANG
            )
            val achievements = lengoDataSource.getAchivementModel()!!
            val list = mutableListOf<Achievements>()
            //data Streak
            val streak = getTotalStreak()
            val points = getTotalValue("daystreak", streak, achievements, 1)
            val item = Achievements(
                title = R.string.TageStrike,
                total = points.first,
                count = streak,
                earnPoints = points.second,
                progress = if (points.first != 0) (streak / (points.first).toFloat()) else 0.0f
            )
            list.add(item)
            //vocab pack
            val allVocabPack = packsDao.getTotalPackEdited(SYS_VOCAB, selLang.sel)
            val vocabPacks = allVocabPack?.distinctBy { "${it.pck}${it.owner}" }?.size ?: 0
            val vocabpoints = getTotalValue("vocpacksedited", vocabPacks, achievements, 1)
            val vocabitem = Achievements(
                title = R.string.VokabelPacksBearbeitet,
                total = vocabpoints.first,
                count = vocabPacks,
                earnPoints = vocabpoints.second,
                progress = if (vocabpoints.first != 0) (vocabPacks / (vocabpoints.first).toFloat()) else 0.0f
            )
            list.add(vocabitem)
            //gram pack
            val allGrammerPack = packsDao.getTotalPackEdited(SYS_GRAMMER, selLang.sel)
            val gramPacks = allGrammerPack?.distinctBy { "${it.pck}${it.owner}" }?.size ?: 0
            val grampoints = getTotalValue("grampacksedited", gramPacks, achievements, 1)
            val gramitem = Achievements(
                title = R.string.GrammatikPacksBearbeitet,
                total = grampoints.first,
                count = gramPacks,
                earnPoints = grampoints.second,
                progress = if (grampoints.first != 0) (gramPacks / (grampoints.first).toFloat()) else 0.0f
            )
            list.add(gramitem)
            //total Words
            val totalVocab = dataStatsDoa.sumOfEditedVocabs(selLang.sel) ?: 0
            val totalVocabpoints = getTotalValue("learnedvocs", totalVocab, achievements, 3)
            val totalVocabitem = Achievements(
                title = R.string.gelernteVokabeln,
                total = totalVocabpoints.first,
                count = totalVocab,
                earnPoints = totalVocabpoints.second,
                progress = if (totalVocabpoints.first != 0) (totalVocab / (totalVocabpoints.first).toFloat()) else 0.0f
            )
            list.add(totalVocabitem)
            //Hours
            val totalSec = dataStatsDoa.sumOfSecound(selLang.sel) ?: 0
            val hours = TimeUnit.SECONDS.toHours(totalSec)
            val hourspoints = getTotalValue("learnhours", hours.toInt(), achievements, 0)
            val hoursitem = Achievements(
                title = R.string.Lernstunden,
                total = hourspoints.first,
                count = hours.toInt(),
                earnPoints = hourspoints.second,
                progress = if (hourspoints.first != 0) (hours.toInt() / (hourspoints.first).toFloat()) else 0.0f
            )
            list.add(hoursitem)

            return@withContext list
        }
    }

    fun getTotalValue(
        type: String,
        currentValue: Int,
        achievements: AchievementsModel,
        initialStartStep: Int
    ): Pair<Int, Int> {
        //val startStepObj = achievements.structure.achievements.find { it.id == type }!!
        var startStep = initialStartStep
        var total = 0
        var pointToEarn = 0
        while (total <= currentValue) {
            val step = achievements.structure.points_steps_list.find { it.id == startStep }!!
            total = step.points
            pointToEarn = step.id * 5
            startStep++
        }
        return Pair(total, pointToEarn)
    }


    suspend fun getMinChartData(): Pair<Long, BarChartData> {
        return withContext(ioDispatchers) {
            val deviceLocale = DEFAULT_OWN_LANG
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = deviceLocale
            )
            //val editedList = mutableListOf<DateStatsDoa.SecoundsWithDate>()
            val existingSecoundsList = dataStatsDoa.getSecounds(selLang.sel) ?: emptyList()


            //val date = getDate(0, deviceLocale)
            //val entry = existingSecoundsList.find { it.date == date }
//            if (entry != null) {
//                editedList.add(entry)
//            } else {
//                editedList.add(DateStatsDoa.SecoundsWithDate(0L, date))
//            }
//            for (n in -3..0) {
//                val date = getDate(n)
//                val entry = existingSecoundsList.find { it.date == date }
//                if (entry != null) {
//                    editedList.add(entry)
//                } else {
//                    editedList.add(DateStatsDoa.SecoundsWithDate(0L,date))
//                }
//            }
            val average = existingSecoundsList.map { it.seconds }.average().toLong()
            val averageMin = TimeUnit.SECONDS.toMinutes(average)
            Pair(averageMin, toMinChartData(existingSecoundsList))
        }
    }


    suspend fun getWordsCharData(): Pair<Long, LineChartModel> {
        return withContext(ioDispatchers) {
            val deviceLocale = DEFAULT_OWN_LANG
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = deviceLocale
            )
            //val editedList = mutableListOf<Long>()
            val existingDates = dataStatsDoa.getEditedVocabs(selLang.sel)?.filter { it.edited_vocab > 0 } ?: emptyList()
            val editedWords = existingDates.map { it.edited_vocab }
//            val date = getDate(0, deviceLocale)
//            val entry = existingDates.find { it.date == date }
//            if (entry != null) {
//                editedList.add(entry.edited_vocab)
//            } else {
//                editedList.add(0)
//            }
            val average = editedWords.average().toLong()
            return@withContext Pair(average, toWordCharData(editedWords))
        }
    }

    private fun toMinChartData(list: List<DateStatsDoa.SecoundsWithDate>): BarChartData {
        val point = mutableListOf<BarChartData.Bar>()
        list.forEach {
            val min = TimeUnit.SECONDS.toMinutes(it.seconds)
            point.add(
                BarChartData.Bar(
                    labelMiddle = "${min}",
                    labelBottom = formattedMinChartDate(it.date),
                    value = min.toFloat()
                )
            )
        }
        return BarChartData(bars = point)
    }


    suspend fun getExpressionChartData(): Pair<Long, LineChartModel?> {
        return withContext(ioDispatchers) {
            val deviceLocale = DEFAULT_OWN_LANG
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = deviceLocale
            )
            //val editedList = mutableListOf<Long>()
            val existingDates = dataStatsDoa.getEditedGram(selLang.sel)?.filter { it.edited_gram > 0 } ?: emptyList()
            val entries = existingDates.map { it.edited_gram }
//            val date = getDate(0, deviceLocale)
//            val entry = existingDates.find { it.date == date }
//            if (entry != null) {
//                editedList.add(entry.edited_gram)
//            } else {
//                editedList.add(0)
//            }
            val average = entries.average().toLong()

            return@withContext Pair(average, toxpressionChartData(entries))
        }
    }

    private fun toxpressionChartData(list: List<Long>): LineChartModel {
        val poinst = mutableListOf(LineChartData.SeriesData.Point(0, 0f))
        val xLabel = mutableListOf("")
        list.forEachIndexed { index, s ->
            poinst.add(LineChartData.SeriesData.Point(index + 1, s.toFloat()))
            xLabel.add(s.toString())
        }
        return LineChartModel(points = poinst, xLabels = xLabel)
//        return LineChartData(
//            chartColors = ChartColors.defaultColors().copy(
//                axis = Color.White,
//                xlabel = Color.White
//            ),
//            series = listOf(
//                LineChartData.SeriesData(
//                    title = "Line A",
//                    points = poinst,
//                    Color.White, gradientFill = true
//                )
//            ),
//            xLabels = xLabel,
//            drawAxis = DrawAxis.X,
//        )
    }


    private fun toWordCharData(list: List<Long>): LineChartModel {
        val points = mutableListOf(LineChartData.SeriesData.Point(0, 0f))
        val xLabel = mutableListOf("")
        list.forEachIndexed { index, s ->
            points.add(LineChartData.SeriesData.Point(index + 1, s.toFloat()))
            xLabel.add(s.toString())
        }
        return LineChartModel(points = points, xLabels = xLabel)
//        return LineChartData(
//            chartColors = ChartColors.defaultColors().copy(
//                axis = Color.White,
//                xlabel = Color.White
//            ),
//            series = listOf(
//                LineChartData.SeriesData(
//                    title = "Line A",
//                    points = poinst,
//                    Color.Red, gradientFill = true
//                )
//            ),
//            xLabels = xLabel,
//            drawAxis = DrawAxis.X,
//        )
    }


    suspend fun getWeekData(): List<WeekModel> {
        return withContext(ioDispatchers) {
            val deviceLocale = DEFAULT_OWN_LANG
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = deviceLocale
            )
            val existingDates = dataStatsDoa.getDates(selLang.sel) ?: emptyList()
            val list = mutableListOf<WeekModel>()
            for (n in -6..0) {
                val date = getDate(n, deviceLocale)
                list.add(
                    WeekModel(
                        displayWeekDay = getDayOfWeek(n, deviceLocale),
                        currentDate = date,
                        isDayPresent = existingDates.contains(date)
                    )
                )
            }
            return@withContext list
        }
    }


    suspend fun getTotalStreak(): Int {
        return withContext(ioDispatchers) {
            val deviceLocale = DEFAULT_OWN_LANG
            val selLang = userDoa.getSyncUserLang() ?: UserDoa.UserLang(
                DEFAULT_SEL_LANG,
                own = deviceLocale
            )
            val existingDates = dataStatsDoa.getDates(selLang.sel) ?: emptyList()
            var totalStreak = 0
            for (n in -6..0) {
                val date = getDate(n, deviceLocale)
                if (existingDates.contains(date)) {
                    ++totalStreak
                } else {
                    totalStreak = 0
                }
            }
            return@withContext totalStreak
        }
    }

    data class LineChartModel(
        val points: List<LineChartData.SeriesData.Point> = emptyList(),
        val xLabels: List<String> = emptyList()
    )


}