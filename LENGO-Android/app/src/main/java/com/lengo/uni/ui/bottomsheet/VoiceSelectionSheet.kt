package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.SoundItem2
import com.lengo.common.ui.theme.LengoBold18h4
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.Theme
import com.lengo.model.data.VoiceItem
import kotlinx.coroutines.flow.StateFlow

@JvmOverloads
@Composable
fun VoiceSelectionSheet(
    offlineVoice: StateFlow<List<VoiceItem>>,
    voices: StateFlow<List<VoiceItem>>,
    onItemSelected: (VoiceItem) -> Unit,
    openSubSheet: () -> Unit,
    playVoice: (VoiceItem) -> Unit,
    onBack: () -> Unit = {},
) {
    val isDarkTheme = LocalDarkModeEnable.current
    val lastVoiceItem: MutableState<VoiceItem?> = remember { mutableStateOf(null) }
    val voicesList = voices.collectAsState().value
    val offlineVoiceList = offlineVoice.collectAsState().value

    LaunchedEffect(key1 = voicesList,offlineVoiceList, block = {
        lastVoiceItem.value = voicesList.find { it.isSelected.value  }
        if(lastVoiceItem.value == null) {
            lastVoiceItem.value = offlineVoiceList.find { it.isSelected.value }
        }
    })

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        SheetAppBar(stringResource(R.string.features_voices), onBack)

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
            ) {
                offlineVoiceList.forEach {
                    Voice(it, onClick = {
                        if(it.tags.contains(R.string.pro)) {
                            openSubSheet()
                        } else {
                            if(lastVoiceItem.value != null) {
                                lastVoiceItem.value?.isSelected?.value = false
                            }
                            it.isSelected.value = true
                            lastVoiceItem.value = it
                            onItemSelected(it)
                        }
                    }, onPlay = {
                        playVoice(it)
                    })
                    Divider(modifier = Modifier.padding(start = 8.dp),color = if(isDarkTheme)
                        MaterialTheme.colors.onSurface else
                        DividerDefaults.color.copy(alpha = 0.5f))
                }
                voicesList.forEachIndexed { index, voiceItem ->
                    Voice(voiceItem, onClick = {
                        if(voiceItem.tags.contains(R.string.pro)) {
                            openSubSheet()
                        } else {
                            if(lastVoiceItem.value != null) {
                                lastVoiceItem.value?.isSelected?.value = false
                            }
                            voiceItem.isSelected.value = true
                            lastVoiceItem.value = voiceItem
                            onItemSelected(voiceItem)
                        }
                    }, onPlay = {
                        playVoice(voiceItem)
                    })
                    if(index < voicesList.size - 1) {
                        Divider(
                            modifier = Modifier.padding(start = 8.dp),
                            color = if(isDarkTheme)
                                MaterialTheme.colors.onSurface else
                                DividerDefaults.color.copy(alpha = 0.5f)
                        )

                    }
                }
            }

        }
    }

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Voice(voice: VoiceItem,onClick: () -> Unit,onPlay:() -> Unit) {

    Surface {
        Row(modifier = Modifier
            .background(
                color = if (voice.isSelected.value) MaterialTheme.colors.primary.copy(alpha = .5f) else
                    MaterialTheme.colors.surface
            )
            .clickable { onClick() }
            .padding(start = 8.dp),verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = voice.personName,
                    style = LengoBold18h4(),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                ) {
                    voice.tags.forEach {
                        Text(text = if(it is Int) stringResource(id = it) else it.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.background,
                            modifier =
                            Modifier
                                .clip(CircleShape)
                                .background(
                                    if (it == R.string.pro) MaterialTheme.colors.onBackground else
                                        MaterialTheme.colors.primary
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

            }

            SoundItem2 {
                onPlay()
            }
        }

    }
}


@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun Chips() {
    Theme {
        FlowRow(Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.Start)) {
            listOf("Pro","asdasdasd","asdasd","Female").forEach {
                Text(text = it,
                    color = MaterialTheme.colors.background,
                    modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(
                            if (it == "Pro") MaterialTheme.colors.onBackground else
                                MaterialTheme.colors.primary
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }

}
