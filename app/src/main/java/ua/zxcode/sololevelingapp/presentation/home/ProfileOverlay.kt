package ua.zxcode.sololevelingapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProfileOverlay(
    onDismiss: () -> Unit, // Функція, яка закриє оверлей
    // Сюди потім передаси сталі дані користувача (рівень, нікнейм, налаштування)
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Дозволяє зробити вікно на всю ширину, якщо треба
        )
    ) {
        // Контент нашого оверлея (твоя картка профілю)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f) // Займає 85% ширини екрана
                .wrapContentHeight()
                .border(
                    width = 2.dp,
                    color = Color(0xFFB0E0E6), // Неоновий контур
                    //shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color(0xFF0A0A12).copy(alpha = 0.1f), // Темний RPG фон
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Заголовок (тут твій кастомний готичний шрифт буде ідеально)
                Text(
                    text = "User Profile",
                    color = Color(0xFF00E6F0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Інфа користувача (Заглушки)
                Text(text = "Користувач: ", color = Color.White, fontSize = 16.sp)




                Text(text = "Нікнейм: Сон Джин Ву", color = Color.White, fontSize = 16.sp)
                Text(text = "Поточний титул: Радість згасання", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // Блок Налаштувань (Теж заглушка)
                Text(text = "[ Звукові ефекти: Увімк ]", color = Color(0xFFB0E0E6), fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка закриття
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "ЗАКРИТИ", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}