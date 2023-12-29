package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel

@Dao
interface SeminarDao {
    @Query("SELECT * FROM seminars")
    suspend fun getAllStudent(): List<SeminarModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeminar(seminarModel: SeminarModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeminars(seminars: List<SeminarModel>)

    @Query("SELECT * FROM seminars WHERE groupId = :groupId AND date = :date")
    suspend fun getSeminarsByGroupIdAndDate(groupId: Long, date: String): List<SeminarModel>

    @Query("SELECT COUNT(*) FROM seminars WHERE groupId = :groupId AND visits = :visits")
    suspend fun getSeminarsCountVisits(groupId: Long, visits: Boolean): Int

    @Query("SELECT COUNT(*) FROM seminars WHERE groupId = :groupId AND grades > :grades")
    suspend fun getSeminarsCountGrades(groupId: Long, grades: Int): Int

    @Query("SELECT SUM(grades) FROM seminars WHERE groupId = :groupId")
    suspend fun getSumOfGradesByGroupId(groupId: Long): Int?

    @Query("SELECT * FROM seminars WHERE studentId = :studentId AND date LIKE '%' || :month || '%'")
    suspend fun getSeminarsByStudentIdAndMonth(studentId: Long, month: String): List<SeminarModel>
}