package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.studentPerformance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.models.StudentWithDetailsModel
import com.geeksPro.teachersbook.data.repositories.LaboratoryRepository
import com.geeksPro.teachersbook.data.repositories.LectureRepository
import com.geeksPro.teachersbook.data.repositories.PracticalRepository
import com.geeksPro.teachersbook.data.repositories.SeminarRepository
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LECTURE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentPerformanceViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val laboratoryRepository: LaboratoryRepository,
    private val practicalRepository: PracticalRepository,
    private val seminarRepository: SeminarRepository,
) : ViewModel() {

    private val _lectureCountVisits = MutableLiveData(0)
    val lectureCountVisits: LiveData<Int> get() = _lectureCountVisits

    private val _lectureMonthlyCountVisits = MutableLiveData(0)
    val lectureMonthlyCountVisits: LiveData<Int> get() = _lectureMonthlyCountVisits

    private val _lectureCountAbsence = MutableLiveData(0)
    val lectureCountAbsence: LiveData<Int> get() = _lectureCountAbsence

    private val _percentageLectureVisits = MutableLiveData(0)
    val percentageLectureVisits: LiveData<Int> get() = _percentageLectureVisits

    private val _percentageLectureAbsence = MutableLiveData(0)
    val percentageLectureAbsence: LiveData<Int> get() = _percentageLectureAbsence


    private val _laboratoryCountVisits = MutableLiveData(0)
    val laboratoryCountVisits: LiveData<Int> get() = _laboratoryCountVisits

    private val _laboratoryMonthlyCountVisits = MutableLiveData(0)
    val laboratoryMonthlyCountVisits: LiveData<Int> get() = _laboratoryMonthlyCountVisits

    private val _laboratoryCountAbsence = MutableLiveData(0)
    val laboratoryCountAbsence: LiveData<Int> get() = _laboratoryCountAbsence


    private val _laboratorySumGrades = MutableLiveData(0)
    val laboratorySumGrades: LiveData<Int> get() = _laboratorySumGrades


    private val _laboratoryPercentageGrades = MutableLiveData(0.0)
    val laboratoryPercentageGrades: LiveData<Double> get() = _laboratoryPercentageGrades

    private val _percentageLaboratoryVisits = MutableLiveData(0)
    val percentageLaboratoryVisits: LiveData<Int> get() = _percentageLaboratoryVisits

    private val _percentageLaboratoryAbsence = MutableLiveData(0)
    val percentageLaboratoryAbsence: LiveData<Int> get() = _percentageLaboratoryAbsence


    private val _practicalCountVisits = MutableLiveData(0)
    val practicalCountVisits: LiveData<Int> get() = _practicalCountVisits

    private val _practicalMonthlyCountVisits = MutableLiveData(0)
    val practicalMonthlyCountVisits: LiveData<Int> get() = _practicalMonthlyCountVisits

    private val _practicalCountAbsence = MutableLiveData(0)
    val practicalCountAbsence: LiveData<Int> get() = _practicalCountAbsence

    private val _practicalSumGrades = MutableLiveData(0)
    val practicalSumGrades: LiveData<Int> get() = _practicalSumGrades

    private val _practicalPercentageGrades = MutableLiveData(0.0)
    val practicalPercentageGrades: LiveData<Double> get() = _practicalPercentageGrades

    private val _percentagePracticalVisits = MutableLiveData(0)
    val percentagePracticalVisits: LiveData<Int> get() = _percentagePracticalVisits

    private val _percentagePracticalAbsence = MutableLiveData(0)
    val percentagePracticalAbsence: LiveData<Int> get() = _percentagePracticalAbsence


    private val _seminarCountVisits = MutableLiveData(0)
    val seminarCountVisits: LiveData<Int> get() = _seminarCountVisits

    private val _seminarMonthlyCountVisits = MutableLiveData(0)
    val seminarMonthlyCountVisits: LiveData<Int> get() = _seminarMonthlyCountVisits

    private val _seminarCountAbsence = MutableLiveData(0)
    val seminarCountAbsence: LiveData<Int> get() = _seminarCountAbsence

    private val _seminarSumGrades = MutableLiveData(0)
    val seminarSumGrades: LiveData<Int> get() = _seminarSumGrades

    private val _seminarPercentageGrades = MutableLiveData(0.0)
    val seminarPercentageGrades: LiveData<Double> get() = _seminarPercentageGrades

    private val _percentageSeminarVisits = MutableLiveData(0)
    val percentageSeminarVisits: LiveData<Int> get() = _percentageSeminarVisits

    private val _percentageSeminarAbsence = MutableLiveData(0)
    val percentageSeminarAbsence: LiveData<Int> get() = _percentageSeminarAbsence


    private val _lectures = MutableLiveData<List<LectureModel>>()
    val lectures: LiveData<List<LectureModel>> get() = _lectures

    private val _laboratories = MutableLiveData<List<LaboratoryModel>>()
    val laboratories: LiveData<List<LaboratoryModel>> get() = _laboratories

    private val _practicals = MutableLiveData<List<PracticalModel>>()
    val practicals: LiveData<List<PracticalModel>> get() = _practicals

    private val _seminars = MutableLiveData<List<SeminarModel>>()
    val seminars: LiveData<List<SeminarModel>> get() = _seminars


    private val _studentPerformancePercentage = MutableLiveData<Double>().apply { value = 0.0 }
    val studentPerformancePercentage: LiveData<Double> get() = _studentPerformancePercentage

    lateinit var studentWithDetails: StudentWithDetailsModel
    private var typeClasses = 0

    fun setTypeClasses(typeClasses: Int) {
        this.typeClasses = typeClasses
    }
    fun getAllData(studentId: Long, month: String) {
        viewModelScope.launch {
            studentWithDetails = studentRepository.getStudentWithDetails(studentId)

            var visits = 0
            var monthlyVisits = 0
            var absence = 0

            studentWithDetails.lectures.forEach { lecture ->
                if (lecture.visits) visits++
                else absence++

                if (lecture.visits && lecture.date.contains(month, ignoreCase = true)) {
                    monthlyVisits++
                }
            }

            _lectureCountVisits.value = visits
            _lectureCountAbsence.value = absence
            _lectureMonthlyCountVisits.value = monthlyVisits
            _percentageLectureVisits.value = findPercentage(visits, visits + absence).toInt()
            _percentageLectureAbsence.value = findPercentage(absence, visits + absence).toInt()

            visits = 0
            monthlyVisits = 0
            absence = 0
            var sumGrades = 0
            var countGrades = 0

            studentWithDetails.laboratories.forEach { laboratory ->
                if (laboratory.visits) visits++
                else absence++

                if (laboratory.visits && laboratory.date.contains(month, ignoreCase = true)) {
                    monthlyVisits++
                }

                if (laboratory.grades > 0) countGrades++

                sumGrades += laboratory.grades
            }

            _laboratoryPercentageGrades.value = findPercentage(countGrades, visits + absence)
            _laboratoryCountVisits.value = visits
            _laboratoryCountAbsence.value = absence
            _laboratoryMonthlyCountVisits.value = monthlyVisits
            _laboratorySumGrades.value = sumGrades
            _percentageLaboratoryVisits.value = findPercentage(visits, visits + absence).toInt()
            _percentageLaboratoryAbsence.value = findPercentage(absence, visits + absence).toInt()

            visits = 0
            monthlyVisits = 0
            absence = 0
            sumGrades = 0
            countGrades = 0


            studentWithDetails.practicals.forEach { practical ->
                if (practical.visits) visits++
                else absence++

                if (practical.visits && practical.date.contains(month, ignoreCase = true)) {
                    monthlyVisits++
                }

                if (practical.grades > 0) countGrades++

                sumGrades += practical.grades
            }

            _practicalPercentageGrades.value = findPercentage(countGrades, visits + absence)
            _practicalCountVisits.value = visits
            _practicalCountAbsence.value = absence
            _practicalMonthlyCountVisits.value = monthlyVisits
            _practicalSumGrades.value = sumGrades
            _percentagePracticalVisits.value = findPercentage(visits, visits + absence).toInt()
            _percentagePracticalAbsence.value = findPercentage(absence, visits + absence).toInt()

            visits = 0
            monthlyVisits = 0
            absence = 0
            sumGrades = 0
            countGrades = 0

            studentWithDetails.seminars.forEach { seminar ->
                if (seminar.visits) visits++
                else absence++

                if (seminar.visits && seminar.date.contains(month, ignoreCase = true)) {
                    monthlyVisits++
                }

                if (seminar.grades > 0) countGrades++

                sumGrades += seminar.grades
            }

            _seminarPercentageGrades.value = findPercentage(countGrades, visits + absence)
            _seminarCountVisits.value = visits
            _seminarCountAbsence.value = absence
            _seminarMonthlyCountVisits.value = monthlyVisits
            _seminarSumGrades.value = sumGrades
            _percentageSeminarVisits.value = findPercentage(visits, visits + absence).toInt()
            _percentageSeminarAbsence.value = findPercentage(absence, visits + absence).toInt()

            _studentPerformancePercentage.value = findStudentPerformancePercentage()
        }
    }

    fun getLectures(studentId: Long, month: String) {
        viewModelScope.launch {
            _lectures.value = lectureRepository.getLecturesByStudentIdAndMonth(studentId, month)
        }
    }

    fun getLaboratories(studentId: Long, month: String) {
        viewModelScope.launch {
            _laboratories.value =
                laboratoryRepository.getLaboratoriesByStudentIdAndMonth(studentId, month)
        }
    }

    fun getPracticals(studentId: Long, month: String) {
        viewModelScope.launch {
            _practicals.value =
                practicalRepository.getPracticalsByStudentIdAndMonth(studentId, month)
        }
    }

    fun getSeminars(studentId: Long, month: String) {
        viewModelScope.launch {
            _seminars.value = seminarRepository.getSeminarsByStudentIdAndMonth(studentId, month)
        }
    }

    fun updateLecture(lectureModel: LectureModel) {
        viewModelScope.launch {
            lectureRepository.insertLecture(lectureModel)
        }
    }

    fun updateLaboratory(laboratoryModel: LaboratoryModel) {
        viewModelScope.launch {
            laboratoryRepository.insertLaboratory(laboratoryModel)
        }
    }

    fun updatePractical(practicalModel: PracticalModel) {
        viewModelScope.launch {
            practicalRepository.insertPractical(practicalModel)
        }
    }

    fun updateSeminar(seminarModel: SeminarModel) {
        viewModelScope.launch {
            seminarRepository.insertSeminar(seminarModel)
        }
    }

    private fun findPercentage(part: Int, whole: Int): Double {
        var result = (part.toDouble() / whole.toDouble()) * 100
        if (result.isNaN()) {
            result = 0.0
        }
        return result
    }

    private fun findStudentPerformancePercentage(): Double {
        var sumAllPercent = 0.0

        _laboratoryPercentageGrades.value?.let { sumAllPercent += it }
        _practicalPercentageGrades.value?.let { sumAllPercent += it }
        _seminarPercentageGrades.value?.let { sumAllPercent += it }

        val maxPercent = countTypes(typeClasses) * 100.0
        return (sumAllPercent / maxPercent) * 100
    }

    private fun countTypes(typeClasses: Int): Double {
        var count = 0
        typeClasses.toString().forEach {
            if (it.toString() == "1") {
                count++
            }
        }

        if (typeClasses >= LECTURE) count--
        return count.toDouble()
    }
}