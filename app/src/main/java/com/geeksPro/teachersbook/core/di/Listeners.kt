package com.geeksPro.teachersbook.core.di

import androidx.lifecycle.MutableLiveData
object Listeners {
    val booleanLiveData = MutableLiveData<Boolean>()
    val languageLiveData = MutableLiveData<Boolean>()
    val restartListener = MutableLiveData<Boolean>()
}