package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.studentPerformance

import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel

data class VisitsModel(
    val typeClasses: Int,
    val id: Long,
    val studentId: Long,
    val groupId: Long,
    val date: String,
    val grades: Int,
    var visits: Boolean
)

fun LectureModel.toVisitsModel(typeClasses: Int) = VisitsModel(
    typeClasses = typeClasses,
    id = this.id!!,
    studentId = this.studentId,
    groupId = this.groupId,
    date = this.date,
    grades = 0,
    visits = this.visits,
)

fun LaboratoryModel.toVisitsModel(typeClasses: Int) = VisitsModel(
    typeClasses = typeClasses,
    id = this.id!!,
    studentId = this.studentId,
    groupId = this.groupId,
    date = this.date,
    grades = this.grades,
    visits = this.visits,
)

fun PracticalModel.toVisitsModel(typeClasses: Int) = VisitsModel(
    typeClasses = typeClasses,
    id = this.id!!,
    studentId = this.studentId,
    groupId = this.groupId,
    date = this.date,
    grades = this.grades,
    visits = this.visits,
)

fun SeminarModel.toVisitsModel(typeClasses: Int) = VisitsModel(
    typeClasses = typeClasses,
    id = this.id!!,
    studentId = this.studentId,
    groupId = this.groupId,
    date = this.date,
    grades = this.grades,
    visits = this.visits,
)

fun List<LectureModel>.toLectureVisitsModel(typeClasses: Int) =
    this.map { it.toVisitsModel(typeClasses) }

fun List<LaboratoryModel>.toLaboratoryVisitsModel (typeClasses: Int) =
    this.map { it.toVisitsModel(typeClasses) }

fun List<PracticalModel>.toPracticalVisitsModel (typeClasses: Int) =
    this.map { it.toVisitsModel(typeClasses) }

fun List<SeminarModel>.toSeminarVisitsModel (typeClasses: Int) =
    this.map { it.toVisitsModel(typeClasses) }