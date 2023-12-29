package com.geeksPro.teachersbook.data.repositories

import com.geeksPro.teachersbook.data.local.dao.LaboratoryDao
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import javax.inject.Inject

class LaboratoryRepository @Inject constructor(
    private val laboratoryDao: LaboratoryDao,
) {

    suspend fun getAll() = laboratoryDao.getAllStudent()

    suspend fun insertLaboratory(laboratory: LaboratoryModel) =
        laboratoryDao.insertLaboratory(laboratory)

    suspend fun insertLaboratories(laboratories: List<LaboratoryModel>) =
        laboratoryDao.insertLaboratories(laboratories)

    suspend fun getLaboratoriesByGroupIdAndDate(groupId: Long, date: String) =
        laboratoryDao.getLaboratoriesByGroupIdAndDate(groupId, date)

    suspend fun getLaboratoriesCountVisits(groupId: Long, visits: Boolean) =
        laboratoryDao.getLaboratoriesCountVisits(groupId, visits)

    suspend fun getLaboratoriesCountGrades(groupId: Long, grades: Int = 0) =
        laboratoryDao.getLaboratoriesCountGrades(groupId, grades)

    suspend fun getSumOfGradesByGroupId(groupId: Long) =
        laboratoryDao.getSumOfGradesByGroupId(groupId)

    suspend fun getLaboratoriesByStudentIdAndMonth(studentId: Long, month: String) =
        laboratoryDao.getLaboratoriesByStudentIdAndMonth(studentId, month)
}