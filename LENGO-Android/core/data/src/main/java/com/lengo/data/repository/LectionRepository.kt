package com.lengo.data.repository

import com.lengo.common.DEFAULT_OWN_LANG
import com.lengo.common.DEFAULT_SEL_LANG
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.USER_VOCAB
import com.lengo.data.mapper.toLection
import com.lengo.data.mapper.toLectionList
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import com.lengo.database.newuidatabase.model.LectionUIEntity
import com.lengo.model.data.Lection
import com.lengo.model.data.PackId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class LectionRepository @Inject constructor(
    private val uiPackDoa: UIPackLecDoa,
    private val userRepository: UserRepository,
    private val packsDao: PacksDao,
    private val imageRepository: ImageRepository,
    @Dispatcher(LengoDispatchers.IO) val ioDispacher: CoroutineDispatcher
) {

    suspend fun insertPackLection(pack: Long,packOwner: Long) {
        val selLang = userRepository.getUserEntitySelAndDevice()
        val id = packsDao.insertUserPackLection("My Lesson", selLang.sel, selLang.own, pack,packOwner)
        val userLectionName = "My Lesson ${if(id > 0) id else ""}"
        uiPackDoa.insertLectionUI(
            LectionUIEntity(
                lec = id,
                lang = selLang.sel,
                pck = pack,
                owner = packOwner,
                type = USER_VOCAB,
                lec_title = userLectionName,
                lec_nameMap = mapOf(Pair(selLang.own, userLectionName)),
                errorDrawable = -1,
                example = null,
                explanation = null,
                lec_image = "placeholder"
            )
        )
        imageRepository.updateUserLectionImage(
            packOwner,
            USER_VOCAB,
            pack,
            id,
            selLang.sel,
            "My Lesson"
        )
    }

    suspend fun updateLectionName(pack: Long, lec: Long, title: String) {
        val currentUser = userRepository.getCurrentUserEntity()
        packsDao.updateUserPackLection(mapOf(Pair(currentUser?.own ?: DEFAULT_OWN_LANG, title)), pack, lec, USER_VOCAB, currentUser?.userid ?: -1)
        uiPackDoa.updateUILectionTitle(
            mapOf(Pair(currentUser?.own ?: DEFAULT_OWN_LANG, title)),
            title,
            pack,
            lec,
            currentUser?.userid ?: -1,
            USER_VOCAB,
        )
    }

    suspend fun getLection(
        packId: Long,
        lectionID: Long,
        owner: Long,
        type: String,
        lang: String
    ): Lection? {
        return uiPackDoa.getLection(lectionID, packId, owner, type, lang)?.toLection()
    }

    fun observeLections(packId: PackId): Flow<List<Lection>> {
        return uiPackDoa.getLections(
            packId.pck,
            packId.owner,
            packId.type,
            packId.lang,
        ).mapLatest {
            return@mapLatest it.sortedBy { it.lec }.toLectionList()
        }.catch { emit(emptyList()) }
            .flowOn(ioDispacher)

    }
}