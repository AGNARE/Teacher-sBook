package com.geeksPro.teachersbook.data.repositories

import com.geeksPro.teachersbook.data.local.dao.PracticalDao
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import javax.inject.Inject

class PracticalRepository @Inject constructor(
    private val practicalDao: PracticalDao,
) {

    suspend fun getAll() = practicalDao.getAllStudent()

    suspend fun insertPractical(practical: PracticalModel) = practicalDao.insertPractical(practical)

    suspend fun insertPracticals(practicals: List<PracticalModel>) =
        practicalDao.insertPracticals(practicals)

    suspend fun getPracticalsByGroupIdAndDate(groupId: Long, date: String) =
        practicalDao.getPracticalsByGroupIdAndDate(groupId, date)

    suspend fun getPracticalsCountVisits(groupId: Long, visits: Boolean) =
        practicalDao.getPracticalsCountVisits(groupId, visits)

    suspend fun getPracticalsCountGrades(groupId: Long, grades: Int = 0) =
        practicalDao.getPracticalsCountGrades(groupId, grades)

    suspend fun getSumOfGradesByGroupId(groupId: Long) =
        practicalDao.getSumOfGradesByGroupId(groupId)

    suspend fun getPracticalsByStudentIdAndMonth(studentId: Long, month: String) =
        practicalDao.getPracticalsByStudentIdAndMonth(studentId, month)
}