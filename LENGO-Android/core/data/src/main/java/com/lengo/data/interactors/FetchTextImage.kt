package com.lengo.data.interactors

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.lengo.common.Dispatcher
import com.lengo.common.LengoDispatchers
import com.lengo.data.repository.ImageRepository
import com.lengo.database.appdatabase.doa.PacksDao
import com.lengo.model.data.quiz.Game
import com.lengo.model.data.quiz.QuizFourQues
import com.lengo.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchTextImage @Inject constructor(
    private val packsDao: PacksDao,
    @Dispatcher(LengoDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val apiService: ApiService,
    private val imageRepository: ImageRepository,
    @ApplicationContext val context: Context,
) {
    val mutex = Mutex()

    suspend fun execute(
        games: List<Game>,
        quizImage: SnapshotStateMap<String, String?>,
        selLangCode: String
    ) {

        games.filterIsInstance<QuizFourQues>().forEach { quiz ->
            quiz.options.map { option ->
                withContext(ioDispatcher) {
                    val path = imageRepository.getObjectImageFilePath(
                        selLangCode, option.text, quiz.objParam.type,
                        option.objId, quiz.objParam.owner, quiz.objParam.pck, quiz.objParam.lec
                    )
                    mutex.withLock {
                        quizImage.put(
                            "IMG_${quiz.objParam.owner}_${quiz.objParam.type}_${quiz.objParam.pck}_${quiz.objParam.lec}_${option.objId}",
                            path
                        )
                    }
                }
            }

        }

    }

}

