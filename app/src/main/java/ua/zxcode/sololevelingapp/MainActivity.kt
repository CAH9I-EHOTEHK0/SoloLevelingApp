package ua.zxcode.sololevelingapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.activity.enableEdgeToEdge
import android.content.Context
import ua.zxcode.sololevelingapp.core.navigation.AppNavigation
import ua.zxcode.sololevelingapp.ui.theme.SoloLevelingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // <- ПЕРШИМ, до всього іншого
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // 3. Відключаємо примусове малювання тіней на нових версіях Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        // 4. (Опціонально) якщо ви хочете щоб іконки мережі і годинника завжди були світлі:
        // androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).apply {
        //     isAppearanceLightStatusBars = false // false - світлі іконки (для темного фону), true - темні іконки
        //     isAppearanceLightNavigationBars = false
        // }

        // Request runtime permission for notifications on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Schedule Daily Reset/Checks via WorkManager
        scheduleDailyResetWork(this)

        setContent {
            SoloLevelingAppTheme {
                AppNavigation()
            }
        }
    }

    private fun scheduleDailyResetWork(context: Context) {
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<ua.zxcode.sololevelingapp.core.work.DailyResetWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelayToMidnight(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .addTag("DailyResetWorkTag")
            .build()

        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyResetWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun calculateInitialDelayToMidnight(): Long {
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis - now
    }
}