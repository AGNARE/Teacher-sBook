package com.geeksPro.teachersbook.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.geeksPro.teachersbook.data.local.models.GroupModel
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups")
    fun getAll(): Flow<List<GroupModel>>

    @Query("SELECT * FROM groups WHERE subjectId = :subjectId")
    suspend fun getGroupsForSubject(subjectId: Long): List<GroupModel>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroup(groupId: Long): LiveData<GroupModel>

    @Insert
    suspend fun insert(groupModel: GroupModel)

    @Delete
    suspend fun delete(groupModel: GroupModel)

    @Update
    suspend fun update(groupModel: GroupModel)
}