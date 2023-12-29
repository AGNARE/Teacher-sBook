package com.geeksPro.teachersbook.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.geeksPro.teachersbook.data.conversion.GroupsTypeConverter
import androidx.room.Relation

@Entity(tableName = "subject")
data class SubjectModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val nameSubject: String,
    var isSelected: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val typeClasses: Int = 0,
    @TypeConverters(GroupsTypeConverter::class)
    @ColumnInfo(name = "groupsCreated")
    val groups: List<GroupModel>? = null
)

@Entity(tableName = "groups")
data class GroupModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val nameGroup: String,
    val subjectId: Long? = null,
    @ColumnInfo(defaultValue = "0")
    var isSelectedGroup: Boolean = false,
)

@Entity(tableName = "students")
data class StudentModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val surname: String,
    val groupId: Long?
)

@Entity(
    tableName = "lectures",
    foreignKeys = [ForeignKey(
        entity = StudentModel::class,
        parentColumns = ["id"],
        childColumns = ["studentId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["studentId", "date"], unique = true)]
)

data class LectureModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val studentId: Long,
    val groupId: Long,
    val date: String,
    var visits: Boolean
)

@Entity(
    tableName = "laboratories",
    foreignKeys = [ForeignKey(
        entity = StudentModel::class,
        parentColumns = ["id"],
        childColumns = ["studentId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["studentId", "date"], unique = true)]
)

data class LaboratoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val studentId: Long,
    val groupId: Long,
    val date: String,
    var grades: Int,
    var visits: Boolean = false
)

@Entity(
    tableName = "practicals",
    foreignKeys = [ForeignKey(
        entity = StudentModel::class,
        parentColumns = ["id"],
        childColumns = ["studentId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["studentId", "date"], unique = true)]
)

data class PracticalModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val studentId: Long,
    val groupId: Long,
    val date: String,
    val grades: Int,
    var visits: Boolean = false
)

@Entity(
    tableName = "seminars",
    foreignKeys = [ForeignKey(
        entity = StudentModel::class,
        parentColumns = ["id"],
        childColumns = ["studentId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["studentId", "date"], unique = true)]
)

data class SeminarModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val studentId: Long,
    val groupId: Long,
    val date: String,
    val grades: Int,
    var visits: Boolean = false
)

data class StudentWithDetailsModel(
    @Embedded val student: StudentModel,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val lectures: List<LectureModel>,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val laboratories: List<LaboratoryModel>,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val practicals: List<PracticalModel>,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val seminars: List<SeminarModel>
)

@Entity(tableName = "studentScores")
data class StudentScoreModel(
    @PrimaryKey
    val id: Long,
    val pointModuleFirst: String,
    val pointModuleSecond: String,
    val finalControlPoint: String,
    val additionallyPoint: String,
    val totalScorePoint: String,
    val estimationPoint: String,
)