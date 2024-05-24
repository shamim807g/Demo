package com.lengo.uni.ui.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.PLACEHOLDER
import com.lengo.common.R
import com.lengo.common.USER_VOCAB
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.MainActivity
import com.lengo.common.ui.EmojiBox
import com.lengo.common.ui.ImageCard3
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.TextChip
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.theme.Grey
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoCaption
import com.lengo.common.ui.theme.LengoRegular18h4
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.lightGrey
import com.lengo.model.data.BADGE
import com.lengo.model.data.Lection
import com.lengo.model.data.SettingModel
import com.lengo.uni.ui.sheet.BaseModalSheet
import com.lengo.uni.ui.sheet.BottomSheetVisibleState
import com.lengo.uni.ui.sheet.SubscriptionModelSheet
import com.lengo.uni.ui.sheet.SubscriptionSheetState
import com.lengo.uni.ui.sheet.VoicesSelectSelectSheet
import com.lengo.uni.ui.wordlist.PackPublicStatus

@ExperimentalMaterialApi
@Composable
fun QuizSettingSheet(
    visible: Boolean,
    lecName: String = "",
    lection: Lection? = null,
    packName: String = "",
    packEmoji: String = "✏️",
    packId: Long? = null,
    packType: String = "",
    packPublicStatus: PackPublicStatus = PackPublicStatus(),
    onPackStatusChange: (Boolean) -> Unit,
    onPackLecNameChange: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    val myPackViewModel = activity.myPackViewModel
    val appState = LocalAppState.current
    var isEmojiSheetVisible by remember { mutableStateOf(false) }
    var pakEmoji by remember { mutableStateOf(packEmoji) }
    var updatedPackName by remember { mutableStateOf(packName) }
    var updatedLecName by remember { mutableStateOf(lecName) }

    BaseModalSheet(visible = visible, onDismiss = onDismiss) {
        QuizSettingContent(
            onBack = onDismiss,
            lection = lection,
            lecName = lecName,
            packName = packName,
            packType = packType,
            packEmoji = pakEmoji,
            packPublicStatus = packPublicStatus,
            onPackStatusChange = onPackStatusChange,
            settingModel = appState.settingModel,
            onEmojiSheet = { isEmojiSheetVisible = true },
            onPackNameChange = {
                if (packId != null) {
                    myPackViewModel.updatePackTitle(packId, it) {
                        activity.mainViewModel.syncDataWithServer()
                    }
                }
                updatedPackName = it
                onPackLecNameChange(updatedPackName, updatedLecName)
            },
            onLecNameChange = {
                if (packId != null && lection != null) {
                    myPackViewModel.updatePackLectionTitle(packId, lection, it) {
                        myPackViewModel.updateLectionImage(lection, it) {
                            activity.mainViewModel.syncDataWithServer()
                        }
                    }
                }
                updatedLecName = it
                onPackLecNameChange(updatedPackName, updatedLecName)
            },
            imageMap = appState.imageMap
        ) {
            mainViewModel.UpdateSettingModel(it)
        }
    }

    if (packId != null && lection != null) {
        EmojiModelSheet(isEmojiSheetVisible, packId, lection.type, lection.owner, lection.lang) {
            isEmojiSheetVisible = false
            pakEmoji = it
            activity.mainViewModel.syncDataWithServer()
        }
    }

}

@ExperimentalMaterialApi
@Composable
private fun QuizSettingContent(
    onBack: () -> Unit,
    lection: Lection? = null,
    lecName: String = "",
    packName: String = "",
    packType: String = "",
    packEmoji: String = "✏️",
    packPublicStatus: PackPublicStatus,
    onPackStatusChange: (Boolean) -> Unit,
    onEmojiSheet: () -> Unit,
    onPackNameChange: (String) -> Unit,
    onLecNameChange: (String) -> Unit,
    settingModel: SettingModel,
    imageMap: SnapshotStateMap<String, String>,
    changeSettingModel: (SettingModel) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        SheetAppBar(stringResource(id = R.string.Einstellungen), onBack)
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            PackSetting(
                lection,
                lecName,
                packName,
                packType,
                packEmoji,
                packPublicStatus,
                onPackStatusChange,
                onEmojiSheet,
                onPackNameChange,
                onLecNameChange,
                imageMap
            )
            TaskSetting(settingModel, changeSettingModel)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PackSetting(
    lection: Lection? = null,
    lecName: String = "",
    pkName: String = "",
    packType: String = "",
    packEmoji: String = "✏️",
    packPublicStatus: PackPublicStatus,
    onPackStatusChange: (Boolean) -> Unit,
    onEmojiSheet: () -> Unit,
    onPackNameChange: (String) -> Unit,
    onLecNameChange: (String) -> Unit,
    imageMap: SnapshotStateMap<String, String>
) {
    val activity = (LocalContext.current as MainActivity)
    val appState = LocalAppState.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var packName by remember { mutableStateOf("") }
    var lectionName by remember { mutableStateOf("") }
    var isPackPublic by remember { mutableStateOf(packPublicStatus.isPackPublic) }
    val isDarkTheme = LocalDarkModeEnable.current

    if (packType == USER_VOCAB) {
        Text(
            stringResource(R.string.packLocal).uppercase(),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(6.dp)
                ) {
                    EmojiBox(packEmoji, 52.dp, onClick = onEmojiSheet)

                    TextField(
                        value = packName,
                        onValueChange = { packName = it },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = if (isDarkTheme) lightGrey else Grey
                        ),
                        maxLines = 1,
                        placeholder = {
                            Text(
                                text = pkName,
                                style = LengoRegular18h4().copy(
                                    color = MaterialTheme.colors.secondary,
                                    textAlign = TextAlign.Start,
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (packName.isNotEmpty()) {
                                onPackNameChange(packName)
                                packName = ""
                            }
                        }),
                        textStyle = LengoRegular18h4().copy(
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Start,
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    )
                }

                if(appState.isUserLogin) {
                    ListItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.IosShare,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colors.onBackground
                            )
                        },
                        text = {
                            Text(
                                stringResource(id = R.string.invitation_link),
                                color = MaterialTheme.colors.onBackground
                            )
                        },
                        trailing = {
                            TextChip(bagText = BADGE.SHARE) {

                            }
                        }
                    )
                }

                if(appState.isUserLogin) {
                    checkItem(
                        isPackPublic,
                        Icons.Filled.Public,
                        stringResource(id = R.string.public_),
                        packPublicStatus.isPackPublicLoading
                    ) {
                        onPackStatusChange(it)
                    }
                }
            }
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                getPackStatusMessage(packPublicStatus),
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = LengoCaption().copy(textAlign = TextAlign.Left)
            )
        }

        VerticleSpace(16.dp)

        Text(
            stringResource(R.string.daily_lesson).uppercase(),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(6.dp)
            ) {

                lection?.let {
                    ImageCard3(
                        modifier = Modifier
                            .width(80.dp)
                            .aspectRatio(16 / 10f),
                        name = lection.title,
                        image = imageMap["${lection.lang}${lection.type}${lection.lec}${lection.owner}${lection.pck}"]
                            ?: PLACEHOLDER
                    ) {
                        //goToLectionWords(lec)
                    }
                }

                TextField(
                    value = lectionName,
                    onValueChange = { lectionName = it },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onBackground,
                        backgroundColor = Color.Transparent,
                        unfocusedIndicatorColor = if (isDarkTheme) lightGrey else Grey
                    ),
                    maxLines = 1,
                    placeholder = {
                        Text(
                            text = lecName,
                            style = LengoRegular18h4().copy(
                                color = MaterialTheme.colors.secondary,
                                textAlign = TextAlign.Start,
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (lectionName.isNotEmpty()) {
                            onLecNameChange(lectionName)
                            lectionName = ""
                        }
                    }),
                    textStyle = LengoRegular18h4().copy(
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Start,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun getPackStatusMessage(packPublicStatus: PackPublicStatus): String {
    if(packPublicStatus.message == R.string.rejected_because_blocked_for_x) {
        return stringResource(id = packPublicStatus.message) + "1 " + stringResource(id = R.string.minute)
    } else {
        return stringResource(id = packPublicStatus.message)
    }

}


@ExperimentalMaterialApi
@Composable
fun TaskSetting(settingModel: SettingModel, changeSettingModel: (SettingModel) -> Unit) {
    val changeModel by rememberUpdatedState(changeSettingModel)
    val sheetState by remember { mutableStateOf(BottomSheetVisibleState()) }

    Column {
        Text(
            stringResource(R.string.general).uppercase(),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                ListItem(
                    modifier = Modifier.clickable {
                        sheetState.isVoiceSheet.value = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.RecordVoiceOver,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    },
                    text = {
                        Text(
                            stringResource(id = R.string.features_voices),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                )

            }
        }

        Text(
            stringResource(R.string.tasks).uppercase(),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                checkItem(
                    settingModel.memorizeTask,
                    Icons.Filled.QuestionAnswer,
                    stringResource(id = R.string.task_MEMORIZE)
                ) {
                    changeModel(settingModel.copy(memorizeTask = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.quizTask,
                    Icons.Filled.AutoStories,
                    stringResource(id = R.string.task_QUIZ)
                ) {
                    changeModel(settingModel.copy(quizTask = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.listeningTask,
                    Icons.Filled.Headphones,
                    stringResource(id = R.string.task_LISTENING)
                ) {
                    changeModel(settingModel.copy(listeningTask = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.speakingTask,
                    Icons.Filled.Speaker,
                    stringResource(id = R.string.task_SPEAKING)
                ) {
                    changeModel(settingModel.copy(speakingTask = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.testTask,
                    Icons.Filled.NoteAlt,
                    stringResource(id = R.string.task_TEST)
                ) {
                    changeModel(settingModel.copy(testTask = it))
                }

            }
        }

        Text(
            stringResource(R.string.Einstellungen).uppercase(),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = LengoCaption().copy(textAlign = TextAlign.Left)
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                checkItem(
                    settingModel.pronounceEnable,
                    Icons.Filled.RecordVoiceOver,
                    stringResource(id = R.string.task_set_VOICE)
                ) {
                    changeModel(settingModel.copy(pronounceEnable = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.audioEnable,
                    Icons.Filled.SpeakerGroup,
                    stringResource(id = R.string.task_set_AUDIO)
                ) {
                    changeModel(settingModel.copy(audioEnable = it))
                }
                Divider(startIndent = 16.dp)
                checkItem(
                    settingModel.darkThemeEnable ?: false,
                    Icons.Filled.Lightbulb,
                    stringResource(id = R.string.task_set_DARKMODE)
                ) {
                    changeModel(settingModel.copy(darkThemeEnable = it))
                }
            }
        }

    }


    SubscriptionModelSheet(
        subscriptionSheetState = SubscriptionSheetState(sheetState.isFreeTrailSheet.value, null)
    ) {
        sheetState.isFreeTrailSheet.value = false
    }


    VoicesSelectSelectSheet(visible = sheetState.isVoiceSheet.value, openSubSheet = {
        sheetState.isFreeTrailSheet.value = true
    }, onDismiss = {
        sheetState.isVoiceSheet.value = false
    })
}


@ExperimentalMaterialApi
@Composable
fun checkItem(
    isChecked: Boolean,
    imageVector: ImageVector,
    text: String,
    isLoading: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {

    val isDarkTheme = LocalDarkModeEnable.current

    ListItem(
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colors.onBackground
            )
        },
        text = { Text(text, color = MaterialTheme.colors.onBackground) },
        trailing = {
            Row (verticalAlignment = Alignment.CenterVertically){
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Grey,
                        checkedTrackColor = MaterialTheme.colors.primary
                    )
                )
                AnimatedVisibility(visible = isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        },
        modifier = Modifier.toggleable(
            value = isChecked,
            onValueChange = onCheckedChange
        )
    )

}


@OptIn(ExperimentalMaterialApi::class)
@Preview(device = "spec:width=411dp,height=891dp")
@Composable
fun QuizSettingSheetPreview() {
    LENGOTheme {
        QuizSettingSheet(
            true,
            "lec name",
            null,
            "Pack Name", "", 1, USER_VOCAB, PackPublicStatus(), onPackStatusChange = {}, { a, b ->

            }, {

            }

        )
    }
}