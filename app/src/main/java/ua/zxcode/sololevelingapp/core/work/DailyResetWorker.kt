package ua.zxcode.sololevelingapp.core.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.firstOrNull
import ua.zxcode.sololevelingapp.MainActivity
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity

class DailyResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "daily_quest_notifications"
        private const val NOTIFICATION_ID = 404
        private const val PENALTY_NOTIFICATION_ID = 505
    }

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(context)
        val questDao = database.questDao()
        val userDao = database.userDao()

        val allQuests = questDao.observeAllQuests().firstOrNull() ?: emptyList()
        val user = userDao.observeUser().firstOrNull()

        // Check if notifications are enabled
        val isNotificationsEnabled = user?.isSoundEnabled ?: true

        // Find quests that were NOT completed
        val uncompletedQuests = allQuests.filter { !it.isCompleted }

        if (uncompletedQuests.isNotEmpty()) {
            // Apply Penalty:
            // 1. Double/1.5x target for penalty quests. (1.5x as requested)
            // 2. Set isPenalty = true
            uncompletedQuests.forEach { quest ->
                val newTarget = if (!quest.isPenalty) {
                    (quest.originalTarget * 1.5).toInt().coerceAtLeast(quest.originalTarget + 1)
                } else {
                    quest.target // Penalty is already active, don't stack it more than once as requested (одноразово)
                }

                questDao.updateQuest(
                    quest.copy(
                        target = newTarget,
                        isPenalty = true,
                        progress = 0
                    )
                )
            }

            if (isNotificationsEnabled) {
                sendNotification(
                    title = "СИСТЕМА: Отримано Штраф!",
                    message = "Ви не виконали daily квести вчора. Цілі збільшено на 50%, досвід за виконання квесту: +15 XP.",
                    notificationId = PENALTY_NOTIFICATION_ID
                )
            }
        } else {
            // Reset regular/penalty quests for the next day
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

            if (isNotificationsEnabled && allQuests.isNotEmpty()) {
                sendNotification(
                    title = "СИСТЕМА: Нові Щоденні Квести!",
                    message = "Новий день почався. Час виконати ваші квести!",
                    notificationId = NOTIFICATION_ID
                )
            }
        }

        return Result.success()
    }

    private fun sendNotification(title: String, message: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Quest Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "System notifications for daily quests progression and penalties."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Using standard system icon fallback if R.drawable.icon is not ready or has compile errors, or vector drawables.
        // We'll use android.R.drawable.ic_dialog_info to be 100% safe.
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
