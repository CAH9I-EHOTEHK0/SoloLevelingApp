package ua.zxcode.sololevelingapp.core.work

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import ua.zxcode.sololevelingapp.data.local.entity.UserEntity
import ua.zxcode.sololevelingapp.presentation.achievements.AchievementIds
import ua.zxcode.sololevelingapp.presentation.achievements.computeRank
import java.time.LocalDate

object DailyResetManager {

    suspend fun checkAndPerformReset(context: Context): Boolean = withContext(Dispatchers.IO) {
        val database = AppDatabase.getInstance(context)
        val questDao = database.questDao()
        val userDao = database.userDao()

        val user = userDao.getUser() ?: return@withContext false
        val today = LocalDate.now().toString() // "yyyy-MM-dd"

        if (user.lastResetDate == today) {
            return@withContext false
        }

        val allQuests = questDao.getAllQuestsSync()

        if (user.lastResetDate.isEmpty()) {
            userDao.updateUser(
                user.copy(
                    lastResetDate = today,
                    hadPenaltyYesterday = false
                )
            )
            return@withContext false
        }

        val uncompletedQuests = allQuests.filter { !it.isCompleted }
        val hadPenalty: Boolean

        if (allQuests.isEmpty()) {
            hadPenalty = false
        } else if (uncompletedQuests.isNotEmpty()) {
            hadPenalty = true
            uncompletedQuests.forEach { quest ->
                val newTarget = if (!quest.isPenalty) {
                    (quest.originalTarget * 1.5).toInt().coerceAtLeast(quest.originalTarget + 1)
                } else {
                    quest.target
                }
                questDao.updateQuest(
                    quest.copy(
                        target = newTarget,
                        isPenalty = true,
                        isCompleted = false,
                        progress = 0
                    )
                )
            }
            allQuests.filter { it.isCompleted }.forEach { quest ->
                questDao.updateQuest(
                    quest.copy(
                        isCompleted = false,
                        progress = 0
                    )
                )
            }
        } else {
            hadPenalty = false

            val achievementDao = database.achievementDao()
            val playersAchv = achievementDao.getAchievementById(AchievementIds.PLAYERS_IRL)
            if (playersAchv != null) {
                val newProgress = playersAchv.progress + 1L
                val newRank = computeRank(AchievementIds.PLAYERS_IRL, newProgress)
                achievementDao.updateAchievement(
                    playersAchv.copy(
                        progress = newProgress,
                        currentRank = newRank,
                        isCompleted = newRank == 5
                    )
                )
            }

            allQuests.forEach { quest ->
                questDao.updateQuest(
                    quest.copy(
                        isCompleted = false,
                        progress = 0,
                        target = quest.originalTarget,
                        isPenalty = false
                    )
                )
            }
        }

        userDao.updateUser(
            user.copy(
                lastResetDate = today,
                hadPenaltyYesterday = hadPenalty
            )
        )

        return@withContext true
    }
}
