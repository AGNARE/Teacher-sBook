package com.geeksPro.teachersbook.ui.fragments.addingStudent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.repositories.GroupRepository
import com.geeksPro.teachersbook.data.repositories.LaboratoryRepository
import com.geeksPro.teachersbook.data.repositories.LectureRepository
import com.geeksPro.teachersbook.data.repositories.PracticalRepository
import com.geeksPro.teachersbook.data.repositories.SeminarRepository
import com.geeksPro.teachersbook.data.repositories.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStudentViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val groupRepository: GroupRepository,
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val laboratoryRepository: LaboratoryRepository,
    private val practicalRepository: PracticalRepository,
    private val seminarRepository: SeminarRepository,
) : ViewModel() {

    private val _subject = MutableLiveData<SubjectModel>()
    val subject: LiveData<SubjectModel> get() = _subject

    private val _students = MutableLiveData<List<StudentModel>>()
    val students: LiveData<List<StudentModel>> get() = _students

    private val _groupId = MutableLiveData<Long>()
    val groupId: LiveData<Long> = _groupId

    private val _hasStudents = MutableLiveData<Boolean>()
    val hasStudents: LiveData<Boolean> = _hasStudents

    private val _lecturesByGroupIdAndDate = MutableLiveData<List<LectureModel>>()
    val lecturesByGroupIdAndDate: LiveData<List<LectureModel>> =
        _lecturesByGroupIdAndDate

    private val _laboratoriesByGroupIdAndDate = MutableLiveData<List<LaboratoryModel>>()
    val laboratoriesByGroupIdAndDate: LiveData<List<LaboratoryModel>> =
        _laboratoriesByGroupIdAndDate

    private val _practicalsByGroupIdAndDate = MutableLiveData<List<PracticalModel>>()
    val practicalsByGroupIdAndDate: LiveData<List<PracticalModel>> =
        _practicalsByGroupIdAndDate

    private val _seminarsByGroupIdAndDate = MutableLiveData<List<SeminarModel>>()
    val seminarsByGroupIdAndDate: LiveData<List<SeminarModel>> =
        _seminarsByGroupIdAndDate

    private val _btnGrades = MutableLiveData<Boolean>()
    val btnGrades: LiveData<Boolean> = _btnGrades

    private var typeSelectedClasses: Int = 0

    fun getSubjectById(subjectsId: Long) {
        viewModelScope.launch {
            subjectRepository.getSubjectById(subjectsId).also { _subject.value = it }
        }
    }

    fun getGroup(groupId: Long) = groupRepository.getGroup(groupId)
    fun setGroup(groupId: Long) {
        _groupId.value = groupId
    }

    fun loadStudents(groupId: Long) {
        viewModelScope.launch {
            val students = studentRepository.getStudentsAlphabetically(groupId)
            _students.value = students
            _hasStudents.value = students.isNotEmpty()
        }
    }

    fun addStudent(student: StudentModel) {
        viewModelScope.launch {
            studentRepository.insertStudent(student)
        }
    }

    suspend fun insertLectures(lectures: List<LectureModel>) =
        lectureRepository.insertLectures(lectures)

    fun getLecturesByGroupIdAndDate(groupId: Long, date: String) {
        viewModelScope.launch {
            _lecturesByGroupIdAndDate.value =
                lectureRepository.getLecturesByGroupIdAndDate(groupId, date)
        }
    }

    suspend fun insertLaboratory(laboratory: LaboratoryModel) =
        laboratoryRepository.insertLaboratory(laboratory)

    suspend fun insertLaboratories(laboratories: List<LaboratoryModel>) =
        laboratoryRepository.insertLaboratories(laboratories)

    fun getLaboratoriesByGroupIdAndDate(groupId: Long, date: String) {
        viewModelScope.launch {
            _laboratoriesByGroupIdAndDate.value =
                laboratoryRepository.getLaboratoriesByGroupIdAndDate(groupId, date)
        }
    }

    suspend fun insertPractical(practical: PracticalModel) =
        practicalRepository.insertPractical(practical)

    suspend fun insertPracticals(practicals: List<PracticalModel>) =
        practicalRepository.insertPracticals(practicals)

    fun getPracticalsByGroupIdAndDate(groupId: Long, date: String) {
        viewModelScope.launch {
            _practicalsByGroupIdAndDate.value =
                practicalRepository.getPracticalsByGroupIdAndDate(groupId, date)
        }
    }

    suspend fun insertSeminar(seminar: SeminarModel) = seminarRepository.insertSeminar(seminar)

    suspend fun insertSeminars(seminars: List<SeminarModel>) =
        seminarRepository.insertSeminars(seminars)

    fun getSeminarsByGroupIdAndDate(groupId: Long, date: String) {
        viewModelScope.launch {
            _seminarsByGroupIdAndDate.value =
                seminarRepository.getSeminarsByGroupIdAndDate(groupId, date)
        }
    }
}
