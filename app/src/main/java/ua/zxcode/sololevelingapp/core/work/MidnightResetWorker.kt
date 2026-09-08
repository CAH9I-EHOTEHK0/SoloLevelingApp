package ua.zxcode.sololevelingapp.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalTime

class MidnightResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val hour = LocalTime.now().hour

        if (hour in 6..23) {
            return Result.success()
        }

        DailyResetManager.checkAndPerformReset(context)
        return Result.success()
    }
}

