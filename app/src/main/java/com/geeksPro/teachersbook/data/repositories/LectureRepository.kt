package com.geeksPro.teachersbook.data.repositories

import com.geeksPro.teachersbook.data.local.dao.LectureDao
import com.geeksPro.teachersbook.data.local.models.LectureModel
import javax.inject.Inject

class LectureRepository @Inject constructor(
    private val lectureDao: LectureDao,
) {

    suspend fun getAll() = lectureDao.getAllStudent()

    suspend fun insertLecture(lecture: LectureModel) = lectureDao.insertLecture(lecture)

    suspend fun insertLectures(lectures: List<LectureModel>) = lectureDao.insertLectures(lectures)

    suspend fun getLecturesByGroupIdAndDate(groupId: Long, date: String) =
        lectureDao.getLecturesByGroupIdAndDate(groupId, date)

    suspend fun getLecturesCountVisits(groupId: Long, visits: Boolean) =
        lectureDao.getLecturesCountVisits(groupId, visits)

    suspend fun getLecturesByStudentIdAndMonth(studentId: Long, month: String) =
        lectureDao.getLecturesByStudentIdAndMonth(studentId, month)
}