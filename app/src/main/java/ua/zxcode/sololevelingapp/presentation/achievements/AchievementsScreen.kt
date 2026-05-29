package ua.zxcode.sololevelingapp.presentation.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

data class AchievementData(
    val title: String,
    val iconResId: Int
)

val achievementsList = listOf(
    AchievementData("Архітектор Системи", R.drawable.achvsystemarchitect),
    AchievementData("Бібліотека Монарха", R.drawable.achvmonarchlibrary),
    AchievementData("Лінгвістичний Поліглот", R.drawable.achvlinguisticpolyglot),
    AchievementData("Шлях до Олімпу", R.drawable.achvroadtoolympus),
    AchievementData("Гравці в реальне життя", R.drawable.achvplayersirl)
)

@Composable
fun AchievementsScreen() {
    val context = LocalContext.current
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
            ),
        contentAlignment = Alignment.Center
    ) {
        SoloLevelingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(top = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(achievementsList) { achievement ->
                    AchievementItem(
                        achievement = achievement,
                        onClick = {
                            Toast.makeText(context, "Clicked ${achievement.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp) // фіксована висота — всі картки однакові
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E283A).copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Icon(
                        painter = painterResource(id = achievement.iconResId),
                        contentDescription = null,
                        tint = Color(0xFF00E6F0),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                renderEffect = RenderEffect
                                    .createBlurEffect(10f, 10f, Shader.TileMode.DECAL)
                                    .asComposeRenderEffect()
                            }
                    )
                }

                Icon(
                    painter = painterResource(id = achievement.iconResId),
                    contentDescription = achievement.title,
                    tint = Color(0xFFB0E0E6),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = achievement.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 17.sp
            )
        }
    }
}