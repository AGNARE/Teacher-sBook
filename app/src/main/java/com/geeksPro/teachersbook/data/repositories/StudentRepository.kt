package com.geeksPro.teachersbook.data.repositories


import com.geeksPro.teachersbook.data.local.dao.StudentDao
import com.geeksPro.teachersbook.data.local.models.StudentModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val studentDao: StudentDao,
) {

    suspend fun getStudentsAlphabetically(groupId: Long) = studentDao.getStudentsAlphabetically(groupId)
//    fun getGroupWithStudents(groupId: Long) = studentDao.getGroupWithStudents(groupId)

    suspend fun getStudentWithDetails(studentId: Long) = studentDao.getStudentWithDetails(studentId)

    suspend fun insertStudent(student: StudentModel) = studentDao.insertStudent(student)

    suspend fun updateStudent(student: StudentModel) = studentDao.updateStudent(student)

    fun findByNameOrSurname(search: String) = studentDao.findByNameOrSurname(search)

    suspend fun getStudentCountByGroupId(groupId: Long) =
        studentDao.getStudentCountByGroupId(groupId)

    suspend fun deleteStudent(student: StudentModel) = studentDao.deleteStudent(student)

    suspend fun deleteByIds(ids: List<Long>) = studentDao.deleteByIds(ids)
}