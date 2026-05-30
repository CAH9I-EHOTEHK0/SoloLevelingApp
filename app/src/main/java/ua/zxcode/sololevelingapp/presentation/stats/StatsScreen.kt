package ua.zxcode.sololevelingapp.presentation.stats

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.zxcode.sololevelingapp.R
//import ua.zxcode.sololevelingapp.presentation.achievements.AchievementData
//import ua.zxcode.sololevelingapp.presentation.achievements.AchievementItem
//import ua.zxcode.sololevelingapp.presentation.achievements.achievementsList
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

data class StatsData(
    val title: String,
    val iconResId: Int
)

val statsList = listOf(
    //Statsup("Маса", R.drawable.statweight),
    StatsData("Пульс", R.drawable.statpulse),
    StatsData("Кількість кроків", R.drawable.statstepsamount),
    StatsData("Сон", R.drawable.statsleeptime),
    StatsData("Рівень кисню", R.drawable.statoxygenlevel),
)
val statup = StatsData("Маса", R.drawable.statweight)
@Composable
fun StatsScreen() {
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
            ), // Градієнтний фон
        contentAlignment = Alignment.Center
    ) {
        // Наш кастомний анімований фон з паралелепіпедами
        SoloLevelingBackground()

        // Вміст екрана поверх фону
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 148.dp, start = 16.dp, end = 16.dp), // відступ зверху для заголовка
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // фіксована висота — всі картки однакові
                    .clickable(
                        onClick = {
                            Toast.makeText(context, "Clicked ${statup.title}", Toast.LENGTH_SHORT).show()
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        //indication = null
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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Icon(
                                painter = painterResource(id = statup.iconResId),
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
                            painter = painterResource(id = statup.iconResId),
                            contentDescription = statup.title,
                            tint = Color(0xFFB0E0E6),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = statup.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 17.sp
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(top = 34.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(statsList) { stat ->
                    StatItem(
                        stat = stat,
                        onClick = {
                            Toast.makeText(context, "Clicked ${stat.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(stat: StatsData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 10.dp)// фіксована висота — всі картки однакові
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Icon(
                        painter = painterResource(id = stat.iconResId),
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
                    painter = painterResource(id = stat.iconResId),
                    contentDescription = stat.title,
                    tint = Color(0xFFB0E0E6),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stat.title,
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