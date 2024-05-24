package com.lengo.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.lengo.common.IMAGE_LOADING_ERROR
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LengoSubHeading2
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.placeHolderDarkGradient
import com.lengo.common.ui.theme.placeHolderGradient
import java.io.File


// Small
//.fillMaxWidth(0.8f)
//.aspectRatio(4 / 3f)

//Large
//.fillMaxWidth()
//.aspectRatio(16 / 9f)


@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    name: String = "Clock 1",
    image: String = "https://picsum.photos/300/300",
    onLectionClicked: () -> Unit = {}
) {

    val isDarkTheme = LocalDarkModeEnable.current

    Column(modifier = Modifier.padding(horizontal = 8.dp), Arrangement.spacedBy(4.dp)) {
        Box {
            Card(
                onClick = onLectionClicked,
                shape = RoundedCornerShape(8.dp),
                elevation = 8.dp,
                modifier = modifier
            ) {
                val file = remember(key1 = image) { File(image) }
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(if (image.contains(IMAGE_LOADING_ERROR)) image.split(":")[1].toInt() else file)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                ) {
                    val state = painter.state
                    when (state) {
                        AsyncImagePainter.State.Empty,
                        is AsyncImagePainter.State.Loading,
                        is AsyncImagePainter.State.Error -> {
                            if (!image.contains(IMAGE_LOADING_ERROR)) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDarkTheme) placeHolderDarkGradient else placeHolderGradient)
                                )
                            }
                        }
                        is AsyncImagePainter.State.Success -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }

        }
        Text(
            modifier = Modifier,
            text = name, style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onBackground
            )
        )
    }
}

@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun ImageCard3(
    modifier: Modifier = Modifier,
    name: String = "Clock 1",
    image: String = "https://picsum.photos/300/300",
    onLectionClicked: () -> Unit = {}
) {

    val isDarkTheme = LocalDarkModeEnable.current

    Box {
        Card(
            onClick = onLectionClicked,
            shape = RoundedCornerShape(8.dp),
            elevation = 1.dp,
            modifier = modifier
        ) {

            val file = remember(key1 = image) { File(image) }
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(if (image.contains(IMAGE_LOADING_ERROR)) image.split(":")[1].toInt() else file)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (painter.state) {
                    AsyncImagePainter.State.Empty,
                    is AsyncImagePainter.State.Loading,
                    is AsyncImagePainter.State.Error -> {
                        if (!image.contains(IMAGE_LOADING_ERROR)) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDarkTheme) placeHolderDarkGradient else placeHolderGradient)
                            )
                        }
                    }
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

        }

    }

}

@ExperimentalMaterialApi
@Composable
fun ImageCard2(
    image: String = "https://picsum.photos/300/300",
    name: String = "Clock 1",
    onLectionClicked: () -> Unit = {}
) {
    val isDarkTheme = LocalDarkModeEnable.current

    Card(
        onClick = onLectionClicked,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(360.dp)
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Box(Modifier.fillMaxSize()) {
            val file = remember(key1 = image) {
                File(image)
            }
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(if (image.contains(IMAGE_LOADING_ERROR)) image.split(":")[1].toInt() else file)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            ) {
                when (painter.state) {
                    AsyncImagePainter.State.Empty,
                    is AsyncImagePainter.State.Loading,
                    is AsyncImagePainter.State.Error -> {
                        if (!image.contains(IMAGE_LOADING_ERROR)) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDarkTheme) placeHolderDarkGradient else placeHolderGradient)
                            )
                        }
                    }
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) MaterialTheme.colors.surface else Color.White)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                text = name, style = LengoSubHeading2()
                    .copy(color = MaterialTheme.colors.onBackground)
            )
        }
    }
}


@ExperimentalMaterialApi
@Preview
@Composable
fun ImageCardDemo() {
    LENGOTheme {
        ImageCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f), name = "adadasd"
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun ImageCardDemo2() {
    LENGOTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageCard2()
        }
    }
}