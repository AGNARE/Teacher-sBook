package com.geeksPro.teachersbook.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.local.models.StudentWithDetailsModel
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    suspend fun getAllStudent(): List<StudentModel>

    @Query("SELECT * FROM students WHERE groupId = :groupId ORDER BY surname ASC")
    suspend fun getStudentsAlphabetically(groupId: Long): List<StudentModel>

//    @Transaction
//    @Query("SELECT * FROM groups WHERE id = :groupId ORDER BY (SELECT surname FROM students WHERE groupId = :groupId) ASC")
//    fun getGroupWithStudents(groupId: Long): Flow<GroupWithStudents?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentModel)

    @Update
    suspend fun updateStudent(studentModel: StudentModel)

    @Query("SELECT * FROM students WHERE name LIKE :search || '%' OR surname LIKE :search || '%'")
    fun findByNameOrSurname(search: String): Flow<List<StudentModel>>

    @Transaction
    @Query("SELECT COUNT(*) FROM students WHERE groupId = :groupId")
    suspend fun getStudentCountByGroupId(groupId: Long): Int

    @Delete
    suspend fun deleteStudent(studentModel: StudentModel)

    @Transaction
    @Query("DELETE FROM students WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentWithDetails(studentId: Long): StudentWithDetailsModel
}