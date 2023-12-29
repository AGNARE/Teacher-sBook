package com.geeksPro.teachersbook.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.repositories.GroupRepository
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import com.geeksPro.teachersbook.data.repositories.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _data = MutableLiveData<List<StudentModel>>()
    val data: LiveData<List<StudentModel>> get() = _data

    fun findByNameOrSurname(search: String): LiveData<List<StudentModel>> {
        return repository.findByNameOrSurname(search).asLiveData()
    }

    fun getGrouopModel(groupId: Long): LiveData<GroupModel> {
        return groupRepository.getGroup(groupId)
    }

    fun getSubjectModel(subjectId: Long): LiveData<SubjectModel> {
        return subjectRepository.getSubjectModel(subjectId)
    }
}