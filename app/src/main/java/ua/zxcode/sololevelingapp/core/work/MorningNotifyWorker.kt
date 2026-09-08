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
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase

class MorningNotifyWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "daily_quest_notifications"
        const val MORNING_NOTIFICATION_ID = 401
    }

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(context)
        val userDao = database.userDao()
        val questDao = database.questDao()

        val user = userDao.observeUser().firstOrNull() ?: return Result.success()
        val isNotificationsEnabled = user.isSoundEnabled
        if (!isNotificationsEnabled) return Result.success()

        val allQuests = questDao.observeAllQuests().firstOrNull() ?: emptyList()
        if (allQuests.isEmpty()) return Result.success()

        if (user.hadPenaltyYesterday) {
            sendNotification(
                title = "⚠️ СИСТЕМА: Штраф Активовано!",
                message = "Вчора ви не виконали всі щоденні квести та отримали штраф. Цілі квестів збільшено на 50%. Не відкладайте — виконайте квести сьогодні!"
            )
        } else {
            sendNotification(
                title = "⚔️ СИСТЕМА: Нові Щоденні Квести!",
                message = "Новий день — нові можливості стати сильнішим! Відкрийте додаток і виконайте свої щоденні квести."
            )
        }

        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(MORNING_NOTIFICATION_ID, notification)
    }
}
