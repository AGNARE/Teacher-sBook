package com.geeksPro.teachersbook.data.local.models

data class UpdatePracticals(
    val id: Long,
    val date: String,
    val grades: Int
)
data class UpdateLaboratory(
    val id: Long,
    val date: String,
    val grades: Int
)
data class UpdateSeminars(
    val id: Long,
    val date: String,
    val grades: Int
)

