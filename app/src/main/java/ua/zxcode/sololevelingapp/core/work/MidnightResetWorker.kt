package ua.zxcode.sololevelingapp.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.firstOrNull
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.presentation.achievements.AchievementIds
import ua.zxcode.sololevelingapp.presentation.achievements.computeRank
import java.time.LocalDate

/**
 * Запускається щоночі о 00:00.
 * Перевіряє виконання квестів і або нараховує штраф, або скидає квести на новий день.
 */
class MidnightResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(context)
        val questDao = database.questDao()
        val userDao = database.userDao()

        val allQuests = questDao.observeAllQuests().firstOrNull() ?: emptyList()
        val user = userDao.observeUser().firstOrNull() ?: return Result.success()

        val today = LocalDate.now().toString() // "yyyy-MM-dd"

        // Захист від подвійного запуску в той самий день
        if (user.lastResetDate == today) {
            return Result.success()
        }

        // Квести вважаються "не виконаними" тільки якщо хоча б один має progress > 0 або isCompleted == false
        // і при цьому target > 0 (тобто реально існуючі квести, не просто щойно скинуті)
        val hasAnyProgress = allQuests.any { it.progress > 0 || it.isCompleted }
        val uncompletedQuests = allQuests.filter { !it.isCompleted }
        val hadPenalty: Boolean

        if (allQuests.isEmpty()) {
            // Квестів немає — нічого не робимо
            hadPenalty = false
        } else if (hasAnyProgress && uncompletedQuests.isNotEmpty()) {
            // ── ШТРАФ: були активні квести, але не всі виконані ──
            hadPenalty = true
            uncompletedQuests.forEach { quest ->
                val newTarget = if (!quest.isPenalty) {
                    (quest.originalTarget * 1.5).toInt().coerceAtLeast(quest.originalTarget + 1)
                } else {
                    quest.target // Якщо вже штраф — не збільшуємо знову
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
            // Також скидаємо виконані квести для нового дня
            allQuests.filter { it.isCompleted }.forEach { quest ->
                questDao.updateQuest(
                    quest.copy(
                        isCompleted = false,
                        progress = 0
                    )
                )
            }
        } else {
            // ── УСПІХ: всі квести виконані або прогресу не було (перший день) ──
            hadPenalty = false

            // Нараховуємо досягнення "ідеальний день"
            val achievementDao = database.achievementDao()
            if (hasAnyProgress) {
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
            }

            // Скидаємо всі квести
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

        // Зберігаємо дату скиду і статус штрафу
        userDao.updateUser(
            user.copy(
                lastResetDate = today,
                hadPenaltyYesterday = hadPenalty
            )
        )

        return Result.success()
    }
}
