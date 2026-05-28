package ua.zxcode.sololevelingapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.activity.enableEdgeToEdge
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

        setContent {
            SoloLevelingAppTheme {
                AppNavigation()
            }
        }
    }
}