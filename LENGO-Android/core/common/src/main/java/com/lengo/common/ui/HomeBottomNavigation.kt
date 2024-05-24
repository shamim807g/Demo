package com.lengo.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.R
import com.lengo.common.ui.theme.translucentBarAlpha
import com.lengo.model.data.BottomNavScreen

@Composable
fun HomeBottomNavigation(
    selectedNavigation: String,
    onNavigationSelected: (BottomNavScreen) -> Unit,
    modifier: Modifier = Modifier
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
                selected = selectedNavigation == BottomNavScreen.Discover.route,
                onClick = { onNavigationSelected(BottomNavScreen.Discover) },
                drawId = R.drawable.ic_tabbar_1
            )

            HomeBottomNavigationItem(
                testtag = "mypacks",
                label = stringResource(R.string.meinePacks),
                selected = selectedNavigation == BottomNavScreen.MyPacks.route,
                onClick = { onNavigationSelected(BottomNavScreen.MyPacks) },
                drawId = R.drawable.ic_tabbar_2
            )

            HomeBottomNavigationItem(
                testtag = "words",
                label = stringResource(R.string.WordsTLocal),
                selected = selectedNavigation == BottomNavScreen.Words.route,
                onClick = { onNavigationSelected(BottomNavScreen.Words) },
                drawId = R.drawable.ic_tabbar_3
            )

            HomeBottomNavigationItem(
                testtag = "progress",
                label = stringResource(R.string.progress_tab),
                selected = selectedNavigation == BottomNavScreen.Progress.route,
                onClick = { onNavigationSelected(BottomNavScreen.Progress) },
                drawId = R.drawable.ic_tabbar_4
            )
        }
    }
}

@Composable
fun RowScope.HomeBottomNavigationItem(
    testtag: String,
    selected: Boolean,
    drawId: Int,
    label: String,
    onClick: () -> Unit,
) {
    BottomNavigationItem(
        icon = {
            Icon(
                modifier = Modifier.size(22.dp),
                tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                painter = painterResource(drawId),
                contentDescription = null
            )
        },
        label = {
            AutoResizeText(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSizeRange = FontSizeRange(
                    min = 10.sp,
                    max = 12.sp,),)
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.testTag(testtag)
    )
}


@Composable
fun HomeNavigationRailItemItem(
    testtag: String,
    selected: Boolean,
    drawId: Int,
    label: String,
    onClick: () -> Unit,
) {
    NavigationRailItem(
        icon = {
            Icon(
                modifier = Modifier.size(22.dp),
                tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                painter = painterResource(drawId),
                contentDescription = null
            )
        },
        label = {
            AutoResizeText(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSizeRange = FontSizeRange(
                    min = 10.sp,
                    max = 12.sp,),)
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.testTag(testtag)
    )
}