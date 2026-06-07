package ua.zxcode.sololevelingapp.data.repository.impl

import ua.zxcode.sololevelingapp.data.local.dao.AchievementDao
import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity
import ua.zxcode.sololevelingapp.data.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao
) : AchievementRepository {

    override fun observeAllAchievements(): Flow<List<AchievementEntity>> =
        achievementDao.observeAllAchievements()

    override fun observeUnlockedAchievements(): Flow<List<AchievementEntity>> =
        achievementDao.observeUnlockedAchievements()

    override suspend fun getAchievementById(achievementId: String): AchievementEntity? =
        achievementDao.getAchievementById(achievementId)

    override suspend fun insertAchievement(achievement: AchievementEntity) =
        achievementDao.insertAchievement(achievement)

    override suspend fun insertAchievements(achievements: List<AchievementEntity>) =
        achievementDao.insertAchievements(achievements)

    override suspend fun updateAchievement(achievement: AchievementEntity) =
        achievementDao.updateAchievement(achievement)

    override suspend fun unlockAchievement(achievementId: String, timestamp: Long) =
        achievementDao.unlockAchievement(achievementId, timestamp)
}
