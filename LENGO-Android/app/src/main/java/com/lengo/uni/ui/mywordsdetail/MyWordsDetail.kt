package com.lengo.uni.ui.mywordsdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.bottomsheet.QuizSettingSheet
import com.lengo.common.ui.ChildAppBar
import com.lengo.common.ui.LengoButton
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.WordItem
import com.lengo.uni.ui.sheet.DownloadLangModelSheet

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyWordsDetail() {
    val navigator = LocalNavigator.current
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<MyWordsDetailViewModel>()
    val state: MyWordsDetailViewState by viewModel.models.collectAsState()

    Column(Modifier.fillMaxSize()) {
        ChildAppBar(
            title = state.wordTitle,
            onBack = { navigator.popBackStack() },
            onSetting = { viewModel.take(MyWordsDetailViewEvent.QuizSettingSheetState(visible = true)) },
            settingIconVisible = true
        )
        if (state.words != null) {
            LazyColumn(Modifier.weight(1f)) {
                if (!state.words.isNullOrEmpty()) {
                    items(items = state.words!!, key = { item ->
                        "${item.obj}${item.lec}${item.pck}${item.owner}"
                    }) { obj ->
                        WordItem(
                            obj.deviceLngWord,
                            obj.selectedLngWord,
                            obj.selectedLngWordTransliterator,
                            obj.isChecked,
                            obj.isGram,
                            obj.color,
                            {
                                viewModel.take(MyWordsDetailViewEvent.onWordSeleted(obj))
                            },
                            onSpeak = { txt, isAdded -> viewModel.take(MyWordsDetailViewEvent.onSpeak(obj, txt, isAdded)) }
                        )
                        VerticleSpace()
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            LengoButton(
                Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp), enabled = state.isQuizButtonEnable
            ) {
                viewModel.take(MyWordsDetailViewEvent.onPrepareQuiz)
                navigator.navigate(Screen.Quiz.createRoute(state.wordsColor,"aa","packname",0L,0L,0L,"hh","aa"))
            }
        }

    }

    QuizSettingSheet(state.isQuizSettingSheetVisible,
        onPackStatusChange = {},
        onPackLecNameChange = { pck,lec -> }) {
        viewModel.take(MyWordsDetailViewEvent.QuizSettingSheetState(false))
    }

    DownloadLangModelSheet(state.isLangDownloadSheetVisible) {
        viewModel.take(MyWordsDetailViewEvent.DownloadSheetState(false))
    }
}