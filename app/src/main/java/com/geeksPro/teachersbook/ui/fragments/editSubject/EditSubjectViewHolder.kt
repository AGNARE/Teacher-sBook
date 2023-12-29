package com.geeksPro.teachersbook.ui.fragments.editSubject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.repositories.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectEditViewModel @Inject constructor(private val repository: SubjectRepository) :
    ViewModel() {
    val subjects: Flow<List<SubjectModel>> = repository.getAllSubjects()

    fun update(subjectModel: SubjectModel) = viewModelScope.launch {
        repository.update(model = subjectModel)
    }

    fun delete(subjectModel: SubjectModel) =
        viewModelScope.launch { repository.delete(model = subjectModel) }

    fun insert(subject: SubjectModel) = viewModelScope.launch {
        repository.insert(model = subject)
    }
}