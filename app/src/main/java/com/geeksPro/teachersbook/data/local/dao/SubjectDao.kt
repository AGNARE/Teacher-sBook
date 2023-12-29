package com.geeksPro.teachersbook.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject")
    fun getAll(): Flow<List<SubjectModel>>

    @Query("SELECT * FROM subject WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Long): SubjectModel?

    @Query("SELECT * FROM subject WHERE id = :subjectId")
    fun getTypeClasses(subjectId: Long): LiveData<SubjectModel>

    @Insert
    suspend fun insert(subjectModel: SubjectModel)

    @Delete
    suspend fun delete(subjectModel: SubjectModel)

    @Update
    suspend fun update(subjectModel: SubjectModel)
}