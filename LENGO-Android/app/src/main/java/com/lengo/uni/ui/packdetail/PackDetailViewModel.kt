package com.lengo.uni.ui.packdetail

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import com.lengo.common.MoleculeViewModel
import com.lengo.data.repository.ImageRepository
import com.lengo.data.repository.LectionRepository
import com.lengo.model.data.Lection
import com.lengo.model.data.PackId
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@Stable
data class PackDetailViewState(
    val packName: String = "",
    val imageMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val lection: List<Lection> = emptyList()
) {
    companion object {
        val Empty = PackDetailViewState()
    }
}

sealed interface PackDetailEvent {
    object LoadPack : PackDetailEvent
}

@HiltViewModel
class PackDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val lectionRepository: LectionRepository,
    private val imageRepository: ImageRepository
) : MoleculeViewModel<PackDetailEvent, PackDetailViewState>() {

    private val packName: String = URLDecoder.decode(
        savedStateHandle["name"]!!,
        StandardCharsets.UTF_8.toString()
    )
    private val packId: Long = savedStateHandle.get<String>("pck")!!.toLong()
    private val owner: Long = savedStateHandle.get<String>("owner")!!.toLong()
    private val type: String = savedStateHandle.get("type")!!
    private val lang: String = savedStateHandle.get("lang")!!

    @Composable
    override fun models(events: Flow<PackDetailEvent>): PackDetailViewState {
        val lections by lectionRepository.observeLections(PackId(packName, packId, owner, type, lang))
            .collectAsState(initial = emptyList())

        return PackDetailViewState(
            imageMap = imageRepository.lecImage,
            lection = lections,
            packName = packName
        )
    }


    override fun onCleared() {
        Log.d("PACK DETAIL VIEWMODEL", "onCleared: ")
        super.onCleared()
    }

}
