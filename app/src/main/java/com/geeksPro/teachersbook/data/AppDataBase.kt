package com.geeksPro.teachersbook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.geeksPro.teachersbook.data.conversion.GroupsTypeConverter
import com.geeksPro.teachersbook.data.local.dao.GeneralPointDao
import com.geeksPro.teachersbook.data.local.dao.GroupDao
import com.geeksPro.teachersbook.data.local.dao.LaboratoryDao
import com.geeksPro.teachersbook.data.local.dao.LectureDao
import com.geeksPro.teachersbook.data.local.dao.PracticalDao
import com.geeksPro.teachersbook.data.local.dao.SeminarDao
import com.geeksPro.teachersbook.data.local.dao.StudentDao
import com.geeksPro.teachersbook.data.local.dao.StudentScoreDao
import com.geeksPro.teachersbook.data.local.dao.SubjectDao
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.data.local.models.StudentScoreModel
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import javax.inject.Singleton

@Database(
    entities = [
        SubjectModel::class,
        GroupModel::class,
        StudentModel::class,
        StudentScoreModel::class,
        LectureModel::class,
        LaboratoryModel::class,
        PracticalModel::class,
        SeminarModel::class
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(GroupsTypeConverter::class)
@Singleton
abstract class AppDataBase : RoomDatabase() {

    abstract fun getSubjectDao(): SubjectDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getStudentDao(): StudentDao
    abstract fun getStudentScoreDao(): StudentScoreDao
    abstract fun getGeneralPointDao(): GeneralPointDao
    abstract fun getLectureDao(): LectureDao
    abstract fun getLaboratoryDao(): LaboratoryDao
    abstract fun getPracticalDao(): PracticalDao
    abstract fun getSeminarDao(): SeminarDao
}