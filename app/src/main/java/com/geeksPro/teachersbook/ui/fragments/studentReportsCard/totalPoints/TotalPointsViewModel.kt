package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.totalPoints

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.StudentScoreModel
import com.geeksPro.teachersbook.data.repositories.StudentScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalPointsViewModel @Inject constructor(private val repository: StudentScoreRepository) :
    ViewModel() {
    private val _estimationPoint = MutableLiveData<String>()
    val estimationPoint: LiveData<String>
        get() = _estimationPoint

    fun updateEstimationPoint(newEstimationPoint: String) {
        _estimationPoint.value = newEstimationPoint
    }

    fun saveScores(model: StudentScoreModel) {
        viewModelScope.launch {
            repository.saveScores(model)
        }
    }

    fun getScores(studentId: Long) = liveData {
        val data = repository.getScores(studentId)
        emit(data)
    }
}