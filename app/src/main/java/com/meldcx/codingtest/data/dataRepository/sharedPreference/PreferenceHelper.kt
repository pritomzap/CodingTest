package com.meldcx.codingtest.data.dataRepository.sharedPreference

interface PreferenceHelper {
    fun setLatestBrowsedUrls(items:List<String>)
    fun getLatestBrowsedUrls():List<String>
}