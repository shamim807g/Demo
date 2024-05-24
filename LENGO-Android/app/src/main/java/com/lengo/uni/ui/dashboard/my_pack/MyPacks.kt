package com.lengo.uni.ui.dashboard.my_pack

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.PLACEHOLDER
import com.lengo.common.R
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.EmojiBox
import com.lengo.common.ui.ImageCard
import com.lengo.common.ui.ImageCard3
import com.lengo.common.ui.TextChip
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.BADGE
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.model.data.UserEditedPack
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.bottomsheet.EmojiModelSheet
import com.lengo.common.ui.theme.LengoCaption
import com.lengo.uni.ui.bottomsheet.LangSelectSheet
import logcat.logcat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyPacks() {
    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    val navigator = LocalNavigator.current
    val viewModel = activity.myPackViewModel
    var isLangSheet by remember { mutableStateOf(false) }
    val viewState by rememberFlowWithLifecycle(viewModel.uiState)
        .collectAsState(initial = MyPackViewState.Empty)

    DisposableEffect(key1 = Unit, effect = {
        viewModel.fetchUserCreatedPacks()
        viewModel.fetchEditedPacks()
        onDispose { }
    })

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                MyPackEvents.INSUFFICIENT_COIN -> {
                    navigator.navigate(Screen.Profile.route)
                }

                is MyPackEvents.OPEN_PACK -> {
                    navigator.navigate(Screen.PacksDetails.createRoute(it.pack))
                }

                is MyPackEvents.OPEN_LECTION -> {
                    navigator.navigate(Screen.WordList.createRoute(it.pack, it.lec))
                }

                MyPackEvents.LangUpdated -> {
                    //viewModel.fetchUserCreatedPacks()
                }
            }
        }
    }


    BoxWithConstraints {
        val scrollState = rememberScrollState()
        val boxWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() - 16.dp }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            VerticleSpace(16.dp)

            Packs(
                boxWidth,
                viewState.packs,
                viewState.imageMap,
                viewState.userCreatedPacks,
                addPackAndLection = {
                    logcat("IMAGE:") { "addPackAndLection" }
                    viewModel.insertUserPackAndLection { p, l ->
                        activity.mainViewModel.syncDataWithServer()
                        navigator.navigate(Screen.WordList.createRoute(p, "✏️", l))
                    }
                },
                addLection = { pack -> viewModel.insertLection(pack) },
                selectPack = { viewModel.processPack(it) },
                onLectionSelected = { pack, lection ->
                    viewModel.processPackWithLection(pack, lection)
                },
                goToPackDetails = { pack ->
                    navigator.navigate(Screen.PacksDetails.createRoute(pack))
                },
                goToLectionWords = { pack, lec ->
                    navigator.navigate(Screen.WordList.createRoute(pack, lec))
                },
            )

            VerticleSpace(120.dp)
        }

    }

    LangSelectSheet(isLangSheet) {
        isLangSheet = false
    }

}


@ExperimentalMaterialApi
@Composable
fun Packs(
    boxWidth: Dp = 0.dp,
    packs: List<UserEditedPack?>?,
    lecImages: SnapshotStateMap<String, String>,
    userCreatedPacks: List<UserEditedPack>,
    addPackAndLection: () -> Unit,
    addLection: (Pack) -> Unit,
    selectPack: (Pack) -> Unit,
    onLectionSelected: (Pack, Lection) -> Unit,
    goToPackDetails: (Pack) -> Unit,
    goToLectionWords: (Pack, Lection) -> Unit,
) {

    AddPack(R.string.create_list, addPackAndLection)

    VerticleSpace(16.dp)

    //AddPack(R.string.addVoc, addPack)

    userCreatedPacks.forEach { item ->
        VerticleSpace(16.dp)
        AddPackHeading(
            item.pack.title,
            "${item.userCount}/${item.totalCount}",
            item.pack.emoji) {
            goToPackDetails(item.pack)
        }
        VerticleSpace(8.dp)
        AddLections(item.pack.lections, lecImages, addLection = {
            addLection(item.pack)
        }, goToLectionWords = {
            goToLectionWords(item.pack, it)
        })
    }

    VerticleSpace(16.dp)

    packs?.forEach { userEditPack ->
        userEditPack?.let {
            PacksHorizontalList(
                boxWidth,
                it,
                lecImages,
                selectPack = selectPack,
                onLectionSelected = onLectionSelected
            )
        }
        VerticleSpace(16.dp)
    }

}


@ExperimentalMaterialApi
@Composable
fun AddLections(
    lections: List<Lection>,
    lecImages: SnapshotStateMap<String, String>,
    addLection: () -> Unit,
    goToLectionWords: (Lection) -> Unit,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        lections.forEach { lec ->

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(200.dp),
                Arrangement.spacedBy(4.dp)
            ) {
                ImageCard3(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    name = lec.title,
                    image = lecImages["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                        ?: PLACEHOLDER
                ) {
                    goToLectionWords(lec)
                }

                Text(
                    text = if(lec.title.isEmpty()) stringResource(id = R.string.addSubCat) else lec.title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        color = MaterialTheme.colors.onBackground,
                    )
                )

            }

        }
        AddLection(addLection)
    }
}

@ExperimentalMaterialApi
@Composable
fun AddLection(addLection: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(200.dp)
            .aspectRatio(16 / 9f),
        backgroundColor = MaterialTheme.colors.surface,
        onClick = addLection
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopStart),
                tint = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(id = R.string.addVoc),
                modifier = Modifier.align(Alignment.BottomStart),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable
fun AddPackHeading(
    title: String,
    score: String,
    emojiText: String,
    goToPackDetails: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()

    ) {
        EmojiBox(emojiText, 44.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = if(title.isEmpty()) stringResource(id = R.string.NewPck) else title,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground,
                )
            )

            Text(
                modifier = Modifier,
                text = score, style = LengoCaption().copy(fontSize = 14.sp)
                    .copy(color = MaterialTheme.colors.onBackground)
            )
        }
        TextChip(bagText = BADGE.OPEN) {
            goToPackDetails()
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun AddPack(title: Int, addPack: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(120.dp),
        backgroundColor = MaterialTheme.colors.surface,
        onClick = addPack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopStart),
                tint = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(id = title),
                modifier = Modifier.align(Alignment.BottomStart),
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun PacksHorizontalList(
    boxWidth: Dp = 0.dp,
    userEditedPack: UserEditedPack,
    lecImages: SnapshotStateMap<String, String>,
    selectPack: (Pack) -> Unit,
    onLectionSelected: (Pack, Lection) -> Unit,
) {
    Column {
        val hScrollState = rememberScrollState()

        PackItemEdited(
            modifier = Modifier
                .width(boxWidth)
                .padding(horizontal = 16.dp),
            userEditedPack.pack?.title ?: "",
            "${userEditedPack.userCount}/${userEditedPack.totalCount}",
            emojiText = userEditedPack.pack.emoji,
            coins = userEditedPack.pack!!.coins,
            bagText = userEditedPack.pack!!.badge
        ) {
            selectPack(userEditedPack.pack!!)
        }

        VerticleSpace(8.dp)

        if (!userEditedPack.pack.lections.isNullOrEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(hScrollState)
                    .padding(horizontal = 8.dp)
            ) {
                userEditedPack.pack.lections.forEach { lec ->
                    ImageCard(
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(16 / 9f),
                        name = lec.title,
                        image = lecImages["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                            ?: PLACEHOLDER
                    ) {
                        onLectionSelected(userEditedPack.pack!!, lec)
                    }
                }
            }
        }
    }

}

@Composable
fun PackItemEdited(
    modifier: Modifier = Modifier,
    heading: String = "Timeasdadadasdadasdadsads adasdasdasdasdsadasdad",
    subHeading: String = "Everasdasdasddasdasdyday Life",
    bagText: BADGE = BADGE.NONE,
    coins: Int = 9,
    emojiText: String = "",
    onClick: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        EmojiBox(emojiText, 44.dp)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier,
                text = heading, style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground
                )
            )
            Text(
                modifier = Modifier,
                text = subHeading, style = LengoCaption().copy(fontSize = 14.sp)
                    .copy(color = MaterialTheme.colors.onBackground)
            )
        }
        TextChip(coins = coins, bagText = bagText) {
            onClick(heading)
        }
    }

}