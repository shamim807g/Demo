package com.lengo.uni.ui.bottomsheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.PlayStoreAppContract
import com.lengo.common.R
import com.lengo.common.ui.LangSelectItem
import com.lengo.common.ui.SheetAppBar
import com.lengo.model.data.Lang
import com.lengo.uni.BuildConfig
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.sheet.BaseModalSheet

@Composable
fun LangSelectSheet(visible: Boolean, onDismiss: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(PlayStoreAppContract()) {}
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    val appState = LocalAppState.current

    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        LanguageSelectionContent(appState.allLanguage, {
            if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
                activity.mainViewModel.syncDataWithServer()
                mainViewModel.selLanguageSelected(it)
            } else {
                launcher.launch(it.code)
            }
            onDismiss()
        }) {
            onDismiss()
        }
    }
}

@JvmOverloads
@Composable
fun LanguageSelectionContent(
    langList: List<Lang>,
    onItemSelected: (Lang) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        SheetAppBar(stringResource(R.string.selectLanguage), onBack)
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            langList.forEach {
                LangSelectItem(
                    it.drawable,
                    if (it.code == "us") "${it.locale.displayLanguage} (USA)" else it.locale.displayLanguage
                ) {
                    onItemSelected(it)
                }
            }
        }
    }

}