package com.lengo.uni.ui.dashboard.progress

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.extension.HexToJetpackColor
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.graph.bar.BarChart
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.MainActivity
import com.lengo.common.ui.AnimatedCircle
import com.lengo.common.ui.HorizontalSpace
import com.lengo.common.ui.SegmentText
import com.lengo.common.ui.SegmentedControl
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.bar.renderer.bar.SimpleBarDrawer
import com.lengo.common.ui.bar.renderer.label.SimpleValueDrawer
import com.lengo.model.data.Achievements
import com.lengo.model.data.LNGColor
import com.lengo.model.data.Lang
import com.lengo.model.data.WeekModel
import com.lengo.common.ui.theme.*
import com.lengo.common.ui.line.LineChart
import com.lengo.common.ui.line.data.ChartColors
import com.lengo.common.ui.line.data.DrawAxis
import com.lengo.common.ui.line.data.LineChartData
import java.util.Locale

@Composable
fun Progress() {
    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    val viewModel = activity.progress
    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = ProgressViewState.Empty)

    DisposableEffect(key1 = Unit, effect = {
        viewModel.getData()
        onDispose { }
    })
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ProgressEvents.LangUpdated -> {
                    viewModel.getData()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        VerticleSpace()
        val vSrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(vSrollState)
                .background(color = MaterialTheme.colors.background)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeekProgress(
                    modifier = Modifier
                        .weight(1f)
                        .height(65.dp), viewState.weekList
                )
                StrekProgress(
                    modifier = Modifier.height(65.dp),
                    percentage = viewState.streakPercentage,
                    progress = viewState.streakProgress
                )
            }
            VerticleSpace()
            GraphBox(
                Modifier
                    .height(400.dp)
                    .padding(horizontal = 16.dp), viewState
            )
            VerticleSpace()

            ScoreBox(
                Modifier
                    .padding(horizontal = 16.dp)
                    .height(80.dp),
                viewState.scoreModel.vocab,
                viewState.scoreModel.expression,
                viewState.scoreModel.minutes
            )

            //VerticleSpace()

            Leaderboard(viewState.youTabSelected, viewState.userRankList, viewState.topRankList) {
                viewModel.tabSelected(it)
            }

            Achivements(viewState.achivementsList)
        }


    }
}


@Composable
fun Achivements(achivements: List<Achievements>?) {
    VerticleSpace(38.dp)
    Text(
        text = stringResource(id = R.string.achievements),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
    )
    VerticleSpace()
    if (achivements != null) {
        achivements.forEach {
            AchivementItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .fillMaxWidth(),
                title = stringResource(id = it.title),
                count = it.count,
                total = it.total,
                progress = it.progress,
                percentage = it.earnPoints
            )
        }
    } else {
        Box(modifier = Modifier.size(100.dp)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}


@Composable
fun ScoreBox(
    modifier: Modifier,
    vocb: String = "0",
    expres: String = "9",
    min: String = "10"
) {
    Row(
        modifier = modifier.background(
            color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
        ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreItem(
            modifier = Modifier.weight(1f),
            vocb,
            stringResource(id = R.string.vocabularyLocal)
        )
        ScoreItem(
            modifier = Modifier.weight(1f),
            expres,
            stringResource(id = R.string.ausdruecke)
        )

        ScoreItem(
            modifier = Modifier.weight(1f),
            min,
            stringResource(id = R.string.minutes)
        )

    }
}

@Composable
fun WeekProgress(modifier: Modifier, weekList: List<WeekModel>) {
    Row(
        modifier = modifier.background(
            color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
        ), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekList.forEach {
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Circle(if (!it.isDayPresent) MaterialTheme.colors.surface else Green)
                Text(
                    text = "${it.displayWeekDay}",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun StrekProgress(
    modifier: Modifier = Modifier,
    progress: Float = 0.5f,
    percentage: String = "100"
) {
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
        )
    ) {
        ProgressBox(
            modifier = Modifier
                .size(60.dp)
                .padding(5.dp)
                .align(Alignment.Center), progress, percentage
        )
    }
}

@Composable
fun Circle(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension / 4
        )
    }
}

@Composable
fun GraphBox(modifier: Modifier, viewState: ProgressViewState) {
    val word = stringResource(id = R.string.WordTLocal)
    val expresion = stringResource(id = R.string.ausdruecke)
    val minutes = stringResource(id = R.string.minutes)
    val threeSegments = remember { listOf(word, expresion, minutes) }
    var selectedThreeSegment by remember { mutableStateOf(threeSegments.first()) }

    Box(
        modifier = modifier.background(
            color = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(8.dp)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            VerticleSpace()
            SegmentedControl(
                threeSegments,
                selectedThreeSegment,
                onSegmentSelected = { selectedThreeSegment = it }
            ) {
                SegmentText(it)
            }

            Crossfade(targetState = selectedThreeSegment) { graph ->
                when (graph) {
                    word -> {
                        if (viewState.wordsChartModel != null) {
                            Column(modifier = Modifier.testTag("line_chart")) {
                                VerticleSpace(16.dp)
                                Text(
                                    text = stringResource(id = R.string.WordTLocal),
                                    style = LengoHeadingh6(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                Text(
                                    text = "${stringResource(id = R.string.LDailyAverage)}: ${viewState.wordAvg}",
                                    style = LengoNormal14body2(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                VerticleSpace()
                                LineChart(
                                    chartHeight = 316.dp,
                                    data = LineChartData(
                                        chartColors = ChartColors.defaultColors().copy(
                                            axis = MaterialTheme.colors.onBackground,
                                            xlabel = MaterialTheme.colors.onBackground,
                                            ylabel = MaterialTheme.colors.onBackground,
                                            horizontalLines = MaterialTheme.colors.onBackground,
                                            drillDownLine = MaterialTheme.colors.onBackground,
                                        ),
                                        series = listOf(
                                            LineChartData.SeriesData(
                                                title = "Line A",
                                                points = viewState.wordsChartModel.points,
                                                MaterialTheme.colors.primary, gradientFill = true
                                            )
                                        ),
                                        xLabels = viewState.wordsChartModel.xLabels,
                                        drawAxis = DrawAxis.X,
                                        xAxisTypeface = MaterialTheme.typography.caption
                                    ),
                                    onDrillDown = null,
                                    legend = null
                                )
                            }
                        }
                    }

                    expresion -> {
                        if (viewState.expressionChartModel != null) {
                            Column {
                                VerticleSpace()
                                Text(
                                    text = stringResource(id = R.string.ausdruecke),
                                    style = LengoHeadingh6(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                Text(
                                    text = "${stringResource(id = R.string.LDailyAverage)}: ${viewState.expressionAverage}",
                                    style = LengoNormal14body2(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                VerticleSpace()
//                                LineChart(
//                                    chartHeight = 300.dp,
//                                    data = viewState.expressionChartData,
//                                    onDrillDown = null,
//                                    legend = null
//                                )

                                LineChart(
                                    chartHeight = 300.dp,
                                    data = LineChartData(
                                        chartColors = ChartColors.defaultColors().copy(
                                            axis = MaterialTheme.colors.onBackground,
                                            xlabel = MaterialTheme.colors.onBackground,
                                            ylabel = MaterialTheme.colors.onBackground,
                                            horizontalLines = MaterialTheme.colors.onBackground,
                                            drillDownLine = MaterialTheme.colors.onBackground,
                                        ),
                                        series = listOf(
                                            LineChartData.SeriesData(
                                                title = "Line A",
                                                points = viewState.expressionChartModel.points,
                                                MaterialTheme.colors.primary, gradientFill = true
                                            )
                                        ),
                                        xLabels = viewState.expressionChartModel.xLabels,
                                        drawAxis = DrawAxis.X,
                                        xAxisTypeface = MaterialTheme.typography.caption
                                    ),
                                    onDrillDown = null,
                                    legend = null
                                )

                            }
                        }
                    }

                    minutes -> {
                        if (viewState.minChartData != null) {
                            Column {
                                VerticleSpace()
                                Text(
                                    text = minutes,
                                    style = LengoHeadingh6(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                Text(
                                    text = "${stringResource(id = R.string.LDailyAverage)}: ${viewState.minAverage}",
                                    style = LengoNormal14body2(),
                                    color = MaterialTheme.colors.onBackground
                                )
                                VerticleSpace()
                                BarChart(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .padding(end = 16.dp)
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    barChartData = viewState.minChartData,
                                    barDrawer = SimpleBarDrawer(MaterialTheme.colors.primary),
                                    labelDrawer = SimpleValueDrawer(
                                        SimpleValueDrawer.DrawLocation.Outside,
                                        12.sp,
                                        MaterialTheme.colors.onBackground
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}


@Composable
fun ProgressBox(modifier: Modifier = Modifier, progress: Float = 0.5f, percentage: String = "100") {
    Box(modifier = modifier) {
        AnimatedCircle(
            listOf(1.0f, progress),
            listOf(
                MaterialTheme.colors.primary.copy(alpha = 0.2f),
                MaterialTheme.colors.primary
            ),
            modifier = Modifier.fillMaxSize(),
            strokeDp = 6.dp
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "$percentage",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun AchivementItem(
    modifier: Modifier = Modifier,
    progress: Float = 0.5f,
    percentage: Int = 0,
    title: String = "Days Streak",
    count: Int = 0,
    total: Int = 0
) {
    Row(
        modifier = modifier.background(
            color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ProgressBox(
            modifier = Modifier
                .padding(8.dp)
                .size(55.dp),
            progress, "$percentage"
        )

        HorizontalSpace()

        Column {
            Text(
                text = title, style = MaterialTheme.typography.subtitle1
                    .copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colors.onBackground
            )

            Text(
                text = "${count}/${total}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        }


    }
}

@Composable
fun ScoreItem(modifier: Modifier, score: String, item: String) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = item,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground
        )
    }
}


@Preview
@Composable
fun LeaderboardPreview() {
    LENGOTheme(
        Lang(
            locale = Locale("es"),
            code = "es",
            accent = "es-MX",
            iso639_3 = "spa",
            drawable = R.drawable.spain, colors = LNGColor(
                HexToJetpackColor.getColor("FACF08"),
                HexToJetpackColor.getColor("F37A02")
            )
        )
    ) {
        // Leaderboard(topRankList = viewState.topRankList, vi)
    }
}


@Preview
@Composable
fun ProgressPreview() {
    val viewState = remember {
        ProgressViewState(
            streakPercentage = "s",
            weekList = listOf(
                WeekModel("Sun", "asdadasd", true),
                WeekModel("Mon", "asdadasd", true),
                WeekModel("Tue", "asdadasd", false),
                WeekModel("Wed", "asdadasd", true),
                WeekModel("Thr", "asdadasd", true),
                WeekModel("Fri", "asdadasd", false),
                WeekModel("Sat", "asdadasd", true)
            )
        )
    }
    LENGOTheme(
        Lang(
            locale = Locale("es"),
            code = "es",
            accent = "es-MX",
            iso639_3 = "spa",
            drawable = R.drawable.spain, colors = LNGColor(
                HexToJetpackColor.getColor("FACF08"),
                HexToJetpackColor.getColor("F37A02")
            )
        )
    ) {
        Progress()
    }

}




