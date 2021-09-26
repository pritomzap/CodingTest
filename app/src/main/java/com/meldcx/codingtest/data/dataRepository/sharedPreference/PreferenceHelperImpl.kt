package com.meldcx.codingtest.data.dataRepository.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class PreferenceHelperImpl @Inject constructor(context: Context) : PreferenceHelper {

    companion object {
        private const val SHARED_PREF_NAME = "MeldCxApplication"
        private const val KEY_SAVED_LATEST_SEARCHS = "keySavedLatestSearchs"
    }

    private var sPref: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private var sPrefEditor: SharedPreferences.Editor = sPref.edit()

    override fun setLatestBrowsedUrls(items: List<String>) {
        sPrefEditor.apply {
            putString(KEY_SAVED_LATEST_SEARCHS, Gson().toJson(items).toString())
            commit()
        }
    }

    override fun getLatestBrowsedUrls(): List<String> {
        val latestSearchUrls = sPref.getString(KEY_SAVED_LATEST_SEARCHS, null)
        if (!latestSearchUrls.isNullOrEmpty()){
            val type = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(latestSearchUrls, type)
        }else
            return listOf()
    }
}