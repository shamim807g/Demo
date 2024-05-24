package com.lengo.uni.ui.dashboard.progress

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.ui.SegmentText
import com.lengo.common.ui.SegmentedControl
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.Ranking
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen.RankingList

@Composable
fun Leaderboard(
    youTabSelected: Boolean = true,
    rankingList: List<Ranking> = emptyList(),
    topRankList: List<Ranking>,
    onTabSelected: (isYouSelected: Boolean) -> Unit
) {
    val appState = LocalAppState.current
    val boardTitle = appState.userSelectedLang?.locale?.displayLanguage
    val you = stringResource(id = R.string.you)
    val top = stringResource(id = R.string.top)
    val threeSegments = remember { listOf(you, top) }
    val controller = LocalNavigator.current
    VerticleSpace(38.dp)
    Row(modifier = Modifier
        .padding(horizontal = 16.dp)
        .clickable {
            controller.navigate(RankingList.route)
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${boardTitle} ${stringResource(id = R.string.leaderboard)}",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
        )

        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colors.secondary
        )

    }

    VerticleSpace()

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier) {
            SegmentedControl(
                threeSegments,
                if (youTabSelected) you else top,
                modifier = Modifier.padding(16.dp),
                onSegmentSelected = {
                    onTabSelected(it == you)
                }
            ) {
                SegmentText(it)
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable {
                        controller.navigate(RankingList.route)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(id = R.string.LName),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.body1.copy(
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.SemiBold
                    )
                )


                Text(
                    text = stringResource(id = R.string.level),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.weight(0.3f),
                    style = MaterialTheme.typography.body1.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = stringResource(id = R.string.rank),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.weight(0.3f),
                    style = MaterialTheme.typography.body1.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Crossfade(targetState = if (youTabSelected) you else top) { graph ->
                when (graph) {
                    you -> {
                        Column(modifier = Modifier.clickable {
                            controller.navigate(RankingList.route)
                        }) {
                            rankingList.forEach { rank ->
                                LeaderBoardItem(false, rank)
                            }
                        }
                    }

                    top -> {
                        Column(modifier = Modifier.clickable {
                            controller.navigate(RankingList.route)
                        }) {
                            topRankList.forEach { rank ->
                                LeaderBoardItem(false, rank)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderBoardItem(isBig: Boolean = false, rank: Ranking, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                if (rank.isCurrentUser)
                    MaterialTheme.colors.primary
                else Color.Transparent
            )
            .padding(vertical = if(isBig) 16.dp else 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = rank.countryImage),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .border(2.dp, Color.White, CircleShape)
                .padding(2.dp)
                .requiredSize(if(isBig) 30.dp else 20.dp)

        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.name,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1.copy(textAlign = TextAlign.Left)
            )
            if (rank.isPro) {
                Text(
                    text = stringResource(id = R.string.pro),
                    fontSize = 12.sp, color = MaterialTheme.colors.background,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onBackground)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }


        Text(
            text = rank.level,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.body1.copy(textAlign = TextAlign.Center)
        )

        Text(
            text = "#${rank.rank}",
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.subtitle2.copy(textAlign = TextAlign.Center)
        )
    }
}

