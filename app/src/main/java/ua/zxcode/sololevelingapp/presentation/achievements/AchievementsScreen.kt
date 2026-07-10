package ua.zxcode.sololevelingapp.presentation.achievements

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity
import ua.zxcode.sololevelingapp.data.repository.impl.AchievementRepositoryImpl
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground

@Composable
fun AchievementsScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val achievementRepository = remember(context) {
        AchievementRepositoryImpl(AppDatabase.getInstance(context).achievementDao())
    }
    val achievements by achievementRepository.observeAllAchievements().collectAsState(initial = emptyList())

    // Seed default achievements if DB is empty
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val existing = achievementRepository.getAchievementById(AchievementIds.SYSTEM_ARCHITECT)
            if (existing == null) {
                achievementRepository.insertAchievements(defaultAchievements())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF141C2B)
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
                val displayList = if (achievements.isEmpty()) defaultAchievements() else achievements
                items(displayList) { achievement ->
                    AchievementItem(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementEntity) {
    val rankIndex = achievement.currentRank
    val hasRank = rankIndex >= 0
    val rankLabel = if (hasRank) RANK_LABELS[rankIndex] else "—"
    val rankColor = if (hasRank) Color(RANK_COLORS[rankIndex]) else Color(0xFF4A5568)
    val iconTint = if (hasRank) rankColor else Color(0xFF4A5568)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .background(
                color = Color(0xFF1A2236).copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = rankColor.copy(alpha = if (hasRank) 0.5f else 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .background(
                        color = rankColor.copy(alpha = if (hasRank) 0.15f else 0.08f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "РАНГ $rankLabel",
                    color = rankColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Icon with glow
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Icon(
                        painter = painterResource(id = achievement.iconResId),
                        contentDescription = null,
                        tint = rankColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                renderEffect = RenderEffect
                                    .createBlurEffect(12f, 12f, Shader.TileMode.DECAL)
                                    .asComposeRenderEffect()
                            }
                    )
                }
                Icon(
                    painter = painterResource(id = achievement.iconResId),
                    contentDescription = achievement.title,
                    tint = iconTint,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = achievement.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress text
            Text(
                text = progressText(achievement),
                color = rankColor.copy(alpha = 0.85f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 13.sp
            )
        }
    }
}