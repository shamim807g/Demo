package com.lengo.uni.ui.bottomsheet

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
import androidx.compose.ui.res.stringResource
import com.lengo.common.R
import com.lengo.common.deviceLang
import com.lengo.common.ui.LangSelectItem
import com.lengo.common.ui.SheetAppBar
import com.lengo.model.data.DeviceLang

@JvmOverloads
@Composable
fun DeviceLanguageSelectionSheet(
    onItemSelected: (DeviceLang) -> Unit = {},
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
            deviceLang.forEach {
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