package com.geeksPro.teachersbook.core.di

import android.content.Context
import androidx.room.Room
import com.geeksPro.teachersbook.data.AppDataBase
import com.geeksPro.teachersbook.data.local.dao.GeneralPointDao
import com.geeksPro.teachersbook.data.local.dao.GroupDao
import com.geeksPro.teachersbook.data.local.dao.LaboratoryDao
import com.geeksPro.teachersbook.data.local.dao.LectureDao
import com.geeksPro.teachersbook.data.local.dao.PracticalDao
import com.geeksPro.teachersbook.data.local.dao.SeminarDao
import com.geeksPro.teachersbook.data.local.dao.StudentDao
import com.geeksPro.teachersbook.data.local.dao.StudentScoreDao
import com.geeksPro.teachersbook.data.local.dao.SubjectDao
import com.geeksPro.teachersbook.data.repositories.GroupRepository
import com.geeksPro.teachersbook.data.repositories.LaboratoryRepository
import com.geeksPro.teachersbook.data.repositories.LectureRepository
import com.geeksPro.teachersbook.data.repositories.PracticalRepository
import com.geeksPro.teachersbook.data.repositories.SeminarRepository
import com.geeksPro.teachersbook.data.repositories.StudentRepository
import com.geeksPro.teachersbook.data.repositories.SubjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDataBase {
        return Room.databaseBuilder(appContext, AppDataBase::class.java, "DB_subject").build()
    }

    @Provides
    fun provideStudentScoreDao(database: AppDataBase): StudentScoreDao {
        return database.getStudentScoreDao()
    }
    @Provides
    fun provideGeneralPointDoa(database: AppDataBase): GeneralPointDao {
        return database.getGeneralPointDao()
    }

    @Provides
    fun provideSubjectDao(database: AppDataBase): SubjectDao {
        return database.getSubjectDao()
    }

    @Provides
    fun provideSubjectRepository(subjectDao: SubjectDao): SubjectRepository {
        return SubjectRepository(subjectDao)
    }

    @Provides
    fun provideGroupDao(database: AppDataBase): GroupDao {
        return database.getGroupDao()
    }

    @Provides
    fun provideGroupRepository(groupDao: GroupDao): GroupRepository {
        return GroupRepository(groupDao)
    }

    @Provides
    fun provideStudentDao(database: AppDataBase): StudentDao {
        return database.getStudentDao()
    }

    @Provides
    fun provideStudentRepository(studentDao: StudentDao): StudentRepository {
        return StudentRepository(studentDao)
    }

    @Provides
    fun provideLectureDao(database: AppDataBase): LectureDao {
        return database.getLectureDao()
    }

    @Provides
    fun provideLectureRepository(lectureDao: LectureDao): LectureRepository {
        return LectureRepository(lectureDao)
    }

    @Provides
    fun provideLaboratoryDao(database: AppDataBase): LaboratoryDao {
        return database.getLaboratoryDao()
    }

    @Provides
    fun provideLaboratoryRepository(laboratoryDao: LaboratoryDao): LaboratoryRepository {
        return LaboratoryRepository(laboratoryDao)
    }

    @Provides
    fun providePracticalDao(database: AppDataBase): PracticalDao {
        return database.getPracticalDao()
    }

    @Provides
    fun providePracticalRepository(practicalDao: PracticalDao): PracticalRepository {
        return PracticalRepository(practicalDao)
    }

    @Provides
    fun provideSeminarDao(database: AppDataBase): SeminarDao {
        return database.getSeminarDao()
    }

    @Provides
    fun provideSeminarRepository(seminarDao: SeminarDao): SeminarRepository {
        return SeminarRepository(seminarDao)
    }
}