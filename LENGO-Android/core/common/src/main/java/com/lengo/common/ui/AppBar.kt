package com.lengo.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lengo.common.BuildConfig
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoTopBar

@Composable
fun DashboardAppBar(
    title: String = "Discover",
    flag: Int? = null,
    onLangSelectionClicked: () -> Unit = {},
    goToProfile: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colors.background,
        contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
        elevation = 4.dp,
        shape = RectangleShape,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars.add(WindowInsets(top = 56.dp, bottom = 56.dp)))
                .padding(horizontal = 8.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                if(BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) { onLangSelectionClicked() }
            }) {
                if(flag != null) {
                    Image(
                        painter = painterResource(id = flag),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                    )
                }
            }

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground,
                    style = LengoTopBar(), modifier = Modifier
                        .weight(1f)
                )

                IconButton(onClick = goToProfile, modifier = Modifier, content = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp),
                        //.background(Color.Blue),
                        tint = MaterialTheme.colors.onBackground
                    )
                })
            }

        }

    }


@Composable
fun ChildAppBar(
    title: String = "Dashboard",
    settingIconVisible: Boolean = false,
    onBack: () -> Unit = {},
    onSetting: () -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colors.background,
        contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
        elevation = 4.dp,
        shape = RectangleShape,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars.add(WindowInsets(top = 56.dp, bottom = 56.dp)))
                .padding(horizontal = 8.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack, modifier = Modifier.testTag("wordlist_back"), content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }

            })


            Text(
                text = title,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                style = LengoTopBar(), modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { if(settingIconVisible) { onSetting() } }, content = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).alpha(if (settingIconVisible) 1f else 0f),
                    tint = MaterialTheme.colors.onBackground
                )
            })

        }

    }

}


@Composable
fun SheetAppBar(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            style = LengoTopBar(), modifier = Modifier.align(Alignment.Center)
        )
        IconButton(onClick = onBack,modifier = Modifier.padding(horizontal = 16.dp)
            .align(Alignment.CenterEnd), content = {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "App bar cancel",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colors.onBackground,
            )
        })
    }
}


@Preview
@Composable
fun AppBarDemo() {
    LENGOTheme {
        DashboardAppBar()
    }
}


