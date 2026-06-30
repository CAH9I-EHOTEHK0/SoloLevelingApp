package ua.zxcode.sololevelingapp.presentation.home

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.repository.impl.QuestRepositoryImpl
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground
import kotlinx.coroutines.launch

private fun closeApp(context: android.content.Context) {
    (context as? Activity)?.finishAffinity()
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val questRepository = remember(context) {
        QuestRepositoryImpl(AppDatabase.getInstance(context).questDao())
    }
    val activeQuests by questRepository.observeActiveQuests().collectAsState(initial = emptyList())

    var isProfileOpen by remember { mutableStateOf(false) }
    var isQuestSettingsOpen by remember { mutableStateOf(false) }

    fun showMessage() {
        Toast.makeText(context, "Поки що повідомлення", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF141C2B), Color(0xFF120A1F))
                )
            )
    ) {
        SoloLevelingBackground()

        // Header притиснутий до топу
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            val btnSize = 58.dp
            val barHeight = 58.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .padding(horizontal = 16.dp), // мінімальний відступ від країв
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // lvlbar займає весь простір
                Box(modifier = Modifier.weight(1.3f).height(barHeight*1.3f)) {
                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.DECAL)
                                        .asComposeRenderEffect()
                                } else null
                            },
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(
                            Color(0xFF00E6F0),
                            blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.lvlbar),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // profilebtn — строго 44×44
                Box(
                    modifier = Modifier
                        .size(btnSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isProfileOpen = true // Відкриваємо оверлей при кліку!
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier
                            .size(btnSize)
                            .graphicsLayer {
                                renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    RenderEffect.createBlurEffect(14f, 14f, Shader.TileMode.DECAL)
                                        .asComposeRenderEffect()
                                } else null
                            },
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(
                            Color(0xFF00E6F0),
                            blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.profilebtn),
                        contentDescription = null,
                        modifier = Modifier.size(btnSize),
                        contentScale = ContentScale.FillBounds
                    )
                }

                if (isProfileOpen) {
                    ProfileOverlay(
                        onDismiss = { isProfileOpen = false }, // Закриваємо при натисканні назад або на кнопку
                        onQuestSettingsClick = {
                            isProfileOpen = false
                            isQuestSettingsOpen = true
                        }
                    )
                }

                if (isQuestSettingsOpen) {
                    QuestSettingsOverlay(
                        onDismiss = { isQuestSettingsOpen = false }
                    )
                }
            }
        }

        //квест бокс.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .navigationBarsPadding()
                .padding(top = 136.dp),
            contentAlignment = Alignment.Center
        ) {
            // 1. Основна велика рамка квест-бокса
            Image(
                painter = painterResource(id = R.drawable.questlistframe),
                contentDescription = null,
                modifier = Modifier.size(width = 364.dp, height = 586.dp),
                contentScale = ContentScale.FillBounds
            )

            // Контент всередині рамки
            Column(
                modifier = Modifier
                    .size(width = 364.dp, height = 586.dp)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 2. ВЕРХНЯ ЧАСТИНА: Знак оклику + QUEST INFO + Хрестик
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Іконка знака оклику зліва
                    Image(
                        painter = painterResource(id = R.drawable.questlistinfo),
                        contentDescription = "Quest Info Icon",
                        modifier = Modifier
                            .size(34.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showMessage() }
                            .align(Alignment.CenterStart)
                    )
                    // Фонова рамка
                    Image(
                        painter = painterResource(id = R.drawable.questlistinfoframe),
                        contentDescription = null,
                        modifier = Modifier
                            .width(180.dp)
                            .height(34.dp),
                        contentScale = ContentScale.FillBounds
                    )

                    // Текст поверх рамки
                    Text(
                        text = "QUEST INFO",
                        color = Color(0xFFB0E0E6),
                        fontSize = 18.sp,
                    )

                    // Кнопка закриття (хрестик) справа
                    Image(
                        painter = painterResource(id = R.drawable.exitbtn),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(26.dp) // Або підгоняй під розмір іконки
                            .align(Alignment.CenterEnd)
                            .clickable { closeApp(context) }
                    )
                }

                // 4. МІСЦЕ ДЛЯ ТВОЇХ КВЕСТІВ (Push-ups, Book, Code)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Займає весь вільний простір між лінією та варнінгом
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeQuests) { quest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF161622).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFB0E0E6).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quest.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "+${quest.expReward} EXP",
                                    color = Color(0xFF00E6F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Checkbox(
                                checked = quest.isCompleted,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch {
                                        questRepository.setQuestCompleted(quest.id, checked)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF00FF66),
                                    uncheckedColor = Color(0xFFB0E0E6),
                                    checkmarkColor = Color.Black
                                )
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                // Червоний текст попередження
                Text(
                    text = "WARNING: Failure to complete the daily quest will result in an appropriate penalty.",
                    color = Color(0xFFFF0033), // Яскраво-червоний під Solo Leveling стайл
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 18.sp
                    // fontFamily = твояFontFamily
                )
            }
        }
    }
}