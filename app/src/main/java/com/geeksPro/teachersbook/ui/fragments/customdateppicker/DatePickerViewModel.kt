package com.geeksPro.teachersbook.ui.fragments.customdateppicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor()  : ViewModel() {
    val selectedDate: MutableLiveData<String> = MutableLiveData()
}