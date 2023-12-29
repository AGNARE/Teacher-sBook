package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel

@Dao
interface LectureDao {
    @Query("SELECT * FROM lectures")
    suspend fun getAllStudent(): List<LectureModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lectures: List<LectureModel>)

    @Query("SELECT * FROM lectures WHERE groupId = :groupId AND date = :date")
    suspend fun getLecturesByGroupIdAndDate(groupId: Long, date: String): List<LectureModel>

    @Query("SELECT COUNT(*) FROM lectures WHERE groupId = :groupId AND visits = :visits")
    suspend fun getLecturesCountVisits(groupId: Long, visits: Boolean): Int

    @Query("SELECT * FROM lectures WHERE studentId = :studentId AND date LIKE '%' || :month || '%'")
    suspend fun getLecturesByStudentIdAndMonth(studentId: Long, month: String): List<LectureModel>
}