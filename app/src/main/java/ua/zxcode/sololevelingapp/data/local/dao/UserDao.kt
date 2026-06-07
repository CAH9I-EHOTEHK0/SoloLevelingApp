package ua.zxcode.sololevelingapp.data.local.dao

import androidx.room.*
import ua.zxcode.sololevelingapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE id = 1")
    fun observeUser(): Flow<UserEntity?>

    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}