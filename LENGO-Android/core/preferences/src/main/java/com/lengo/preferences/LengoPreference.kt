package com.lengo.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lengo.common.di.ApplicationScope
import com.lengo.common.extension.getCurrentDate
import com.lengo.model.data.SettingModel
import com.lengo.model.data.network.Recommendedresources
import com.lengo.model.data.toJsonString
import com.lengo.model.data.toSettingModel
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import logcat.logcat
import java.io.IOException
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "facr_pref")


interface IPreferenceDataStoreAPI {
    suspend fun <T> getPreference(key: Preferences.Key<T>,defaultValue: T):Flow<T>
    suspend fun <T> getFirstPreference(key: Preferences.Key<T>,defaultValue: T):T
    suspend fun <T> putPreference(key: Preferences.Key<T>,value:T)
    suspend fun <T> removePreference(key: Preferences.Key<T>)
    suspend fun <T> clearAllPreference()
}


class PreferenceDataStoreHelper(context: Context):IPreferenceDataStoreAPI {

    // dataSource access the DataStore file and does the manipulation based on our requirements.
    private val dataSource = context.dataStore

    /* This returns us a flow of data from DataStore.
    Basically as soon we update the value in Datastore,
    the values returned by it also changes. */
    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
            Flow<T> = dataSource.data.catch { exception ->
        if (exception is IOException){
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map { preferences->
        val result = preferences[key]?: defaultValue
        result
    }

    /* This returns the last saved value of the key. If we change the value,
        it wont effect the values produced by this function */
    override suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T) :
            T = dataSource.data.first()[key] ?: defaultValue

    // This Sets the value based on the value passed in value parameter.
    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataSource.edit {   preferences ->
            preferences[key] = value
        }
    }

    // This Function removes the Key Value pair from the datastore, hereby removing it completely.
    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataSource.edit { preferences ->
            preferences.remove(key)
        }
    }

    // This function clears the entire Preference Datastore.
    override suspend fun <T> clearAllPreference() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
    }
}

@Singleton
class LengoPreference @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val gson: Gson,
    @ApplicationScope coroutineScope: CoroutineScope
) {

    companion object {
        val SETUP_STRUCTURE_VERSION_STR = stringPreferencesKey(name = "setup_structure_version_v2_str")
        val IS_LANG_SHEET_SHOWN = booleanPreferencesKey(name = "sel_lng_sheet")
        val IS_SUB_SHEET_SHOWN = booleanPreferencesKey(name = "onboarding_sub_sheet_shown")
        val IS_REVIEW_SUBMITTED = booleanPreferencesKey(name = "is_new_review_submitted")
        val PACK_REVIEW_RATING = floatPreferencesKey(name = "pack_review_rating")
        val IS_ONBOARDING_COMPELETE = booleanPreferencesKey(name = "is_onboading_complete")
        val IS_FREE_TRAIL_AVAIL = booleanPreferencesKey(name = "is_free_trail_avail")
        val TIMES_USER_VISITED_WORD_LIST_SCREEN = intPreferencesKey(name = "times_user_visited_word_list_screen")
        val QUIZ_SETTING_KEY = stringPreferencesKey(name = "quiz_setting")
        val SESSION_ID_KEY = intPreferencesKey(name = "session_id")
        val SESSION_SOURCE = stringPreferencesKey(name = "session_source")
        val SESSION_RECOMMENDED_RES_KEY = stringPreferencesKey(name = "session_recommended_res")
        val IS_DISCOVER_DATA_SHOWN = stringPreferencesKey(name = "is_discover_data_shown")
        val IS_COUPONS_SHOWN = booleanPreferencesKey(name = "is_coupons_shown")
        val SUBMITTED_COUPONS = stringPreferencesKey(name = "submitted_coupons")
        val IS_FINNISH_SHEET_SHOW = booleanPreferencesKey(name = "is_finnish_sheet_shown")
    }


    suspend fun setDiscoverData() {
        context.dataStore.edit { preferences ->
            val date = getCurrentDate()
            preferences[IS_DISCOVER_DATA_SHOWN] = date
        }
    }

    suspend fun getDiscoverData(): String? {
        val pref = context.dataStore.data.first()
        return pref[IS_DISCOVER_DATA_SHOWN]
    }

    suspend fun setCouponsShown(shownInLngs: String, own: String) {
        val lngArr = shownInLngs.split("|")
        val shown = lngArr.contains(own)
        context.dataStore.edit { preferences ->
            preferences[IS_COUPONS_SHOWN] = shown
        }
    }

    fun getCouponsShown(): Flow<Boolean> {
        return context.dataStore.data.map { pref ->
            val shown = pref[IS_COUPONS_SHOWN] ?: false
            shown
        }
    }

    suspend fun addToSubmittedCoupons(add: String) {
        val pref = context.dataStore.data.first()
        val prev = pref[SUBMITTED_COUPONS] ?: ""
        val allCoupons = "$prev|$add"
        context.dataStore.edit { preferences ->
            preferences[SUBMITTED_COUPONS] = allCoupons
        }
    }

    suspend fun isCouponValid(code: String): Boolean {
        val pref = context.dataStore.data.first()
        val allCoupons = pref[SUBMITTED_COUPONS] ?: ""
        val allArr = allCoupons.split("|")
        val isContained = allArr.contains(code)
        return !isContained
    }

    suspend fun setSessionId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = id
        }
    }

    suspend fun setSessionSource(source: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_SOURCE] = source
        }
    }

    suspend fun getSessionSource(): String? {
        val pref = context.dataStore.data.first()
        return pref[SESSION_SOURCE]
    }

    suspend fun saveSessionRecommedRes(recommendedresources: List<Recommendedresources>?) {
        context.dataStore.edit { preferences ->
            if (recommendedresources != null) {
                val list = gson.toJson(recommendedresources)
                if (!list.isNullOrEmpty()) {
                    preferences[SESSION_RECOMMENDED_RES_KEY] = list
                }
            } else {
                preferences[SESSION_RECOMMENDED_RES_KEY] = ""
            }
        }
    }

    fun observeSessionRecommedRes(): Flow<List<Recommendedresources>> {
        return context.dataStore.data
            .map { preferences ->
                val sessionRecommedReJson = preferences[SESSION_RECOMMENDED_RES_KEY] ?: ""
                val type = object : TypeToken<List<Recommendedresources>>() {}.type
                val competitionList = try {
                    gson.fromJson(sessionRecommedReJson, type)
                } catch (e: Exception) {
                    emptyList<Recommendedresources>()
                }
                competitionList
            }
    }

    suspend fun getSetupStructureVersion(): String? {
        val pref = context.dataStore.data.first()
        return pref[SETUP_STRUCTURE_VERSION_STR]
    }

    suspend fun setSetupStructureVersion(version: String) {
        context.dataStore.edit { preferences ->
            preferences[SETUP_STRUCTURE_VERSION_STR] = version
        }
    }

    suspend fun getPackReviewRating(): Float {
        val pref = context.dataStore.data.first()
        return pref[PACK_REVIEW_RATING] ?: 0f
    }

    suspend fun setPackReviewRating(rating: Float) {
        context.dataStore.edit { preferences ->
            preferences[PACK_REVIEW_RATING] = rating
        }
    }

    suspend fun getSessionId(): Int? {
        val pref = context.dataStore.data.first()
        return pref[SESSION_ID_KEY]
    }

    suspend fun updateSetting(model: SettingModel) {
        context.dataStore.edit { preferences ->
            val json = model.toJsonString()
            if (!json.isNullOrEmpty()) {
                preferences[QUIZ_SETTING_KEY] = json
            }
        }
    }

    fun observeSettingModel(): Flow<SettingModel> {
        return context.dataStore.data.map { pref ->
            val json = pref[QUIZ_SETTING_KEY]
            json?.toSettingModel() ?: SettingModel(darkThemeEnable = false)
        }
    }

    suspend fun syncSettingModel(): SettingModel {
        val pref = context.dataStore.data.first()
        val json = pref[QUIZ_SETTING_KEY]
        return json?.toSettingModel() ?: SettingModel(darkThemeEnable = false)
    }

    suspend fun setOnboardingCompleted(value: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPELETE] = value
        }
    }

    suspend fun setReviewSubmitted(value: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[IS_REVIEW_SUBMITTED] = value
        }
    }

    suspend fun isReviewSubmitted(): Boolean {
        val pref = context.dataStore.data.first()
        return pref[IS_REVIEW_SUBMITTED] ?: false
    }

    suspend fun setOnboardingSubSheetShown(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SUB_SHEET_SHOWN] = value
            preferences[IS_FINNISH_SHEET_SHOW] = true
        }
    }

    suspend fun setLangSheetShown(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LANG_SHEET_SHOWN] = value
        }
    }

    suspend fun setFreeTrailAvail() {
        context.dataStore.edit { preferences ->
            preferences[IS_FREE_TRAIL_AVAIL] = true
        }
    }


    suspend fun incrementUserVisitedWordList() {
        context.dataStore.edit { pref ->
            val visted = pref[TIMES_USER_VISITED_WORD_LIST_SCREEN]
            if (visted == null || visted == 0) {
                pref[TIMES_USER_VISITED_WORD_LIST_SCREEN] = 1
            }
        }
    }

    suspend fun isValidToShowReview(isValid: (Boolean) -> Unit) {
        context.dataStore.edit { pref ->
            val visted = pref[TIMES_USER_VISITED_WORD_LIST_SCREEN]
            val source = pref[SESSION_SOURCE]
            val reviewSubmitted = pref[IS_REVIEW_SUBMITTED] ?: false
            logcat { "isValidToShowReview:$visted and $source" }
            if (visted == 1 && source != null && source != "search" && !reviewSubmitted) {
                isValid(true)
            } else {
                isValid(false)
            }
        }
    }

    suspend fun setAccentVoiceCode(accent: String, voiceCode: String) {
        val accentKey = stringPreferencesKey(name = accent)
        context.dataStore.edit { preferences ->
            preferences[accentKey] = voiceCode
        }
    }

    suspend fun getVoiceCode(accent: String): String? {
        val pref = context.dataStore.data.first()
        val accentKey = stringPreferencesKey(name = accent)
        return pref[accentKey]
    }


    val observePrefData: SharedFlow<ObservablePrefData> = context.dataStore.data.map { pref ->
            val isLangSheetShown = pref[IS_LANG_SHEET_SHOWN] ?: false
            val isSubSheetShown = pref[IS_SUB_SHEET_SHOWN] ?: false
            val isOnboardingComplete = pref[IS_ONBOARDING_COMPELETE] ?: false
            val isFreeTrailAvail = pref[IS_FREE_TRAIL_AVAIL] ?: false
            val isCouponShown = pref[IS_COUPONS_SHOWN] ?: false
            val isFinnishSheetShown = pref[IS_FINNISH_SHEET_SHOW] ?: (BuildConfig.FLAVOR_LANG_CODE != "fi")

            ObservablePrefData(
                isLangSheetShown,
                isSubSheetShown,
                isOnboardingComplete,
                isFreeTrailAvail,
                isFinnishSheetShown,
                isCouponShown
            )
        }.shareIn(coroutineScope, SharingStarted.Lazily, 1)
    }


@Immutable
data class ObservablePrefData(
    val isLangSheetShown: Boolean,
    val isSubSheetShown: Boolean,
    val isOnboardingComplete: Boolean,
    val isFreeTrailAvail: Boolean,
    val isFinnishSheetSecoundTime: Boolean,
    val isCouponShown: Boolean
)