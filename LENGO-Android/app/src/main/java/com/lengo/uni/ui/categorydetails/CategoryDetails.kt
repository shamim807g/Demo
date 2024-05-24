package com.lengo.uni.ui.categorydetails

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lengo.common.ui.ChildAppBar
import com.lengo.common.ui.HeadingCoin
import com.lengo.common.ui.ImageCard
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.Lection
import com.lengo.model.data.PLACEHOLDER
import com.lengo.model.data.Pack
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen

@Composable
fun CategoryDetails() {
    val appState = LocalAppState.current
    val controller = LocalNavigator.current
    val viewModel = hiltViewModel<CategoryDetailViewModel>()
    val state: CategoryDetailViewState by viewModel.models.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                CategoryEvents.INSUFFICIENT_COIN -> {
                    controller.navigate(Screen.Profile.route)
                }
                is CategoryEvents.OPEN_PACK -> {
                    controller.navigate(Screen.PacksDetails.createRoute(it.pack))
                }
                is CategoryEvents.OPEN_LECTION -> {
                    controller.navigate(Screen.WordList.createRoute( it.pack, it.lec))
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ChildAppBar(title = stringResource(state.categoryName), onBack = { controller.popBackStack() })
        if(state.packs.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center))
        } else {
            PackList(state.packs, appState.imageMap, selectPack = { pack ->
                viewModel.take(CategoryAction.processPack(pack))
            }, onLectionSelected = { pack, lection ->
                viewModel.take(CategoryAction.processPackWithLection(pack, lection))
            })
        }
    }
}

@Composable
fun PackList(
    packs: List<Pack>,
    imageMap: SnapshotStateMap<String, String>,
    selectPack: (Pack) -> Unit,
    onLectionSelected: (Pack, Lection) -> Unit
) {

    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = WindowInsets.systemBars.only( WindowInsetsSides.Bottom).asPaddingValues()
    ) {
        items(items = packs, key = { pack ->
            "${pack.pck}${pack.owner}${pack.type}${pack.lang}${pack.badge}"
        }) { pack ->
            VerticleSpace()
            HeadingCoin(
                pack.title,
                pack.coins,
                pack.badge
            ) {
                selectPack(pack)
            }
            VerticleSpace()
            HorizontalLections(pack.lections, imageMap, onLectionSelected = {
                onLectionSelected(pack, it)
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HorizontalLections(
    lections: List<Lection>,
    imageMap: SnapshotStateMap<String, String>,
    onLectionSelected: (Lection) -> Unit
) {
    val hScrollState = rememberScrollState()
    Row(
        Modifier
            .horizontalScroll(hScrollState)
            .padding(horizontal = 8.dp)
    ) {
        lections.forEach { lec ->
            key(
                lec.title,
                lec.lang,
                lec.lec,
                lec.owner,
                lec.pck,
                lec.type,
                imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                    ?: PLACEHOLDER
            ) {
                ImageCard(
                    modifier = Modifier
                        .width(300.dp)
                        .aspectRatio(4 / 3f),
                    name = lec.title,
                    image = imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                        ?: PLACEHOLDER,
                ) {
                    onLectionSelected(lec)
                }
            }
        }
    }
}