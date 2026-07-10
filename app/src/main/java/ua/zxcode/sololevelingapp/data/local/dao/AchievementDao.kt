package ua.zxcode.sololevelingapp.data.local.dao

import androidx.room.*
import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements")
    fun observeAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}