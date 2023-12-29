package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geeksPro.teachersbook.data.local.models.StudentScoreModel

@Dao
interface StudentScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: StudentScoreModel)

    @Query("SELECT * FROM studentScores WHERE id = :studentId")
    suspend fun getScores(studentId: Long): StudentScoreModel?
}