package com.lengo.uni.ui.dashboard.my_pack

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lengo.data.repository.ImageRepository
import com.lengo.data.repository.LanguageRepository
import com.lengo.data.repository.LectionRepository
import com.lengo.data.repository.PacksRepository
import com.lengo.data.repository.UserRepository
import com.lengo.model.data.Lang
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.model.data.UserEditedPack
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@Stable
data class MyPackViewState(
    val packs: List<UserEditedPack?>? = null,
    val userCreatedPacks: List<UserEditedPack> = emptyList(),
    val imageMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val loadingPacks: Boolean = false,
    val lang: Lang? = null
) {

    companion object {
        val Empty = MyPackViewState()
    }
}

interface MyPackAction {
    fun processPack(pack: Pack)
    fun insertLection(pack: Pack)
    fun updateLectionImage(lec: Lection, text: String, onUpdateComplete: () -> Unit)
    fun updatePackTitle(packId: Long, title: String, onUpdateComplete: () -> Unit)
    fun updatePackLectionTitle(packId: Long, lec: Lection, title: String, onUpdateComplete: () -> Unit)
    fun processPackWithLection(pack: Pack, lection: Lection)
}

@Immutable
sealed class MyPackEvents {
    object INSUFFICIENT_COIN : MyPackEvents()
    data class OPEN_PACK(val pack: Pack) : MyPackEvents()
    data class OPEN_LECTION(val pack: Pack, val lec: Lection) : MyPackEvents()
    object LangUpdated : MyPackEvents()
}

@HiltViewModel
class MyPackViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val imageRepository: ImageRepository,
    private val languageRepository: LanguageRepository,
    private val packsRepository: PacksRepository,
    private val lectionRepository: LectionRepository,
    private val userRepository: UserRepository
) : ViewModel(), MyPackAction {

    private val _uiState = MutableStateFlow(
        MyPackViewState(loadingPacks = true, imageMap = imageRepository.lecImage)
    )
    val uiState: StateFlow<MyPackViewState> = _uiState.asStateFlow()

    val channel = Channel<MyPackEvents>()
    val event = channel.receiveAsFlow()

    init {
        val data = savedStateHandle.get<String>("data")
        logcat("MyPackViewModel") { "MyPackViewModel INIT ${data}"  }

        //Observe Lang
        viewModelScope.launch {
            languageRepository.observeSelectedLang.collect { lang ->
                _uiState.update { it.copy(lang = lang) }
                channel.send(MyPackEvents.LangUpdated)
            }
        }

        //Observe Sync
        viewModelScope.launch {
            userRepository.isLoginOrRegisterComplete.collect { lang ->
                logcat { "fetchUserCreatedPacks!!! EVENT NUMBER ${lang}" }
                fetchUserCreatedPacks()
            }
        }

    }

    fun fetchEditedPacks() {
        viewModelScope.launch {
            val pack = packsRepository.userEditedPacks()
            _uiState.update { it.copy(packs = pack) }
        }
    }

    fun fetchUserCreatedPacks() {
        viewModelScope.launch {
            val packs = packsRepository.fetchUserPacks()
            _uiState.update { it.copy(userCreatedPacks = packs) }
            packs.forEach {
                it.pack.lections.forEach { lectionImage ->
                    imageRepository.lecImage["${lectionImage.lang}${lectionImage.type}${lectionImage.lec}${lectionImage.owner}${lectionImage.pck}"] =
                        lectionImage.image
                }
            }
        }
    }

    override fun onCleared() {
        Log.d("MyPackViewModel", "onCleared")
        super.onCleared()
    }

    override fun processPack(pack: Pack) {
        viewModelScope.launch {
            when (val result = packsRepository.processPack(pack)) {
                PacksRepository.PackOrLectionStatus.InsufficientCoin -> channel.send(
                    MyPackEvents.INSUFFICIENT_COIN
                )
                is PacksRepository.PackOrLectionStatus.LectionOpen ->
                    channel.send(MyPackEvents.OPEN_LECTION(pack, result.lec))
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete ->
                    channel.send(
                        MyPackEvents.OPEN_LECTION(pack, result.lec)
                    )
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete -> channel.send(
                    MyPackEvents.OPEN_PACK(result.pack)
                )
                is PacksRepository.PackOrLectionStatus.PurchaseOpen -> channel.send(
                    MyPackEvents.OPEN_PACK(result.pack)
                )
                else -> {}
            }

        }
    }

    override fun insertLection(pack: Pack) {
        viewModelScope.launch {
            lectionRepository.insertPackLection(pack.pck,pack.owner)
            fetchUserCreatedPacks()
        }
    }

    fun insertUserPackAndLection(onLectionCreated: (packName: String, lec: Lection) -> Unit) {
        viewModelScope.launch {
            logcat("IMAGE:") { "insertPackAndLection" }
            packsRepository.insertUserPackAndLection("My Pack","My Lesson") {
                packName, lection ->
                onLectionCreated(packName, lection)
            }
        }
    }


    override fun updateLectionImage(lec: Lection, text: String, onUpdateComplete: () -> Unit) {
        viewModelScope.launch {
            imageRepository.updateUserLectionImage(
                lec.owner,
                lec.type,
                lec.pck,
                lec.lec,
                lec.lang,
                text
            )
            onUpdateComplete()
            fetchUserCreatedPacks()
        }
    }

    override fun updatePackTitle(packId: Long, title: String, onUpdateComplete: () -> Unit) {
        viewModelScope.launch {
            packsRepository.updateUserPackName(packId, title)
            onUpdateComplete()
            fetchUserCreatedPacks()
        }
    }

    override fun updatePackLectionTitle(packId: Long, lec: Lection, title: String, onUpdateComplete: () -> Unit) {
        viewModelScope.launch {
            lectionRepository.updateLectionName(packId, lec.lec, title)
            onUpdateComplete()
        }
    }

    override fun processPackWithLection(pack: Pack, lection: Lection) {
        viewModelScope.launch {
            when (val result = packsRepository.processPackAndLection(pack, lection)) {
                PacksRepository.PackOrLectionStatus.InsufficientCoin -> channel.send(
                    MyPackEvents.INSUFFICIENT_COIN
                )
                is PacksRepository.PackOrLectionStatus.LectionOpen -> channel.send(
                    MyPackEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackLectionPurchaseComplete -> channel.send(
                    MyPackEvents.OPEN_LECTION(pack, result.lec)
                )
                is PacksRepository.PackOrLectionStatus.PackPurchaseComplete -> channel.send(
                    MyPackEvents.OPEN_PACK(result.pack)
                )
                is PacksRepository.PackOrLectionStatus.PurchaseOpen -> channel.send(
                    MyPackEvents.OPEN_PACK(result.pack)
                )
                else -> {}
            }

        }
    }

}