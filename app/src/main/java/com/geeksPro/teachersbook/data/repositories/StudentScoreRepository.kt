package com.geeksPro.teachersbook.data.repositories

import com.geeksPro.teachersbook.data.local.dao.StudentScoreDao
import com.geeksPro.teachersbook.data.local.models.StudentScoreModel
import javax.inject.Inject

class StudentScoreRepository @Inject constructor(private val scoreDao: StudentScoreDao) {
    suspend fun saveScores(score: StudentScoreModel) {
        scoreDao.insert(score)
    }
    suspend fun getScores(studentId: Long): StudentScoreModel? {
        return scoreDao.getScores(studentId)
    }
}