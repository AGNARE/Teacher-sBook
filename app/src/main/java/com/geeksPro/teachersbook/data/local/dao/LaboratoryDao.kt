package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel

@Dao
interface LaboratoryDao {
    @Query("SELECT * FROM laboratories")
    suspend fun getAllStudent(): List<LaboratoryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaboratory(laboratory: LaboratoryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaboratories(laboratories: List<LaboratoryModel>)

    @Query("SELECT * FROM laboratories WHERE groupId = :groupId AND date = :date")
    suspend fun getLaboratoriesByGroupIdAndDate(groupId: Long, date: String): List<LaboratoryModel>

    @Query("SELECT COUNT(*) FROM laboratories WHERE groupId = :groupId AND visits = :visits")
    suspend fun getLaboratoriesCountVisits(groupId: Long, visits: Boolean): Int

    @Query("SELECT COUNT(*) FROM laboratories WHERE groupId = :groupId AND grades > :grades")
    suspend fun getLaboratoriesCountGrades(groupId: Long, grades: Int): Int

    @Query("SELECT SUM(grades) FROM laboratories WHERE groupId = :groupId")
    suspend fun getSumOfGradesByGroupId(groupId: Long): Int?

    @Query("SELECT * FROM laboratories WHERE studentId = :studentId AND date LIKE '%' || :month || '%'")
    suspend fun getLaboratoriesByStudentIdAndMonth(studentId: Long, month: String): List<LaboratoryModel>
}