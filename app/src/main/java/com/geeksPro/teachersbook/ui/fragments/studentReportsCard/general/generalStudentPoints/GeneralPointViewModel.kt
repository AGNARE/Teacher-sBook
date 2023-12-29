package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.general.generalStudentPoints

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.repositories.GeneralPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralPointViewModel @Inject constructor(val repository: GeneralPointRepository) :
    ViewModel() {

    private val _laboratoryGradesAndDates = MutableLiveData<List<LaboratoryModel>>()
    val laboratoryGradesAndDates: LiveData<List<LaboratoryModel>> =
        _laboratoryGradesAndDates

    private val _practicalGradesAndDates = MutableLiveData<List<PracticalModel>>()
    val practicalGradesAndDates: LiveData<List<PracticalModel>> = _practicalGradesAndDates

    private val _seminarGradesAndDates = MutableLiveData<List<SeminarModel>>()
    val seminarGradesAndDates: LiveData<List<SeminarModel>> = _seminarGradesAndDates

    fun getGradesAndDates(studentId: Long) {
        viewModelScope.launch {
            _laboratoryGradesAndDates.value =
                repository.getLaboratoryDatesAndGradesForStudent(studentId)
            _practicalGradesAndDates.value =
                repository.getPracticalDatesAndGradesForStudent(studentId)
            _seminarGradesAndDates.value = repository.getSeminarDatesAndGradesForStudent(studentId)
        }
    }

    fun updateLaboratoryGrade(dateAndGrade: List<LaboratoryModel>) {
        viewModelScope.launch {
            repository.updateLaboratoryGrade(dateAndGrade)
        }
    }

    fun updatePracticalGrade(dateAndGrade: List<PracticalModel>) {
        viewModelScope.launch {
            repository.updatePracticalGrade(dateAndGrade)
        }
    }

    fun updateSeminarGrade(dateAndGrade: List<SeminarModel>) {
        viewModelScope.launch {
            repository.updateSeminarGrade(dateAndGrade)
        }
    }
}