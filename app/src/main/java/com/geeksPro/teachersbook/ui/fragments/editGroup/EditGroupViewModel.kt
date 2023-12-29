package com.geeksPro.teachersbook.ui.fragments.editGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGroupViewModel @Inject constructor(private val repository: GroupRepository) :
    ViewModel() {

     private val _groupsForSelectedSubject = MutableStateFlow<List<GroupModel>>(emptyList())
    val groupsForSelectedSubject: Flow<List<GroupModel>> get() = _groupsForSelectedSubject
    fun update(groupModel: GroupModel) {
        viewModelScope.launch {
            repository.update(groupModel = groupModel)
        }
    }

    fun delete(groupModel: GroupModel) =
        viewModelScope.launch { repository.delete(groupModel = groupModel) }

    fun insert(groupModel: GroupModel) = viewModelScope.launch {
        repository.insert(groupModel = groupModel)
    }
    fun loadGroupBySelectingSubject(subjectId: Long) {
        viewModelScope.launch {
            val groups = async { repository.getGroupsForSubject(subjectId) }
            _groupsForSelectedSubject.value = groups.await()
        }
    }
}