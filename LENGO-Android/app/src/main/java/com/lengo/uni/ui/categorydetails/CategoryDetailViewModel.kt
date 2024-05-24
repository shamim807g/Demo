package com.lengo.uni.ui.categorydetails

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lengo.common.MoleculeViewModel
import com.lengo.common.R
import com.lengo.common.di.ApplicationScope
import com.lengo.data.repository.ImageRepository
import com.lengo.data.repository.PacksRepository
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject


@Immutable
sealed class CategoryEvents {
    object INSUFFICIENT_COIN : CategoryEvents()
    data class OPEN_PACK(val pack: Pack) : CategoryEvents()
    data class OPEN_LECTION(val pack: Pack,val lec: Lection) : CategoryEvents()
}

@Immutable
sealed class CategoryAction {
    data class processPack(val pack: Pack) : CategoryAction()
    data class processPackWithLection(val pack: Pack,val lection: Lection) : CategoryAction()
}

@Stable
data class CategoryDetailViewState(
    val categoryName: Int = R.string.Vokabeln,
    val imageMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val packs: List<Pack> = emptyList()
) {
    companion object {
        val Empty = CategoryDetailViewState()
    }
}


@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
    private val packsRepository: PacksRepository,
    private val imageRepository: ImageRepository
) : MoleculeViewModel<CategoryAction, CategoryDetailViewState>() {

    private var categoryName: String = URLDecoder.decode(
        savedStateHandle["categoryName"]!!,
        StandardCharsets.UTF_8.toString()
    )
    val categoryChannel = Channel<CategoryEvents>()
    val event = categoryChannel.receiveAsFlow()

    @Composable
    override fun models(events: Flow<CategoryAction>): CategoryDetailViewState {
        var packs: List<Pack> by remember { mutableStateOf(emptyList()) }

        LaunchedEffect(Unit) {
            delay(400)
            packsRepository.observePacksForCategory(categoryName).collect { pack ->
                packs = pack
            }
        }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is CategoryAction.processPack -> processPack(event.pack)
                    is CategoryAction.processPackWithLection -> processPackWithLection(event.pack, event.lection)
                }
            }
        }

        return CategoryDetailViewState(
            packs = packs,
            categoryName = if (categoryName.contains("Vok")) R.string.Vokabeln
            else R.string.Grammatik, imageMap = imageRepository.lecImage
        )
    }

     private fun processPack(pack: Pack) {
        appScope.launch {
            when (val result = packsRepository.processPack(pack)) {
                PacksRepository.PackOrLectionStatus.InsufficientCoin ->
                    categoryChannel.send(CategoryEvents.INSUFFICIENT_COIN)
                is PacksRepository.PackOrLectionStatus.LectionOpen ->
                    categoryChannel.send(CategoryEvents.OPEN_LECTION(pack, result.lec))
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete ->
                    categoryChannel.send(CategoryEvents.OPEN_LECTION(pack, result.lec))
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete ->
                    categoryChannel.send(CategoryEvents.OPEN_PACK(result.pack))
                is PacksRepository.PackOrLectionStatus.PurchaseOpen ->
                    categoryChannel.send(CategoryEvents.OPEN_PACK(result.pack))
                PacksRepository.PackOrLectionStatus.Default -> {

                }
            }
        }
    }

     private fun processPackWithLection(pack: Pack, lection: Lection) {
        viewModelScope.launch {
            when (val result = packsRepository.processPackAndLection(pack, lection)) {
                PacksRepository.PackOrLectionStatus.InsufficientCoin -> categoryChannel.send(
                    CategoryEvents.INSUFFICIENT_COIN
                )
                is PacksRepository.PackOrLectionStatus.LectionOpen -> categoryChannel.send(
                    CategoryEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete -> categoryChannel.send(
                    CategoryEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete -> categoryChannel.send(
                    CategoryEvents.OPEN_PACK(result.pack)
                )
                is PacksRepository.PackOrLectionStatus.PurchaseOpen -> categoryChannel.send(
                    CategoryEvents.OPEN_PACK(result.pack)
                )
                PacksRepository.PackOrLectionStatus.Default -> {}
            }

        }
    }


}