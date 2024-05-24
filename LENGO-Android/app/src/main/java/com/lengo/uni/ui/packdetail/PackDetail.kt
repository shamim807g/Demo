package com.lengo.uni.ui.packdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lengo.common.ui.ChildAppBar
import com.lengo.common.ui.ImageCard2
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.Lection
import com.lengo.model.data.PLACEHOLDER
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen

@Composable
fun PackDetails() {
    val appState = LocalAppState.current
    val controller = LocalNavigator.current
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<PackDetailViewModel>()
    val state: PackDetailViewState by viewModel.models.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ChildAppBar(title = state.packName, onBack = {
            controller.popBackStack()
        })
        VerticleSpace()
        LectionList(state.lection, appState.imageMap) { lec ->
            controller.navigate(Screen.WordList.createRoute(state.packName,"✏️",lec))
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LectionList(
    lection: List<Lection>,
    imageMap: SnapshotStateMap<String, String>,
    onLectionSelected: (Lection) -> Unit
) {
    val vScrollState = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(vScrollState)) {
        lection.forEach { lec ->
            val image = imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"] ?: PLACEHOLDER
            ImageCard2(image, lec.title) {
                onLectionSelected(lec)
            }
            VerticleSpace()
        }

        VerticleSpace(50.dp)
    }
}