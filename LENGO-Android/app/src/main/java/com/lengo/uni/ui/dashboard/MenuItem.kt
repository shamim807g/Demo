package com.lengo.uni.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lengo.common.R
import com.lengo.common.ui.HomeBottomNavigationItem
import com.lengo.common.ui.HomeNavigationRailItemItem
import com.lengo.common.ui.theme.translucentBarAlpha



sealed class MenuItem {
    @Immutable
    data object Discover : MenuItem() {
        val key: String
            get() = Discover.javaClass.simpleName
        val route: String
            get() = "Discover"

    }


    @Immutable
    data object MyPacks : MenuItem() {
        val key: String
            get() = MyPacks.javaClass.simpleName
        val route: String
            get() = "MyPacks"

    }

    @Immutable
    data object Words : MenuItem() {
        val key: String
            get() = Words.javaClass.simpleName
        val route: String
            get() = "Words"

    }


    @Immutable
    object Progress : MenuItem() {
        val key: String
            get() = Progress.javaClass.simpleName
        val route: String
            get() = "Progress"

        override fun toString() = "Progress"
    }

}

interface Menu {

    companion object {
        @Composable
        fun BottonNavMenu(
            modifier: Modifier = Modifier,
            currentRoute: String,
            onMenuItemClicked: (MenuItem) -> Unit
        ) {
            Surface(
                color = MaterialTheme.colors.background.copy(alpha = translucentBarAlpha()),
                contentColor = contentColorFor(MaterialTheme.colors.background),
                elevation = 8.dp,
                modifier = modifier
            ) {
                Row(
                    Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    HomeBottomNavigationItem(
                        testtag = "discover",
                        label = stringResource(R.string.discover_tab),
                        selected = currentRoute == MenuItem.Discover.route,
                        onClick = { onMenuItemClicked(MenuItem.Discover) },
                        drawId = R.drawable.ic_tabbar_1
                    )

                    HomeBottomNavigationItem(
                        testtag = "mypacks",
                        label = stringResource(R.string.meinePacks),
                        selected = currentRoute == MenuItem.MyPacks.route,
                        onClick = { onMenuItemClicked(MenuItem.MyPacks) },
                        drawId = R.drawable.ic_tabbar_2
                    )

                    HomeBottomNavigationItem(
                        testtag = "words",
                        label = stringResource(R.string.WordsTLocal),
                        selected = currentRoute == MenuItem.Words.route,
                        onClick = { onMenuItemClicked(MenuItem.Words) },
                        drawId = R.drawable.ic_tabbar_3
                    )

                    HomeBottomNavigationItem(
                        testtag = "progress",
                        label = stringResource(R.string.progress_tab),
                        selected = currentRoute == MenuItem.Progress.route,
                        onClick = { onMenuItemClicked(MenuItem.Progress) },
                        drawId = R.drawable.ic_tabbar_4
                    )
                }
            }
        }

        @Composable
        fun NavigationRailContent(
            modifier: Modifier = Modifier,
            currentRoute: String,
            onMenuItemClicked: (MenuItem) -> Unit
        ) {

            NavigationRail(
                containerColor = MaterialTheme.colors.background.copy(alpha = translucentBarAlpha()),
                contentColor = contentColorFor(MaterialTheme.colors.background),
                modifier = Modifier.padding(top = 4.dp).fillMaxHeight()
            ) {

                HomeNavigationRailItemItem(
                    testtag = "discover",
                    label = stringResource(R.string.discover_tab),
                    selected = currentRoute == MenuItem.Discover.route,
                    onClick = { onMenuItemClicked(MenuItem.Discover) },
                    drawId = R.drawable.ic_tabbar_1
                )

                HomeNavigationRailItemItem(
                    testtag = "mypacks",
                    label = stringResource(R.string.meinePacks),
                    selected = currentRoute == MenuItem.MyPacks.route,
                    onClick = { onMenuItemClicked(MenuItem.MyPacks) },
                    drawId = R.drawable.ic_tabbar_2
                )

                HomeNavigationRailItemItem(
                    testtag = "words",
                    label = stringResource(R.string.WordsTLocal),
                    selected = currentRoute == MenuItem.Words.route,
                    onClick = { onMenuItemClicked(MenuItem.Words) },
                    drawId = R.drawable.ic_tabbar_3
                )

                HomeNavigationRailItemItem(
                    testtag = "progress",
                    label = stringResource(R.string.progress_tab),
                    selected = currentRoute == MenuItem.Progress.route,
                    onClick = { onMenuItemClicked(MenuItem.Progress) },
                    drawId = R.drawable.ic_tabbar_4
                )

            }
        }
    }
}
