package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.extension.Coins
import com.lengo.common.ui.BuyTextChip
import com.lengo.common.ui.SheetAppBar
import com.lengo.common.ui.VerticleSpace
import com.lengo.common.R
import com.lengo.common.inAppList
import com.lengo.uni.ui.profile.Coin
import com.lengo.common.ui.theme.CoinBronze
import com.lengo.common.ui.theme.CoinGold
import com.lengo.common.ui.theme.CoinSilver
import com.lengo.common.ui.theme.LengoHeading4
import com.lengo.common.ui.theme.LengoSubHeading2

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BuyCoinSheet(coinType: Coins, onPurchase: (sku: String) -> Unit, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SheetAppBar(stringResource(id = R.string.my_coins), onBack)

        Box(Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MyCoins(coinType)

                InAppItems(onPurchase)

            }
        }

    }
}


@Composable
fun MyCoins(coinTypes: Coins = Coins(0, 0, 0)) {
    Column {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Coin(
                        72.dp,
                        CoinGold,
                        coinTypes.gold.toString(),
                        6.dp,
                        LengoHeading4().copy(fontWeight = FontWeight.W600, fontSize = 24.sp)
                    )
                    Coin(
                        72.dp,
                        CoinSilver,
                        coinTypes.silver.toString(),
                        6.dp,
                        LengoHeading4().copy(fontWeight = FontWeight.W600, fontSize = 24.sp)
                    )
                    Coin(
                        72.dp,
                        CoinBronze,
                        coinTypes.bronze.toString(),
                        6.dp,
                        LengoHeading4().copy(fontWeight = FontWeight.W600, fontSize = 24.sp)
                    )
                }

            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
private fun InAppItems(
    onPurchase: (sku: String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            inAppList.forEachIndexed { index, obj ->
                ListItem(
                    text = {
                        Text(
                            getTitle(obj.sku),
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    secondaryText = {
                        Text(
                            getDescription(obj.sku),
                            color = MaterialTheme.colors.secondary
                        )
                    },
                    trailing = {
                        Column(horizontalAlignment = Alignment.End) {
                            BuyTextChip { onPurchase(obj.sku) }
                            VerticleSpace(4.dp)
                            Text(
                                obj.price.value ?: "",
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.secondary
                            )
                        }
                    },
                    icon = {
                        val coin = getCoinString(sku = obj.sku)
                        Coin(
                            48.dp,
                            coin.first,
                            coin.second,
                            style = LengoSubHeading2()
                        )
                    },
                    modifier = Modifier.clickable { onPurchase(obj.sku) }
                )

//        if (index != skuList.size - 1) {
//            Divider(color = MaterialTheme.colors.surface, thickness = 2.dp)
//        }
            }
        }

    }
}

@Composable
fun getTitle(sku: String): String {
    return when(sku) {
        "coin.silver_20_new" ->  stringResource(id = R.string.s20SC)
        "coin.bronze_10_new" -> stringResource(id = R.string.s10BC)
        "coin.silver_1_new" -> stringResource(id = R.string.s1SC)
        "coin.bronze_50_new" -> stringResource(id = R.string.s50BC)
        else -> { "" }
    }
}

@Composable
fun getCoinString(sku: String): Pair<Color,String> {
    return when(sku) {
        "coin.silver_20_new" ->  Pair(CoinSilver,"20")
        "coin.bronze_10_new" -> Pair(CoinBronze,"10")
        "coin.silver_1_new" -> Pair(CoinSilver,"1")
        "coin.bronze_50_new" -> Pair(CoinBronze,"50")
        else -> { Pair(CoinSilver,"") }
    }
}

@Composable
fun getDescription(sku: String): String {
    return when(sku) {
        "coin.silver_20_new" ->  "= 2000 "+ stringResource(id = R.string.coins)
        "coin.bronze_10_new" -> "= 10 "+stringResource(id = R.string.coins)
        "coin.silver_1_new" -> "= 100 "+stringResource(id = R.string.coins)
        "coin.bronze_50_new" -> "= 50 "+stringResource(id = R.string.coins)
        else -> { "" }
    }
}