package com.lengo.uni.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale


object LocaleHelper {

    /*fun onAttach(context: Context?): Context? {
        return if(context != null) {
            val lang = getApplicationLocales(context)
            setLocale(context, lang)
        } else {
            null
        }
    }*/

    fun setLocale(context: Context, language: String): Context {
        return updateResources(context, language)
    }


    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

}