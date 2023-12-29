package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel

@Dao
interface GeneralPointDao {
    @Query("SELECT * FROM laboratories WHERE studentId = :studentId")
    suspend fun getLaboratoryDatesAndGradesForStudent(studentId: Long): List<LaboratoryModel>

    @Query("SELECT * FROM practicals WHERE studentId = :studentId")
    suspend fun getPracticalDatesAndGradesForStudent(studentId: Long): List<PracticalModel>

    @Query("SELECT * FROM seminars WHERE studentId = :studentId")
    suspend fun getSeminarDatesAndGradesForStudent(studentId: Long): List<SeminarModel>

    @Update
    suspend fun updateLaboratoryGrade(dateAndGrade: List<LaboratoryModel>)

    @Update
    suspend fun updatePracticalGrade(dateAndGrade: List<PracticalModel>)

    @Update
    suspend fun updateSeminarGrade(dateAndGrade: List<SeminarModel>)
}

