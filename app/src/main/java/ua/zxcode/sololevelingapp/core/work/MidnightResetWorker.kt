package ua.zxcode.sololevelingapp.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalTime

/**
 * Запускається щоночі о 00:00.
 * Запобігає нарахуванню штрафів вдень у разі запізнілого фонового запуску.
 */
class MidnightResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val hour = LocalTime.now().hour

        // Якщо таска запустилась вдень (наприклад, через запізнення WorkManager),
        // ігноруємо фоновий скид, щоб не збити поточний день користувача.
        if (hour in 6..23) {
            return Result.success()
        }

        DailyResetManager.checkAndPerformReset(context)
        return Result.success()
    }
}

