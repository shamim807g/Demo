package com.lengo.uni.ui.wordlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.extension.toAnnotatedString
import com.lengo.common.ui.ChildAppBar
import com.lengo.common.ui.LengoButton
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.ui.WordItem
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.bottomsheet.QuizSettingSheet
import com.lengo.common.ui.AutoResizeText
import com.lengo.common.ui.FontSizeRange
import com.lengo.uni.ui.sheet.DownloadLangModelSheet
import com.lengo.common.ui.theme.LengoHeading2
import com.lengo.common.ui.theme.LengoRegular18h4
import com.lengo.model.data.quiz.Word
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.bottomsheet.UseWordUpdateSheet
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun WordList() {
    val activity = LocalContext.current as MainActivity
    val controller = LocalNavigator.current
    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<WordListViewModel>()
    val wordListViewState: WordListViewState by viewModel.uiState.collectAsState()
    var isQuizSetting by remember { mutableStateOf(false) }
    var isLangDownload by remember { mutableStateOf(false) }
    var selectedWord: Word? by remember { mutableStateOf(null) }
    var isWordUpdateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, block = {
        delay(400)
        viewModel.fetchWords()
        viewModel.wordListEvent.collect {
            when (it) {
                WordListEvents.LangDownload -> {
                    isLangDownload = true
                }
            }
        }
    })


    Column(Modifier.fillMaxSize()) {
        ChildAppBar(
            title = wordListViewState.packName,
            onBack = { controller.popBackStack() },
            onSetting = { isQuizSetting = true },
            settingIconVisible = true
        )

        AutoResizeText(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = wordListViewState.lectionName,
            maxLines = 1,
            fontSizeRange = FontSizeRange(
                min = 23.sp,
                max = 24.sp,
            ),
            style = LengoHeading2().copy(color = MaterialTheme.colors.onBackground),
        )

        if (wordListViewState.isUserVock) {
            AddWord(
                wordListViewState.ownWord,
                wordListViewState.ownLabel,
                wordListViewState.selWord,
                wordListViewState.selLabel,
                wordListViewState.addButtonEnable,
                onOwnWordChange = viewModel::ownWordChange,
                onSelWordChange = viewModel::selWordChange, onAddWord = {
                    viewModel.addWord { activity.mainViewModel.syncDataWithServer() }
                }
            )
        }

        if (!wordListViewState.words.isNullOrEmpty()) {
            LazyColumn(Modifier.weight(1f)) {
                if (!wordListViewState.lection?.explanation.isNullOrEmpty()) {
                    item {
                        val deviceLang =
                            if (wordListViewState.ownLang == "us") "en" else wordListViewState.ownLang
                        val text = wordListViewState.lection?.explanation?.getOrDefault(
                            deviceLang,
                            null
                        )
                        if (!text.isNullOrEmpty()) {
                            val annotText = text.toAnnotatedString(MaterialTheme.colors.primary)
                            if (annotText != null) {
                                Text(
                                    text = annotText,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                        .fillMaxWidth(),
                                    style = MaterialTheme.typography.h6.copy(
                                        color = MaterialTheme.colors.onBackground,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                                Divider(startIndent = 8.dp)
                                VerticleSpace()
                            }
                        }
                    }
                }

                if (!wordListViewState.lection?.example.isNullOrEmpty()) {
                    items(items = wordListViewState.lection?.example!!) { word ->
                        val text = word.toAnnotatedString(MaterialTheme.colors.primary)
                        if (!text.isNullOrEmpty()) {
                            Text(
                                text = text,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.h5
                                    .copy(color = MaterialTheme.colors.onBackground)
                            )
                            VerticleSpace()
                            Divider(startIndent = 8.dp)
                        }
                    }
                }

                if (!wordListViewState.words.isNullOrEmpty()) {
                    items(items = wordListViewState.words!!, key = { item ->
                        "${item.obj}${item.lec}${item.pck}${item.owner}"
                    }) { obj ->
                        WordItem(
                            obj.deviceLngWord,
                            obj.selectedLngWord,
                            obj.selectedLngWordTransliterator,
                            obj.isChecked,
                            obj.isGram,
                            obj.color,
                            onCheckChanged = {
                                viewModel.onWordSeleted(obj)
                            },
                            onItemClicked = {
                                selectedWord = obj
                                isWordUpdateSheet = true
                            },
                            onSpeak = { text, isAdded -> viewModel.onSpeak(obj, text, isAdded) }
                        )
                        VerticleSpace()
                    }
                }
            }
        } else if (wordListViewState.words == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize())
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
                    .padding(16.dp),
                enabled = wordListViewState.isQuizButtonEnable
            ) {
                viewModel.onPrepareQuiz()
                wordListViewState.lection?.let {
                    controller.navigate(
                        Screen.Quiz.createRoute(
                            0,
                            it.title,
                            wordListViewState.packName,
                            it.pck,
                            it.lec,
                            it.owner,
                            it.type,
                            it.lang
                        )
                    )
                }
            }
        }

    }


    QuizSettingSheet(isQuizSetting,
        wordListViewState.lectionName,
        wordListViewState.lection,
        wordListViewState.packName,
        wordListViewState.pacKEmoji,
        wordListViewState.packId,
        wordListViewState.packType,
        wordListViewState.packPublic,
        onPackStatusChange = { status ->
            viewModel.updatePackPublicStatus(status)
        },
        onPackLecNameChange = { pck, lec ->
            viewModel.updatedPackLecName(pck, lec)
        }) {
        isQuizSetting = false
    }

    DownloadLangModelSheet(isLangDownload) {
        isLangDownload = false
    }

    UseWordUpdateSheet(isWordUpdateSheet,
        selectedWord?.deviceLngWord ?: "",
        selectedWord?.selectedLngWord ?: "",
        wordListViewState.ownLabel,
        wordListViewState.selLabel,
        onUpdateWords = { d, s ->

        }) {
        isWordUpdateSheet = false
    }

}

@ExperimentalComposeUiApi
@Composable
fun AddWord(
    ownWord: String,
    ownLabel: String,
    selWord: String,
    selLabel: String,
    isAddEnable: Boolean,
    onOwnWordChange: (String) -> Unit,
    onSelWordChange: (String) -> Unit,
    onAddWord: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = ownWord,
        onValueChange = onOwnWordChange,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onBackground,
            backgroundColor = Color.Transparent,
            unfocusedIndicatorColor = MaterialTheme.colors.surface
        ),
        maxLines = 1,
        placeholder = {
            Text(
                text = ownLabel,
                style = LengoRegular18h4().copy(
                    color = MaterialTheme.colors.secondary,
                    textAlign = TextAlign.Start,
                )
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        textStyle = LengoRegular18h4().copy(
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Start,
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )

    TextField(
        value = selWord,
        onValueChange = onSelWordChange,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onBackground,
            backgroundColor = Color.Transparent,
            unfocusedIndicatorColor = MaterialTheme.colors.surface
        ),
        maxLines = 1,
        placeholder = {
            Text(
                text = selLabel,
                style = LengoRegular18h4().copy(
                    color = MaterialTheme.colors.secondary,
                    textAlign = TextAlign.Start,
                )
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        textStyle = LengoRegular18h4().copy(
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Start,
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    )

    LengoButton(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), enabled = isAddEnable,
        text = stringResource(id = R.string.addVoc)
    ) {
        onAddWord()
    }

}