package com.geeksPro.teachersbook.ui.fragments.groupstatistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.repositories.LaboratoryRepository
import com.geeksPro.teachersbook.data.repositories.LectureRepository
import com.geeksPro.teachersbook.data.repositories.PracticalRepository
import com.geeksPro.teachersbook.data.repositories.SeminarRepository
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupStatisticViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val laboratoryRepository: LaboratoryRepository,
    private val practicalRepository: PracticalRepository,
    private val seminarRepository: SeminarRepository,
) : ViewModel() {

    private val _countStudents = MutableLiveData(0)
    val countStudents: LiveData<Int> get() = _countStudents

    private val _groupPerformancePercentage = MutableLiveData(0.0)
    val groupPerformancePercentage: LiveData<Double> get() = _groupPerformancePercentage

    private val _averageVisitsPercent = MutableLiveData(0.0)
    val averageVisitsPercent: LiveData<Double> get() = _averageVisitsPercent

    private val _averageAbsencesPercent = MutableLiveData(0.0)
    val averageAbsencesPercent: LiveData<Double> get() = _averageAbsencesPercent

    private val _laboratoryAverageGrades = MutableLiveData(0)
    val laboratoryAverageGrades: LiveData<Int> get() = _laboratoryAverageGrades

    private val _practicalAverageGrades = MutableLiveData(0)
    val practicalAverageGrades: LiveData<Int> get() = _practicalAverageGrades

    private val _seminarAverageGrades = MutableLiveData(0)
    val seminarAverageGrades: LiveData<Int> get() = _seminarAverageGrades

    private val _laboratoryAveragePercent = MutableLiveData(0.0)
    val laboratoryAveragePercent: LiveData<Double> get() = _laboratoryAveragePercent

    private val _practicalAveragePercent = MutableLiveData(0.0)
    val practicalAveragePercent: LiveData<Double> get() = _practicalAveragePercent

    private val _seminarAveragePercent = MutableLiveData(0.0)
    val seminarAveragePercent: LiveData<Double> get() = _seminarAveragePercent

    private var lectureGroupVisits = 0
    private var lectureGroupAbsences = 0

    private var laboratoryGroupVisits = 0
    private var laboratoryGroupAbsences = 0
    private var laboratoryGroupCountGrades = 0
    private var laboratoryGroupSumGrades = 0

    private var practicalGroupVisits = 0
    private var practicalGroupAbsences = 0
    private var practicalGroupCountGrades = 0
    private var practicalGroupSumGrades = 0

    private var seminarGroupVisits = 0
    private var seminarGroupAbsences = 0
    private var seminarGroupCountGrades = 0
    private var seminarGroupSumGrades = 0

    private var typeClasses = 0

    fun setTypeClasses(typeClasses: Int) {
        this.typeClasses = typeClasses
    }

    fun getAllData(groupId: Long) {
        viewModelScope.launch {
            _countStudents.value = studentRepository.getStudentCountByGroupId(groupId)

            lectureGroupAbsences = lectureRepository.getLecturesCountVisits(groupId, false)
            lectureGroupVisits = lectureRepository.getLecturesCountVisits(groupId, true)

            laboratoryGroupAbsences =
                laboratoryRepository.getLaboratoriesCountVisits(groupId, false)
            laboratoryGroupVisits = laboratoryRepository.getLaboratoriesCountVisits(groupId, true)
            laboratoryGroupCountGrades = laboratoryRepository.getLaboratoriesCountGrades(groupId)
            laboratoryGroupSumGrades = laboratoryRepository.getSumOfGradesByGroupId(groupId) ?: 0

            _laboratoryAverageGrades.value =
                calculateAverage(laboratoryGroupSumGrades, laboratoryGroupCountGrades)

            practicalGroupAbsences = practicalRepository.getPracticalsCountVisits(groupId, false)
            practicalGroupVisits = practicalRepository.getPracticalsCountVisits(groupId, true)
            practicalGroupCountGrades = practicalRepository.getPracticalsCountGrades(groupId)
            practicalGroupSumGrades = practicalRepository.getSumOfGradesByGroupId(groupId) ?: 0

            _practicalAverageGrades.value =
                calculateAverage(practicalGroupSumGrades, practicalGroupCountGrades)

            seminarGroupAbsences = seminarRepository.getSeminarsCountVisits(groupId, false)
            seminarGroupVisits = seminarRepository.getSeminarsCountVisits(groupId, true)
            seminarGroupCountGrades = seminarRepository.getSeminarsCountGrades(groupId)
            seminarGroupSumGrades = seminarRepository.getSumOfGradesByGroupId(groupId) ?: 0

            _seminarAverageGrades.value =
                calculateAverage(seminarGroupSumGrades, seminarGroupCountGrades)

            val sumVisits =
                lectureGroupVisits + laboratoryGroupVisits + practicalGroupVisits + seminarGroupVisits
            val sumAbsences =
                lectureGroupAbsences + laboratoryGroupAbsences + practicalGroupAbsences + seminarGroupAbsences

            _averageAbsencesPercent.value = findPercentage(sumAbsences, sumAbsences + sumVisits)
            _averageVisitsPercent.value = findPercentage(sumVisits, sumAbsences + sumVisits)

            _laboratoryAveragePercent.value =
                findPercentage(laboratoryGroupCountGrades, laboratoryGroupVisits)
            _practicalAveragePercent.value =
                findPercentage(practicalGroupCountGrades, practicalGroupVisits)
            _seminarAveragePercent.value =
                findPercentage(seminarGroupCountGrades, seminarGroupVisits)

            _groupPerformancePercentage.value = findStudentPerformancePercentage()
        }
    }

    private fun findStudentPerformancePercentage(): Double {
        var sumAllPercent = 0.0

        sumAllPercent += _laboratoryAveragePercent.value!!
        sumAllPercent += _practicalAveragePercent.value!!
        sumAllPercent += _seminarAveragePercent.value!!

        val maxPercent = countTypes(typeClasses) * 100.0
        return (sumAllPercent / maxPercent) * 100
    }

    private fun findPercentage(part: Int, whole: Int): Double {
        val result = (part.toDouble() / whole.toDouble()) * 100
        if (result.isNaN()) {
            return 0.0 // Чтобы избежать NaN
        }
        return result
    }

    private fun calculateAverage(sum: Int, count: Int): Int {
        if (count == 0) {
            return 0 // Чтобы избежать деления на ноль
        }
        return sum / count
    }

    private fun countTypes(typeClasses: Int): Double {
        var count = 0
        typeClasses.toString().forEach {
            if (it.toString() == "1") {
                count++
            }
        }

        if (typeClasses >= SubjectsFragment.LECTURE) count--
        return count.toDouble()
    }
}