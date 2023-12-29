package com.geeksPro.teachersbook.ui.fragments.editingStudent.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeletingStudentViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _student = MutableLiveData<List<StudentModel>>()
    val student: LiveData<List<StudentModel>> get() = _student

    fun getStudents(groupId: Long) {
        viewModelScope.launch {
            _student.value = repository.getStudentsAlphabetically(groupId)
        }
    }

    suspend fun deleteStudents(deletingStudents: List<Long>) =
        repository.deleteByIds(deletingStudents)
}