package com.geeksPro.teachersbook.data.conversion

import android.util.Log
import androidx.room.TypeConverter
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class GroupsTypeConverter {

    @TypeConverter
    fun fromGroups(groups: List<GroupModel>?): String? {
        if (groups == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<GroupModel>>() {}.type
        val json = gson.toJson(groups, type)
        Log.d("TypeConverter", "fromGroups: $json")
        return gson.toJson(groups, type)
    }

    @TypeConverter
    fun toGroups(data: String?): List<GroupModel>? {
        if (data == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<GroupModel>>() {}.type
        val groups = gson.fromJson<List<GroupModel>>(data, type)
        Log.d("TypeConverter", "toGroups: $groups")
        return gson.fromJson(data, type)
    }
}