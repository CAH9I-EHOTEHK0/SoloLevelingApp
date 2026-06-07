package ua.zxcode.sololevelingapp.data.repository

import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAllAchievements(): Flow<List<AchievementEntity>>
    fun observeUnlockedAchievements(): Flow<List<AchievementEntity>>
    suspend fun getAchievementById(achievementId: String): AchievementEntity?
    suspend fun insertAchievement(achievement: AchievementEntity)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)
    suspend fun updateAchievement(achievement: AchievementEntity)
    suspend fun unlockAchievement(achievementId: String, timestamp: Long)
}