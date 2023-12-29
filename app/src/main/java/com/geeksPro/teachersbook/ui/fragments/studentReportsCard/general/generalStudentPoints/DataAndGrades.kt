package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.general.generalStudentPoints

import androidx.room.PrimaryKey

data class DateAndGrade(
    @PrimaryKey(autoGenerate = true)
    val id: Long?= null,
    val date: String,
    var grades: Int,
    val visits: Boolean
)
