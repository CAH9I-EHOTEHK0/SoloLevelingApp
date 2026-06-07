package ua.zxcode.sololevelingapp.data.repository

import ua.zxcode.sololevelingapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<UserEntity?>
    suspend fun getUser(): UserEntity?
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
}
