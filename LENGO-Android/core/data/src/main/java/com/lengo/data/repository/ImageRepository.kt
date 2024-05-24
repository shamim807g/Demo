package com.lengo.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.gson.JsonObject
import com.lengo.common.Dispatcher
import com.lengo.common.IMAGE_LOADING_ERROR
import com.lengo.common.LengoDispatchers
import com.lengo.common.SYS_GRAMMER
import com.lengo.common.SYS_VOCAB
import com.lengo.common.USER_VOCAB
import com.lengo.common.uni_images
import com.lengo.data.datasource.FileDownloader
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.database.appdatabase.doa.UserDoa
import com.lengo.database.newuidatabase.doa.UIPackLecDoa
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import com.lengo.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import logcat.logcat
import okhttp3.OkHttpClient
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

data class LectionImage(
    val lang: String,
    val type: String,
    val lec: Long,
    val owner: Long,
    val pck: Long,
    val result: String
)

@Singleton
class ImageRepository @Inject constructor(
    val apiService: ApiService,
    val uiPackLecDoa: UIPackLecDoa,
    val userDoa: UserDoa,
    val packsDao: PacksDao,
    val fileDownloader: FileDownloader,
    @Named("defaultClient") val okHttpClient: OkHttpClient,
    @ApplicationContext val context: Context,
    @Dispatcher(LengoDispatchers.IO) val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(LengoDispatchers.Main) val mainDispatcher: CoroutineDispatcher
) {
    val lecImage = SnapshotStateMap<String, String>()

    fun updateLectionImage(lectionImage: LectionImage) {
        if (!lecImage.containsKey("${lectionImage.lang}${lectionImage.type}${lectionImage.lec}${lectionImage.owner}${lectionImage.pck}")
            || lecImage["${lectionImage.lang}${lectionImage.type}${lectionImage.lec}${lectionImage.owner}${lectionImage.pck}"] != lectionImage.result
        ) {
            lecImage["${lectionImage.lang}${lectionImage.type}${lectionImage.lec}${lectionImage.owner}${lectionImage.pck}"] =
                lectionImage.result
        }
    }

    suspend fun setLectionImagePath(
        owner: Long,
        type: String,
        pck: Long,
        lec: Long,
        lang: String, path: String
    ) {
        if (!lecImage.containsKey("${lang}${type}${lec}${owner}${pck}") || lecImage["${lang}${type}${lec}${owner}${pck}"] != path) {
            lecImage["${lang}${type}${lec}${owner}${pck}"] = path
        }
        packsDao.updateLectionImage(path, lec, pck, type, owner)
        uiPackLecDoa.updateLectionImage2(path, lec, pck, owner, type)
    }

    suspend fun updateUserLectionImage(
        owner: Long,
        type: String,
        pck: Long,
        lec: Long,
        lang: String,
        text: String
    ) {
        logcat("IMAGE:") { "IN textToImage" }
        Log.d(
            "IMAGE:",
            "textToImage() called with owner = $owner, type = $type, pck = $pck, lec = $lec, lang = $lang, text = $text"
        )
        withContext(ioDispatcher) {
            val errorDraw = uni_images.random()
            try {
                val existingFile = isImageFileExist(
                    context,
                    getLectionDownloadImageWithTextFilePath(
                        owner = owner,
                        func = type,
                        pck = pck,
                        lec = lec,
                        txt = text
                    ),
                    getOldLectionPath(lang = lang, owner = owner, func = type, pck = pck, lec = lec)
                )

                if (existingFile != null) {
                    logcat("IMAGE:") { "existingFile present ${existingFile}" }
                    setLectionImagePath(owner, type, pck, lec, lang, existingFile)
                    return@withContext
                }

                logcat("IMAGE:") { "existingFile not present ${existingFile}" }

                logcat("IMAGE:") { "fetchLectionImages API START" }
                val resultImg = getLectionTxtImageFromAPI(lang = lang,
                    func = type.replace("-SUGGESTED", ""),lec = lec,
                    owner = owner, pck = pck, txt =  text)

                if (resultImg != null) {
                    logcat("IMAGE:") { "fetchLectionImages API END  ${resultImg}" }
                    val filePath = fileDownloader.downloadFile(
                        context,
                        getLectionDownloadImageWithTextFilePath(
                            owner = owner,
                            func = type,
                            pck = pck,
                            lec = lec,
                            txt = text
                        ),
                        "downloads",
                        resultImg!!
                    )
                    if (filePath != null) {
                        logcat("IMAGE:") { "fetchLectionImages filePath  ${filePath}" }
                        setLectionImagePath(owner, type, pck, lec, lang, filePath)
                        return@withContext
                    } else {
                        logcat("IMAGE:") { "fetchLectionImages error  ${IMAGE_LOADING_ERROR}:${errorDraw}" }
                        setLectionImagePath(
                            owner,
                            type,
                            pck,
                            lec,
                            lang,
                            "${IMAGE_LOADING_ERROR}:${errorDraw}"
                        )
                        return@withContext
                    }
                } else {
                    logcat("IMAGE:") { "fetchLectionImages error 2 ${IMAGE_LOADING_ERROR}:${errorDraw}" }
                    setLectionImagePath(
                        owner,
                        type,
                        pck,
                        lec,
                        lang,
                        "${IMAGE_LOADING_ERROR}:${errorDraw}"
                    )
                    return@withContext
                }


            } catch (ex: Exception) {
                logcat("IMAGE:") { "fetchLectionImages API EXCEPTION  ${ex.localizedMessage}" }
                setLectionImagePath(
                    owner,
                    type,
                    pck,
                    lec,
                    lang,
                    "${IMAGE_LOADING_ERROR}:${errorDraw}"
                )
                return@withContext
            }
        }
    }

    fun fetchLectionImages(discoverPacks: Map<String, List<Pack>>): Flow<LectionImage> {
        return flow {
            val remainingLections = mutableListOf<Lection>()
            for ((_, packs) in discoverPacks) {
                packs.forEachIndexed { index, pack ->
                    if (index == 0) {
                        pack.lections.forEach { lec ->
                            emit(lec)
                        }
                    } else {
                        remainingLections.addAll(pack.lections)
                    }
                }
            }
            remainingLections.forEach { value ->
                emit(value)
            }
        }.flatMapMerge { lec ->
            getLectionImagesForDiscover(lec)
        }.onEach { lecImage ->
            withContext(mainDispatcher) {
                updateLectionImage(lecImage)
            }
        }.flowOn(ioDispatcher)
    }


    private fun getLectionImagesForDiscover(
        lection: Lection
    ): Flow<LectionImage> {
        val errorDraw = uni_images.random()
        return flow {
            val existingFile = isImageFileExist(
                context,
                getLectionDownloadImageFilePath(
                    owner = lection.owner,
                    func = lection.type,
                    pck = lection.pck,
                    lec = lection.lec
                ),
                getOldLectionPath(
                    lang = lection.lang,
                    owner = lection.owner,
                    func = lection.type,
                    pck = lection.pck,
                    lec = lection.lec
                )
            )
            if (existingFile != null) {
                logcat("image repo:") { "existingFile present ${existingFile}" }
                emit(
                    LectionImage(
                        lang = lection.lang,
                        type = lection.type,
                        lec = lection.lec,
                        owner = lection.owner,
                        pck = lection.pck,
                        result = existingFile
                    )
                )
                return@flow
            }
            val existingAssetFilePath: String? =
                if (lection.type == SYS_GRAMMER) null else getImageFileFromAsset(
                    context,
                    "lectionimg/metadata_SysVok_-1_${lection.pck}_${lection.lec}.webp",
                    getLectionDownloadImageFilePath(
                        owner = lection.owner,
                        func = lection.type,
                        pck = lection.pck,
                        lec = lection.lec
                    ),
                )

            if (existingAssetFilePath != null) {
                logcat("image repo:") { "existingFile Asset File ${existingAssetFilePath}" }
                emit(
                    LectionImage(
                        lang = lection.lang,
                        type = lection.type,
                        lec = lection.lec,
                        owner = lection.owner,
                        pck = lection.pck,
                        result = existingAssetFilePath
                    )
                )
                return@flow
            }

            logcat("image repo:") { "existingFile not present ${existingFile}" }
            try {
                val resultImg = getLectionTxtImageFromAPI(lang = lection.lang,
                    func = lection.type.replace("-SUGGESTED", ""),lec = lection.lec,
                    owner = lection.owner, pck = lection.pck, txt =  "Nothing")

                if (resultImg != null) {
                    logcat("image repo:") { "fetchLectionImages API END  ${resultImg}" }
                    val filePath = fileDownloader.downloadFile(
                        context,
                        getLectionDownloadImageFilePath(
                            owner = lection.owner,
                            func = lection.type,
                            pck = lection.pck,
                            lec = lection.lec
                        ),
                        "downloads",
                        resultImg!!
                    )
                    if (filePath != null) {
                        logcat("image repo:") { "fetchLectionImages filePath  ${filePath}" }
                        emit(
                            LectionImage(
                                lang = lection.lang,
                                type = lection.type,
                                lec = lection.lec,
                                owner = lection.owner,
                                pck = lection.pck,
                                result = filePath
                            )
                        )
                    } else {
                        logcat("image repo:") { "fetchLectionImages error  ${IMAGE_LOADING_ERROR}:${errorDraw}" }
                        emit(
                            LectionImage(
                                lang = lection.lang,
                                type = lection.type,
                                lec = lection.lec,
                                owner = lection.owner,
                                pck = lection.pck,
                                result = "${IMAGE_LOADING_ERROR}:${errorDraw}"
                            )
                        )
                    }
                } else {
                    logcat("image repo:") { "fetchLectionImages error 2 ${IMAGE_LOADING_ERROR}:${errorDraw}" }
                    emit(
                        LectionImage(
                            lang = lection.lang,
                            type = lection.type,
                            lec = lection.lec,
                            owner = lection.owner,
                            pck = lection.pck,
                            result = "${IMAGE_LOADING_ERROR}:${errorDraw}"
                        )
                    )
                }
            } catch (ex: Exception) {
                logcat("image repo:") { "fetchLectionImages API EXCEPTION  ${ex.localizedMessage}" }
                emit(
                    LectionImage(
                        lang = lection.lang,
                        type = lection.type,
                        lec = lection.lec,
                        owner = lection.owner,
                        pck = lection.pck,
                        result = "${IMAGE_LOADING_ERROR}:${errorDraw}"
                    )
                )
            }
        }
    }

    suspend fun getObjectImageFilePath(
        selLang: String, txt: String, type: String, obj: Long, owner: Long, pck: Long, lec: Long
    ): String? {
        try {
            val existingFile = isImageFileExist(
                context,
                getObjDownloadImageFilePath(
                    func = type,
                    owner = owner,
                    pck = pck,
                    lec = lec,
                    obj = obj
                ),
                ""
            )
            if (existingFile != null) {
                logcat("image Object repo:") { "existingFile present ${existingFile}" }
                return existingFile
            }

            val existingAssetFilePath: String? = getImageFileFromAsset(
                context,
                "objectimg/object_${type}_${owner}_${pck}_${lec}_${obj}.webp",
                getObjDownloadImageFilePath(
                    func = type,
                    owner = owner,
                    pck = pck,
                    lec = lec,
                    obj = obj
                )
            )

            if (existingAssetFilePath != null) {
                logcat("image Object repo:") { "existingFile Asset File ${existingAssetFilePath}" }
                return existingAssetFilePath
            }
            logcat("image Object repo:") { "existingFile not present ${existingFile}" }
            val imageUrl = getObjTxtImageFromAPI(
                lang = selLang,
                txt = txt,
                func = type,
                obj = obj,
                owner = owner,
                pck = pck,
                lec = lec
            )
            if (imageUrl != null) {
                logcat("image Object repo:") { "imageUrl from API ${imageUrl}" }
                val downloadFilePath = fileDownloader.downloadFile(
                    context,
                    getObjDownloadImageFilePath(owner = owner, func = type, lec = lec, pck = pck, obj = obj),
                    "downloads",
                    imageUrl
                )
                return if (downloadFilePath != null) {
                    logcat("image Object repo:") { "downloadFilePath  ${downloadFilePath}" }
                    downloadFilePath
                } else {
                    logcat("image Object repo:") { "downloadFilePath is null ${downloadFilePath}" }
                    null
                }

            } else {
                logcat("image Object repo:") { "imageUrl is null from API ${imageUrl}" }
                return null
            }

        } catch (ex: Exception) {
            logcat("image Object repo:") { "getObjectImageFilePath  EXCEPTION  ${ex.localizedMessage}" }
            return null
        }
    }


    fun isImageFileExist(
        context: Context,
        imageFileName: String,
        oldImageFileNameToRemove: String
    ): String? {
        try {
            val downloadedDirectory = File("${context.cacheDir}", "downloads")
            if (!oldImageFileNameToRemove.isNullOrEmpty()) {
                val oldDownloadedFile = File("$downloadedDirectory", oldImageFileNameToRemove)
                if (oldDownloadedFile.exists()) {
                    oldDownloadedFile.delete()
                }
            }
            val downloadedFile = File("$downloadedDirectory", imageFileName)
            if (downloadedFile.exists()) {
                //logcat("image repo:") { "FILE EXIST ${imageName}" }
                return downloadedFile.absolutePath
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun getObjTxtImageFromAPI(
        lang: String, txt: String, func: String, obj: Long, owner: Long, pck: Long, lec: Long
    ): String? {
        return try {
            val innerJsonObject = JsonObject()
            innerJsonObject.addProperty("from_lng_tkn", lang)
            innerJsonObject.addProperty("text", txt)
            innerJsonObject.addProperty("func", func)
            innerJsonObject.addProperty("obj", obj)
            innerJsonObject.addProperty("owner", owner)
            innerJsonObject.addProperty("pck", pck)
            innerJsonObject.addProperty("lec", lec)
            val result = apiService.getTextToImage(innerJsonObject)
            result.get("img").asString
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLectionTxtImageFromAPI(
        lang: String, txt: String, func: String,
        owner: Long, pck: Long, lec: Long
    ): String? {
        return try {
            logcat("IMAGE:") { "fetchLectionImages API START" }
            val jsonObject = JsonObject()
            jsonObject.addProperty("from_lng_tkn", lang)
            jsonObject.addProperty("func", if (func == USER_VOCAB) SYS_VOCAB else func)
            jsonObject.addProperty("lec", lec)
            jsonObject.addProperty("owner", owner)
            jsonObject.addProperty("pck", pck)
            jsonObject.addProperty("text", txt)
            val result = apiService.getLectionImg(jsonObject)
            return result.img
        } catch (e: Exception) {
            null
        }
    }


    private fun getImageFileFromAsset(
        context: Context,
        assetFilePath: String,
        imageName: String,
    ): String? {
        return try {
            logcat("IMAGE:") { "Asset File path: ${assetFilePath}" }
            val existingAssetFile = context.assets.open(assetFilePath)
            if (existingAssetFile != null) {
                logcat("IMAGE:") { "existingAssetFile not null ${existingAssetFile}" }
            }
            val downloadedDirectory = File("${context.cacheDir}", "downloads")
            val downloadedFile = File("$downloadedDirectory", imageName)
            if (!downloadedDirectory.exists()) {
                downloadedDirectory.mkdir()
            }
            copyToFile(existingAssetFile, downloadedFile)
            logcat("IMAGE:") { "Asset File complete ${downloadedFile.absolutePath}" }
            downloadedFile.absolutePath
        } catch (e: Exception) {
            logcat("IMAGE:") { "Exception on Asset Loading File ${e.localizedMessage}" }
            null
        }
    }

    private fun getObjDownloadImageFilePath(
        owner: Long,
        func: String,
        pck: Long,
        lec: Long,
        obj: Long
    ): String {
        return "IMG_${owner}_${func}_${pck}_${lec}_${obj}.jpg"
    }

    private fun getLectionDownloadImageFilePath(
        owner: Long,
        func: String,
        pck: Long,
        lec: Long
    ): String {
        return "IMG_${owner}_${func}_${pck}_${lec}.jpg"
    }

    private fun getLectionDownloadImageWithTextFilePath(
        owner: Long,
        func: String,
        pck: Long,
        lec: Long,
        txt: String
    ): String {
        return "IMG_${txt}_${owner}_${func}_${pck}_${lec}.jpg"
    }

    private fun getOldLectionPath(
        lang: String,
        owner: Long,
        func: String,
        pck: Long,
        lec: Long
    ): String {
        return "IMG_${lang}_${owner}_${func}_${pck}_${lec}.jpg"
    }

    private fun copyToFile(inputStream: InputStream, outputFile: File) {
        val source = inputStream.source().buffer()
        val sink = outputFile.sink().buffer()
        source.use { input ->
            sink.use { output ->
                output.writeAll(input)
                sink.close()
                source.close()
            }
        }
    }
}