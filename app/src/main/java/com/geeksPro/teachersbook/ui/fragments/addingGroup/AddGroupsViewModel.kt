package com.geeksPro.teachersbook.ui.fragments.addingGroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.repositories.GroupRepository
import com.geeksPro.teachersbook.data.repositories.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
    repositorySubject: SubjectRepository
) : ViewModel() {

    val groups: Flow<List<GroupModel>> = repository.getAllGroup()

    private val _groupsForSelectedSubject = MutableLiveData<List<GroupModel>>(emptyList())
    val groupsForSelectedSubject: LiveData<List<GroupModel>> get() = _groupsForSelectedSubject

    val allSubjects: Flow<List<SubjectModel>> = repositorySubject.getAllSubjects()

    private val _selectedSubject = MutableLiveData<SubjectModel>()

    fun selectSubject(subject: SubjectModel) {
        _selectedSubject.value = subject
    }

    fun addGroupToSubject(groupName: String) {
        val selectedSubject = _selectedSubject.value
        if (selectedSubject != null) {
            val newGroup = GroupModel(nameGroup = groupName, subjectId = selectedSubject.id)
            viewModelScope.launch {
                repository.insert(newGroup)
            }
        }
    }

    fun loadGroupsForSubject(subjectId: Long) {
        viewModelScope.launch {
            val groups = repository.getGroupsForSubject(subjectId)
            _groupsForSelectedSubject.value = groups
        }
    }
}