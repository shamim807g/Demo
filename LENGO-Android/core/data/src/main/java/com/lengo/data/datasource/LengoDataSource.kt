package com.lengo.data.datasource

import android.content.Context
import android.content.res.AssetManager
import com.lengo.data.preload.AchievementsModel
import com.lengo.data.preload.AchievementsModelJsonAdapter
import com.lengo.data.preload.LanguageModel
import com.lengo.data.preload.LanguageModelJsonAdapter
import com.lengo.data.preload.PointsStepsModel
import com.lengo.data.preload.PointsStepsModelJsonAdapter
import com.lengo.data.preload.PreloadModel
import com.lengo.data.preload.PreloadModelJsonAdapter
import com.lengo.data.preload.VersionModelJsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.buffer
import okio.source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class LengoDataSource @Inject constructor(
    @ApplicationContext val context: Context,
    private val assetManager: AssetManager,
    private val moshi: Moshi,
) {
    //var model: MainModel? = null
    var preModel: PreloadModel? = null
    var pointMap: MutableMap<Int, Int> = mutableMapOf()
    var levelKeys = listOf<Int>()
    var pointsStepsModel: PointsStepsModel? = null

    fun AssetManager.toAchievementModel(jsonFile: String): AchievementsModel? {
        var model: AchievementsModel? = null
        JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
            AchievementsModelJsonAdapter(moshi).fromJson(reader).let {
                model = it
            }
        }
        return model
    }

    fun AssetManager.toLanguageModel(jsonFile: String): LanguageModel? {
        var model: LanguageModel? = null
        JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
            LanguageModelJsonAdapter(moshi).fromJson(reader).let {
                model = it
            }
        }
        return model
    }

    fun AssetManager.toSetupStructureVersion(jsonFile: String): String? {
        var model: String? = null
        JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
            VersionModelJsonAdapter(moshi).fromJson(reader).let {
                model = it.structure.version
            }
        }
        return model
    }

    fun AssetManager.toPointSteps(jsonFile: String): PointsStepsModel? {
        if (pointsStepsModel == null) {
            JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
                PointsStepsModelJsonAdapter(moshi).fromJson(reader).let {
                    pointsStepsModel = it
                }
            }
        }
        return pointsStepsModel
    }

    fun AssetManager.layoutsFromJson(jsonFile: String): PreloadModel? {
        if (preModel == null) {
            JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
                PreloadModelJsonAdapter(moshi).fromJson(reader).let {
                    preModel = it
                }
            }
        }
        return preModel
    }

//    fun AssetManager.layoutsFromJsonDB(jsonFile: String): MainModel? {
//        if(model == null) {
//            JsonReader.of(open(jsonFile).source().buffer()).use { reader ->
//                MainModelJsonAdapter(moshi).fromJson(reader).let {
//                    model = it
//                }
//            }
//        }
//        return model
//    }

    fun getAllLanguages(): LanguageModel? {
        return assetManager.toLanguageModel("setup_structure.json")
    }

    fun getSetupStructureVersion(): String? {
        return assetManager.toSetupStructureVersion("setup_structure.json")
    }

    fun getAchivementModel(): AchievementsModel? {
        return assetManager.toAchievementModel("setup_structure.json")
    }

    fun getPointSteps(): PointsStepsModel? {
        return assetManager.toPointSteps("setup_structure.json")
    }

    fun getLevelPointMap(): Map<Int, Int> {
        if (pointMap.isEmpty()) {
            val steps = getPointSteps()
            steps?.structure?.points_steps_list?.filter { it.lvl != 0 }?.forEach {
                var lastPoint = pointMap.getOrDefault(it.lvl, 0)
                if (lastPoint == 0) {
                    lastPoint = pointMap.getOrDefault(it.lvl - 1, 0)
                }
                pointMap[it.lvl] = lastPoint + it.points
            }
            return pointMap
        } else {
            return pointMap
        }
    }

    fun getLevelPointsKeys(): List<Int> {
        if(levelKeys.isEmpty()) {
            levelKeys = pointMap.keys.sorted()
        }
        return levelKeys
    }


//    fun getAllPacksForJsonDb(): MainModel? {
//        return assetManager.layoutsFromJsonDB("preload.json")
//    }

    fun getAllPacks(): PreloadModel? {
        return assetManager.layoutsFromJson("preload.json")
    }


}