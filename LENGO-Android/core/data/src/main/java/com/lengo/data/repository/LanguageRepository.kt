package com.lengo.data.repository

import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.common.Version
import com.lengo.common.di.ApplicationScope
import com.lengo.common.mapToSetupStructureLangCode
import com.lengo.data.datasource.LengoDataSource
import com.lengo.data.mapper.toLang
import com.lengo.data.mapper.toListOfLang
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.appdatabase.model.LanguageEntity
import com.lengo.model.data.Lang
import com.lengo.preferences.LengoPreference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepository @Inject constructor(
    val userDoa: UserDoa,
    val userRepository: UserRepository,
    @ApplicationScope appScope: CoroutineScope,
    private val lengoPreference: LengoPreference,
    @Dispatcher(LengoDispatchers.IO) val ioDispacher: CoroutineDispatcher,
    private val lengoDataSource: LengoDataSource,
) {

    val observeSelectedLang: SharedFlow<Lang> =
        userRepository.observeUserEntitySelAndDevice.flatMapLatest { userLang ->
            userDoa.getLanguage(userLang.sel).map { it?.toLang() }
        }.filterNotNull()
            .distinctUntilChanged()
            .flowOn(ioDispacher)
            .shareIn(appScope, SharingStarted.Lazily, 1)

    val observeDeviceLang: SharedFlow<Lang> =
        userRepository.observeUserEntitySelAndDevice.flatMapLatest { userLang ->
            val ownLang = mapToSetupStructureLangCode(userLang.own)
            userDoa.getLanguage(ownLang).map { it?.toLang() }
        }.filterNotNull()
            .distinctUntilChanged()
            .flowOn(ioDispacher)
            .shareIn(appScope, SharingStarted.Lazily, 1)


    val observeAllLanguages: SharedFlow<List<Lang>> =
        combine(userRepository.observeUserEntitySelAndDevice, userDoa.getAllLanguages())
        { _, allLang -> allLang }.map { it?.toListOfLang() }.filterNotNull()
            .flowOn(ioDispacher)
            .shareIn(appScope, SharingStarted.Lazily, 1)

    suspend fun updateAllLanguges() {
        withContext(ioDispacher) {
            val totalCount = userDoa.getTotalLanguageCount()
            val currentVersion = lengoPreference.getSetupStructureVersion()
            val jsonVersion = lengoDataSource.getSetupStructureVersion() ?: ""
            logcat { "currentVersion = ${currentVersion} jsonVersion ${jsonVersion}" }

            if (totalCount == null || totalCount <= 0 || currentVersion == null || Version(
                    jsonVersion
                ).compareTo(Version(currentVersion)) >= 1
            ) {
                logcat { "Updating..." }
                val languages = lengoDataSource.getAllLanguages()
                val languageEntitys = languages?.structure?.languages?.map { lng ->
                    LanguageEntity(
                        lng.tkn,
                        lng.accent,
                        lng.firstcolor,
                        lng.ios_appid,
                        lng.ios_bundleid,
                        lng.iso639,
                        lng.iso639_3,
                        lng.secondcolor
                    )
                }

                if (!languageEntitys.isNullOrEmpty()) {
                    userDoa.insertLanguage(languageEntitys)
                }
                lengoPreference.setSetupStructureVersion(jsonVersion)
            }
        }
    }

}