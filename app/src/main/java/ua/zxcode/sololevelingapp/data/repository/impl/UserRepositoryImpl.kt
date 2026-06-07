package ua.zxcode.sololevelingapp.data.repository.impl

import ua.zxcode.sololevelingapp.data.local.dao.UserDao
import ua.zxcode.sololevelingapp.data.local.entity.UserEntity
import ua.zxcode.sololevelingapp.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun observeUser(): Flow<UserEntity?> = userDao.observeUser()

    override suspend fun getUser(): UserEntity? = userDao.getUser()

    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    override suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
}
