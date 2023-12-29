package com.geeksPro.teachersbook.data.repositories

import android.util.Log
import com.geeksPro.teachersbook.data.local.dao.GeneralPointDao
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import javax.inject.Inject

class GeneralPointRepository @Inject constructor(val dao: GeneralPointDao) {
    suspend fun getPracticalDatesAndGradesForStudent(studentId: Long): List<PracticalModel> {
        return dao.getPracticalDatesAndGradesForStudent(studentId)
    }

    suspend fun getLaboratoryDatesAndGradesForStudent(studentId: Long): List<LaboratoryModel> {
        return dao.getLaboratoryDatesAndGradesForStudent(studentId)
    }

    suspend fun getSeminarDatesAndGradesForStudent(studentId: Long): List<SeminarModel> {
        return dao.getSeminarDatesAndGradesForStudent(studentId)
    }

    suspend fun updateLaboratoryGrade(dateAndGrade: List<LaboratoryModel>) {
        Log.e("GPR", "Updating laboratory grades: $dateAndGrade")
        dao.updateLaboratoryGrade(dateAndGrade)
    }

    suspend fun updatePracticalGrade(dateAndGrade: List<PracticalModel>) {
        dao.updatePracticalGrade(dateAndGrade)
    }

    suspend fun updateSeminarGrade(dateAndGrade: List<SeminarModel>) {
        dao.updateSeminarGrade(dateAndGrade)
    }
}