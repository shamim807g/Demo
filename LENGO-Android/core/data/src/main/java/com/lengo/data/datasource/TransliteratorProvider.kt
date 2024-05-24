package com.lengo.data.datasource

import android.icu.text.Transliterator
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransliteratorProvider @Inject constructor() {

    var transliterator: Transliterator? = null

    fun getTransliteration(lngCode: String): Transliterator? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (lngCode) {
                "cn","ar","el","ru","ua","th","ko" -> {
                    if(transliterator == null) {
                        transliterator = Transliterator.getInstance("Any-Latin")
                    }
                }
                else -> {
                    return null
                }
            }
        } else {
            return null
        }
        return transliterator
    }

    fun getExistingTransliterator(): Transliterator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            transliterator
        } else {
            null
        }
    }
}