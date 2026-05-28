package ua.zxcode.sololevelingapp.presentation.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

@Composable
fun AchievementsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141C2B),
                        Color(0xFF120A1F),
                    )
                )
            ), // Градієнтний фон
        contentAlignment = Alignment.Center
    ) {
        // Наш кастомний анімований фон з паралелепіпедами
        SoloLevelingBackground()

        // Вміст екрана поверх фону
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ACHIEVEMENTS & TITLES",
                color = Color(0xFFBB86FC),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}