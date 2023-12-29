package com.geeksPro.teachersbook.data.repositories

import com.geeksPro.teachersbook.data.local.dao.SeminarDao
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import javax.inject.Inject

class SeminarRepository @Inject constructor(
    private val seminarDao: SeminarDao
) {

    suspend fun getAll() = seminarDao.getAllStudent()

    suspend fun insertSeminar(seminar: SeminarModel) = seminarDao.insertSeminar(seminar)

    suspend fun insertSeminars(seminars: List<SeminarModel>) =
        seminarDao.insertSeminars(seminars)

    suspend fun getSeminarsByGroupIdAndDate(groupId: Long, date: String) =
        seminarDao.getSeminarsByGroupIdAndDate(groupId, date)

    suspend fun getSeminarsCountVisits(groupId: Long, visits: Boolean) =
        seminarDao.getSeminarsCountVisits(groupId, visits)

    suspend fun getSeminarsCountGrades(groupId: Long, grades: Int = 0) =
        seminarDao.getSeminarsCountGrades(groupId, grades)

    suspend fun getSumOfGradesByGroupId(groupId: Long) = seminarDao.getSumOfGradesByGroupId(groupId)

    suspend fun getSeminarsByStudentIdAndMonth(studentId: Long, month: String) =
        seminarDao.getSeminarsByStudentIdAndMonth(studentId, month)
}