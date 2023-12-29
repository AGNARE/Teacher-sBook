package com.geeksPro.teachersbook.extentions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel(){
    val currentTab: MutableLiveData<Int> = MutableLiveData()
    val subjectId = MutableLiveData<Long>()
}