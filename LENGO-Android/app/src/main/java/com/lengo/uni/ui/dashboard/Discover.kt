package com.lengo.uni.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lengo.common.PLACEHOLDER
import com.lengo.common.R
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.ui.HeadingCoin
import com.lengo.common.ui.HeadingSeAll
import com.lengo.common.ui.PackItem
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.Screen
import com.lengo.uni.ui.MainActivity
import com.lengo.common.ui.ImageCard
import com.lengo.common.ui.TextChip
import com.lengo.common.ui.VerticleSpace
import com.lengo.model.data.BADGE
import com.lengo.model.data.network.Recommendedresources
import com.lengo.uni.ui.sheet.DashboardReviewSheet
import com.lengo.uni.ui.sheet.SubscriptionModelSheet
import com.lengo.uni.ui.sheet.SubscriptionSheetState
import com.lengo.common.ui.theme.Alpha80Black
import com.lengo.common.ui.theme.LengoBold20
import com.lengo.common.ui.theme.LengoHeading2
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.placeHolderGradient

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Discover(windowSize: WindowWidthSizeClass) {
    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    val navigator = LocalNavigator.current
    val viewModel = activity.discoverViewModel
    val state: DiscoverViewState by viewModel.uiState.collectAsState()
    var isFreeTrailSheet by remember { mutableStateOf(false) }
    var isDashboardReviewSheet by remember { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LaunchedEffect(Unit) {
        if(!activity.isLaunchSync) {
            activity.mainViewModel.syncDataWithServer()
            activity.isLaunchSync = true
        }

        viewModel.event.collect {
            when (it) {
                is DasboardEvents.INSUFFICIENT_COIN -> {
                    isFreeTrailSheet = true
                }
                is DasboardEvents.OPEN_PACK -> {
                    navigator.navigate(Screen.PacksDetails.createRoute(it.pack))
                    viewModel.checkValidReviewState()
                }
                is DasboardEvents.OPEN_LECTION -> {
                    navigator.navigate(Screen.WordList.createRoute(it.pack,it.lec))
                    viewModel.checkValidReviewState()
                }
                DasboardEvents.OPEN_REVIEW -> {
                    isDashboardReviewSheet = true
                }
            }
        }
    }

    if (state.packs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .testTag("dash_pack")
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 50.dp)
            ) {
                for ((categoryName, packs) in state.packs) {
                    val firstPack = packs.firstOrNull()
                    if (firstPack != null) {
                        item {
                            HeadingCoin(firstPack.title, firstPack.coins, firstPack.badge) {
                                viewModel.processPack(firstPack)
                            }
                        }
                        item {
                            LazyRow(modifier = Modifier.testTag("first_item_${categoryName}"),contentPadding = PaddingValues(horizontal = 8.dp)) {
                                items(firstPack.lections, key = { lec ->
                                    "${lec.title}${lec.lang}${lec.lec}${lec.owner}${lec.pck}${lec.type}"
                                }) { lec ->
                                    ImageCard(
                                        modifier = Modifier
                                            .width(if(windowSize == WindowWidthSizeClass.Compact) screenWidth.dp.minus(32.dp) else 480.dp.minus(32.dp))
                                            .aspectRatio(4 / 2.5f)
                                            .testTag("card_item"),
                                        name = lec.title,
                                        image = appState.imageMap["${lec.lang}${lec.type}${lec.lec}${lec.owner}${lec.pck}"]
                                            ?: PLACEHOLDER,
                                    ) {
                                        viewModel.processPackWithLection(firstPack, lec)
                                    }
                                }
                            }
                        }

                        item {
                            Divider(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            )
                        }

                        item {
                            HeadingSeAll(
                                when(categoryName) {
                                    SYS_VOCAB -> stringResource(R.string.Vokabeln)
                                    SYS_GRAMMER -> stringResource(R.string.Grammatik)
                                    else -> { "Community Packs" }
                                }
                            ) {
                                navigator.navigate(Screen.CategoryDetails.createRoute(categoryName))
                            }
                        }
                        val items = packs.chunked(3)
                        item {
                            LazyRow(modifier = Modifier.padding(horizontal = 8.dp)) {
                                items(items) { pack ->
                                    Column {
                                        pack.forEach { entry ->
                                            PackItem(
                                                modifier = Modifier
                                                    .width(if(windowSize == WindowWidthSizeClass.Compact) screenWidth.dp.minus(16.dp) else 480.dp.minus(16.dp))
                                                    .padding(horizontal = 8.dp),
                                                entry.title,
                                                entry.lections.map { it.title }
                                                    .joinToString { it },
                                                emojiText = entry.emoji,
                                                coins = entry.coins,
                                                bagText = entry.badge,
                                            ) {
                                                viewModel.processPack(entry)
                                            }
                                            VerticleSpace()
                                        }

                                    }
                                }
                            }
                        }

                        item {
                            Divider(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            )
                        }

                    }
                }

                item {
                    if (state.recommendation.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            text = stringResource(id = com.lengo.common.R.string.recommended_resources),
                            style = LengoHeading2()
                                .copy(color = MaterialTheme.colors.onBackground)
                        )
                        VerticleSpace()
                        RecommededResource(state.recommendation) { url, id ->
                            navigator.navigate(Screen.WebPage.createRoute(url))
                            viewModel.referralSession(id)
                        }
                        VerticleSpace()
                        Divider(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            )
                        )
                    }
                }

            }

            if (appState.settingModel.isUnlockCardVisible) {
                UnlockAll(unlockAll = {
                    isFreeTrailSheet = true
                }, onCancel = {
                    activity.mainViewModel.UpdateSettingModel(
                        appState.settingModel.copy(
                            isUnlockCardVisible = false
                        )
                    )
                })
            }
        }

    }


    SubscriptionModelSheet(subscriptionSheetState = SubscriptionSheetState(isFreeTrailSheet,null)
    ) {
        isFreeTrailSheet = false
    }

//    FreeTrailSheet(isFreeTrailSheet) {
//        isFreeTrailSheet = false
//    }

    DashboardReviewSheet(isDashboardReviewSheet) {
        isDashboardReviewSheet = false
    }


}

@Composable
fun RecommededResource(
    recommendation: List<Recommendedresources>,
    goToWebPage: (String, Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        recommendation.forEach {
            RecommededBox(imageUrl = it.image_location, it.title) {
                goToWebPage(it.dest_link, it.id)
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoxScope.UnlockAll(unlockAll: () -> Unit, onCancel: () -> Unit) {

    Card(
        onClick = unlockAll,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(60.dp)
            .align(Alignment.BottomCenter)
    ) {
        Row(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "unlock cancel button",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCancel() },
                tint = MaterialTheme.colors.onBackground
            )

            Text(
                stringResource(id = R.string.lockoutAll),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                style = LengoBold20(), modifier = Modifier
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecommededBox(
    imageUrl: String = "https://picsum.photos/300/300",
    label: String = "Some Test long Test Some Test long",
    onClick: () -> Unit
) {

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
            crossfade(true)
        }).build()
    )

    Box(
        modifier = Modifier
            .width(300.dp)
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(8.dp))
    ) {

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp,
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            when (painter.state) {
                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Error,
                -> {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(placeHolderGradient)
                    )
                }
                else -> {}
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .background(Alpha80Black)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(1f),
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Left
            )

            TextChip(
                color = Color.White,
                onClick = onClick,
                bagText = BADGE.OPEN
            )

        }


    }

}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true ,device = Devices.NEXUS_10)
@Composable
fun RowLayout() {
    val windowSize = WindowWidthSizeClass.Expanded
    CompositionLocalProvider(LocalDarkModeEnable provides false) {

        val screenWidth = LocalConfiguration.current.screenWidthDp
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 50.dp)
        ) {
            item {
                HeadingCoin("firstPack.title", 33, BADGE.COIN) {

                }
            }
            item {
                LazyRow(modifier = Modifier, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    items(
                        listOf(Color.Red, Color.Blue, Color.Yellow), key = { lec -> "${lec}" }) { lec ->
                        ImageCard(
                            modifier = Modifier
                                .width(if(windowSize == WindowWidthSizeClass.Compact) screenWidth.dp.minus(32.dp) else 480.dp.minus(32.dp))
                                .aspectRatio(4 / 2.5f)
                                .testTag("card_item"),
                            name = "${lec}",
                            image = PLACEHOLDER,
                        ) {

                        }
                    }
                }
            }

        }

    }


}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun RowLayout2() {

    val screenWidth = LocalConfiguration.current.screenWidthDp
    CompositionLocalProvider(LocalDarkModeEnable provides false) {
        Box(
            Modifier
                .fillMaxSize()
                .testTag("dash_pack")
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 50.dp)
            ) {
                item {
                    HeadingCoin("firstPack.title", 33, BADGE.COIN) {

                    }
                }
                item {
                    LazyRow(modifier = Modifier, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(
                            listOf(Color.Red, Color.Blue, Color.Yellow), key = { lec -> "${lec}" }) { lec ->
                            ImageCard(
                                modifier = Modifier
                                    .width(screenWidth.dp.minus(32.dp))
                                    .aspectRatio(4 / 2.5f)
                                    .testTag("card_item"),
                                name = "${lec}",
                                image = PLACEHOLDER,
                            ) {

                            }
                        }
                    }
                }

            }

        }

    }


}