package com.lengo.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lengo.database.jsonDatabase.model.JsonPack
import java.lang.reflect.Type


@ProvidedTypeConverter
class LengoTypeConverter(val gson: Gson) {

    @TypeConverter
    fun fromStringToMapList(data: String?): Map<String, List<String>>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String, List<String>>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

    @TypeConverter
    fun fromMapListToString(data: Map<String, List<String>>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }


    @TypeConverter
    fun fromStringToMap(data: String?): Map<String, String>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

//    val columnAdapterMap = object : ColumnAdapter<Map<String, String>, String> {
//        override fun decode(data: String): Map<String, String> {
//            if (data.isNullOrEmpty()) return emptyMap()
//            val typeOfHashMap: Type = object : TypeToken<Map<String, String>>() {}.type
//            return gson.fromJson(data, typeOfHashMap)
//        }
//
//        override fun encode(value: Map<String, String>): String {
//            if (value.isNullOrEmpty()) return ""
//            return gson.toJson(value)
//        }
//
//    }


    @TypeConverter
    fun fromMapToString(data: Map<String, String>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

    @TypeConverter
    fun fromStringToMap2(data: String?): Map<String, Int>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String?, Int>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

    @TypeConverter
    fun fromMapToString2(data: Map<String, Int>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

    //@Json(name = "explanation")
    //    val explanation: Map<String,Map<String,String>>?,
    //    @Json(name = "examples")
    //    val examples: Map<String,List<Example>>?,

    @TypeConverter
    fun fromGramObj(data: String?): Map<String, Any>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String, Any>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

    @TypeConverter
    fun toGramObj(data: Map<String, Any>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }


    @TypeConverter
    fun fromStringToMapOfMap(data: String?): Map<String, Map<String, String>>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

    @TypeConverter
    fun fromMapOfMapToString(data: Map<String, Map<String, String>>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

    @TypeConverter
    fun fromStringToListOfLections(data: String?): List<JsonPack.Lection>? {
        if (data.isNullOrEmpty()) return null
        val type: Type = object : TypeToken<List<JsonPack.Lection>?>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun froListOfLectionsToString(data: List<JsonPack.Lection>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

    @TypeConverter
    fun fromStringToMapOfMapList(data: String?): Map<String, Map<String, Any>>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<Map<String, Map<String, Any>>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

    @TypeConverter
    fun fromMapOfMapToStringList(data: Map<String, Map<String, Any>>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

//    @TypeConverter
//    fun fromStringToMapOfList(data: String?): Map<String,List<Example>>? {
//        if(data.isNullOrEmpty()) return null
//        val typeOfHashMap: Type = object : TypeToken<Map<String,List<Example>>?>() {}.type
//        return gson.fromJson(data, typeOfHashMap)
//    }
//
//    @TypeConverter
//    fun fromMapOfListToString(data: Map<String,List<Example>>?): String? {
//        if(data.isNullOrEmpty()) return null
//        return gson.toJson(data)
//    }

    @TypeConverter
    fun formListToString(data: List<String>?): String? {
        if (data.isNullOrEmpty()) return null
        return gson.toJson(data)
    }

    @TypeConverter
    fun toListFromString(data: String?): List<String>? {
        if (data.isNullOrEmpty()) return null
        val typeOfHashMap: Type = object : TypeToken<List<String>?>() {}.type
        return gson.fromJson(data, typeOfHashMap)
    }

}
