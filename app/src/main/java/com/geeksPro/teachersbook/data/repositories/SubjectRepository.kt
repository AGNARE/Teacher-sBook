package com.geeksPro.teachersbook.data.repositories

import androidx.lifecycle.LiveData
import com.geeksPro.teachersbook.data.local.dao.SubjectDao
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepository @Inject constructor(
    private val dao: SubjectDao,
) {
    suspend fun insert(model: SubjectModel) {
        dao.insert(model)
    }

    suspend fun update(model: SubjectModel) = dao.update(model)
    suspend fun delete(model: SubjectModel) = dao.delete(model)
    fun getAllSubjects(): Flow<List<SubjectModel>> = dao.getAll()
    fun getSubjectModel(subjectId: Long): LiveData<SubjectModel> = dao.getTypeClasses(subjectId)
    suspend fun getSubjectById(subjectId: Long) = dao.getSubjectById(subjectId)
}