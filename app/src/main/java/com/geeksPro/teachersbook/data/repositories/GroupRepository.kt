package com.geeksPro.teachersbook.data.repositories

import androidx.lifecycle.LiveData
import com.geeksPro.teachersbook.data.local.dao.GroupDao
import com.geeksPro.teachersbook.data.local.models.GroupModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    suspend fun insert(groupModel: GroupModel) = groupDao.insert(groupModel)
    suspend fun update(groupModel: GroupModel) = groupDao.update(groupModel)
    suspend fun delete(groupModel: GroupModel) = groupDao.delete(groupModel)
    fun getAllGroup(): Flow<List<GroupModel>> = groupDao.getAll()
    fun getGroup(groupId: Long): LiveData<GroupModel> = groupDao.getGroup(groupId)

    suspend fun getGroupsForSubject(subjectId: Long): List<GroupModel> {
        return groupDao.getGroupsForSubject(subjectId)
    }
}