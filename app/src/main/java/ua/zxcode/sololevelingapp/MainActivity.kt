package ua.zxcode.sololevelingapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.activity.enableEdgeToEdge
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ua.zxcode.sololevelingapp.core.navigation.AppNavigation
import ua.zxcode.sololevelingapp.core.work.EveningReminderWorker
import ua.zxcode.sololevelingapp.core.work.MidnightResetWorker
import ua.zxcode.sololevelingapp.core.work.MorningNotifyWorker
import ua.zxcode.sololevelingapp.ui.theme.SoloLevelingAppTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        scheduleAllWorkers(this)

        setContent {
            SoloLevelingAppTheme {
                AppNavigation()
            }
        }
    }

    // Schedule background synchronization and notifications
    private fun scheduleAllWorkers(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Midnight Reset (00:00)
        val midnightRequest = PeriodicWorkRequestBuilder<MidnightResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayToNextHour(hour = 0, minute = 0), TimeUnit.MILLISECONDS)
            .addTag("MidnightResetTag")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "MidnightResetWork",
            ExistingPeriodicWorkPolicy.KEEP,
            midnightRequest
        )

        // Morning Notification (08:00)
        val morningRequest = PeriodicWorkRequestBuilder<MorningNotifyWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayToNextHour(hour = 8, minute = 0), TimeUnit.MILLISECONDS)
            .addTag("MorningNotifyTag")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "MorningNotifyWork",
            ExistingPeriodicWorkPolicy.KEEP,
            morningRequest
        )

        // Evening Reminder (19:00)
        val eveningRequest = PeriodicWorkRequestBuilder<EveningReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayToNextHour(hour = 19, minute = 0), TimeUnit.MILLISECONDS)
            .addTag("EveningReminderTag")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "EveningReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            eveningRequest
        )
    }

    // Calculates delay until the next occurrence of the specified hour
    private fun delayToNextHour(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
