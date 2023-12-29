package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel

@Dao
interface PracticalDao {
    @Query("SELECT * FROM practicals")
    suspend fun getAllStudent(): List<PracticalModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPractical(practical: PracticalModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticals(practicals: List<PracticalModel>)

    @Query("SELECT * FROM practicals WHERE groupId = :groupId AND date = :date")
    suspend fun getPracticalsByGroupIdAndDate(groupId: Long, date: String): List<PracticalModel>

    @Query("SELECT COUNT(*) FROM practicals WHERE groupId = :groupId AND visits = :visits")
    suspend fun getPracticalsCountVisits(groupId: Long, visits: Boolean): Int

    @Query("SELECT COUNT(*) FROM practicals WHERE groupId = :groupId AND grades > :grades")
    suspend fun getPracticalsCountGrades(groupId: Long, grades: Int): Int

    @Query("SELECT SUM(grades) FROM practicals WHERE groupId = :groupId")
    suspend fun getSumOfGradesByGroupId(groupId: Long): Int?

    @Query("SELECT * FROM practicals WHERE studentId = :studentId AND date LIKE '%' || :month || '%'")
    suspend fun getPracticalsByStudentIdAndMonth(studentId: Long, month: String): List<PracticalModel>
}