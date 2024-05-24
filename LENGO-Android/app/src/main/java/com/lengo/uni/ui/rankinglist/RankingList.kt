package com.lengo.uni.ui.rankinglist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.ChildAppBar
import com.lengo.common.ui.SegmentText
import com.lengo.common.ui.SegmentedControl
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.dashboard.progress.LeaderBoardItem
import com.lengo.uni.ui.dashboard.progress.ProgressViewState


@Composable
fun RankingList() {
    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    val controller = LocalNavigator.current
    val viewModel = activity.progress
    val you = stringResource(id = R.string.you)
    val top = stringResource(id = R.string.top)
    val threeSegments = remember { listOf(you, top) }

    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = ProgressViewState.Empty)
    val youListState = rememberLazyListState()

    LaunchedEffect(key1 = viewState.userFullRankList) {
        if (viewState.userFullRankList.isNotEmpty()) {
            val index = viewState.userFullRankList.indexOfFirst { it.isCurrentUser }
            youListState.scrollToItem(index = index)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ChildAppBar(title = stringResource(R.string.progress_tab), onBack = {
            controller.popBackStack()
        })


        SegmentedControl(
            threeSegments,
            if(viewState.youTabSelected) you else top,
            modifier = Modifier.padding(16.dp),
            onSegmentSelected = {
                viewModel.tabSelected(it == you)
            }
        ) {
            SegmentText(it)
        }

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .fillMaxWidth(),
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


        Column(
            modifier = Modifier.weight(1f)
        ) {
            Crossfade(targetState = if(viewState.youTabSelected) you else top) { graph ->
                when (graph) {
                    you -> {
                        LazyColumn(state = youListState) {
                            items(viewState.userFullRankList) { rank ->
                                LeaderBoardItem(true, rank)
                            }
                        }
                    }

                    top -> {
                        LazyColumn {
                            items(viewState.topFullRankList) { rank ->
                                LeaderBoardItem(true, rank)
                            }
                        }
                    }
                }
            }
        }

    }
}