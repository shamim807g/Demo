package com.lengo.uni.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lengo.common.R
import com.lengo.uni.ui.LocalAppState
import com.lengo.uni.ui.LocalNavigator
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.MainViewState
import com.lengo.uni.ui.Screen
import com.lengo.common.ui.DashboardAppBar
import com.lengo.uni.ui.dashboard.my_pack.MyPacks
import com.lengo.uni.ui.dashboard.my_word.MyWords
import com.lengo.uni.ui.dashboard.progress.Progress
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.uni.ui.bottomsheet.LangSelectSheet


@Composable
fun Dashboard(currentRoute: String, windowSize: WindowWidthSizeClass) {

    val activity = LocalContext.current as MainActivity
    val appState = LocalAppState.current
    var isLangSheet by remember { mutableStateOf(false) }


    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            BottomNavigation(currentRoute, appState,{
                isLangSheet = it
            }) {
                activity.mainViewModel.updateMenuScreen(it)
            }
        }

        WindowWidthSizeClass.Medium , WindowWidthSizeClass.Expanded  -> {
            NavigationRail(currentRoute, appState,{
                isLangSheet = it
            }) {
                activity.mainViewModel.updateMenuScreen(it)
            }
        }
    }

    LangSelectSheet(isLangSheet) {
        isLangSheet = false
    }

}

@Composable
private fun BottomNavigation(
    currentRoute: String,
    appState: MainViewState,
    onLangSheetChange: (Boolean) -> Unit,
    onChangeMenu: (String) -> Unit
) {
    val navigator = LocalNavigator.current

    Column {

        DashboardAppBar(
            title = getTitle(currentRoute),
            appState.userSelectedLang?.drawable,
            onLangSelectionClicked = { onLangSheetChange(true) },
            goToProfile = {
                navigator.navigate(Screen.Profile.route)
            }
        )

        MenuNavigation(currentRoute)

        Menu.BottonNavMenu(
            currentRoute = currentRoute,
            onMenuItemClicked = { menuItem ->
                onChangeMenu(menuItem.toString())
            })
    }
}


@Composable
fun ColumnScope.MenuNavigation(currentRoute: String) {
    Box(modifier = Modifier
        .weight(1f)
        .wrapContentSize(Alignment.TopStart)) {

        AnimatedContent(targetState = currentRoute, label = "", transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        }) { currentRoute ->
            when(currentRoute) {
                MenuItem.Discover.route ->  Discover(WindowWidthSizeClass.Compact)
                MenuItem.MyPacks.route ->  MyPacks()
                MenuItem.Words.route ->  MyWords()
                MenuItem.Progress.route ->  Progress()
                else -> Discover(WindowWidthSizeClass.Compact)
            }
        }
    }

}


@Composable
private fun NavigationRail(
    currentRoute: String,
    appState: MainViewState,
    onLangSheetChange: (Boolean) -> Unit,
    onChangeMenu: (String) -> Unit
) {
    val navigator = LocalNavigator.current
    val activity = LocalContext.current as MainActivity
    Column {

        DashboardAppBar(
            title = getTitle(currentRoute),
            appState.userSelectedLang?.drawable,
            onLangSelectionClicked = { onLangSheetChange(true) },
            goToProfile = {
                navigator.navigate(Screen.Profile.route)
            }
        )

        Row {

            Menu.NavigationRailContent(
                currentRoute = currentRoute,
                onMenuItemClicked = { menuItem ->
                    onChangeMenu(menuItem.toString())
                })

            Column(modifier = Modifier.fillMaxSize()) {
                MenuNavigation(currentRoute)
            }
        }
    }

}


@Composable
fun getTitle(value: String?): String {
    return when (value) {
        MenuItem.Discover.route -> stringResource(id = R.string.discover_tab)
        MenuItem.MyPacks.route -> stringResource(id = R.string.meinePacks)
        MenuItem.Progress.route -> stringResource(id = R.string.progress_tab)
        MenuItem.Words.route -> stringResource(id = R.string.WordsTLocal)
        else -> {
            stringResource(id = R.string.discover_tab)
        }
    }
}

@Preview
@Composable
fun DashboardPreview() {
    LENGOTheme {
        CompositionLocalProvider(LocalAppState provides MainViewState()) {
            CompositionLocalProvider(LocalDarkModeEnable provides false) {
                Dashboard(currentRoute = MenuItem.Discover.route, windowSize = WindowWidthSizeClass.Compact)
            }
        }
    }
}
