package ua.zxcode.sololevelingapp.presentation.home

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Path
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
import ua.zxcode.sololevelingapp.data.repository.impl.UserRepositoryImpl
import ua.zxcode.sololevelingapp.data.repository.impl.QuestRepositoryImpl
import ua.zxcode.sololevelingapp.data.repository.impl.AchievementRepositoryImpl
import ua.zxcode.sololevelingapp.data.local.entity.UserEntity
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground
import ua.zxcode.sololevelingapp.presentation.achievements.AchievementIds
import ua.zxcode.sololevelingapp.presentation.achievements.computeRank
import ua.zxcode.sololevelingapp.presentation.achievements.defaultAchievements
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
    val userRepository = remember(context) {
        UserRepositoryImpl(AppDatabase.getInstance(context).userDao())
    }
    val achievementRepository = remember(context) {
        AchievementRepositoryImpl(AppDatabase.getInstance(context).achievementDao())
    }
    val activeQuests by questRepository.observeAllQuests().collectAsState(initial = emptyList())
    val userState by userRepository.observeUser().collectAsState(initial = null)

    // Seed achievements on first launch
    LaunchedEffect(Unit) {
        val existing = achievementRepository.getAchievementById(AchievementIds.SYSTEM_ARCHITECT)
        if (existing == null) {
            achievementRepository.insertAchievements(defaultAchievements())
        }
    }

    val currentLevel = userState?.currentLevel ?: 0
    val currentXp = userState?.currentXp ?: 0
    val xpToNextLevel = userState?.xpToNextLevel ?: 100

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
                    Box(modifier = Modifier.weight(1.3f).height(barHeight * 1.3f)) {
                        // Background glow layer
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
                        // Foreground image layer
                        Image(
                            painter = painterResource(id = R.drawable.lvlbar),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )

                        // ── CUSTOM RENDERING OVER THE LEVEL BAR ──
                        val percentage = if (xpToNextLevel > 0) (currentXp.toFloat() / xpToNextLevel).coerceIn(0f, 1f) else 0f
                        val ticksToFill = (percentage * 10).toInt()

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val scaleX = size.width / 316f
                            val scaleY = size.height / 62f
                            val greenColor = Color(0xFF00FF33)

                            // Boundaries of the 10 progress segments from vector coordinates (with small margins to look nice inside borders)
                            val topX = floatArrayOf(54.17f, 80.83f, 104.83f, 128.83f, 152.83f, 176.83f, 200.83f, 224.83f, 248.83f, 272.83f, 280.83f)
                            val bottomX = floatArrayOf(54.17f, 75.5f, 99.5f, 123.5f, 147.5f, 171.5f, 195.5f, 219.5f, 243.5f, 267.5f, 280.83f)

                            for (i in 0 until ticksToFill) {
                                val path = Path().apply {
                                    // Add minor inner margins (1.2f scaleX/scaleY) to keep the fill inside the vector boundaries
                                    moveTo(topX[i] * scaleX + 1.2f * scaleX, 22.0f * scaleY)
                                    lineTo(topX[i+1] * scaleX - 1.2f * scaleX, 22.0f * scaleY)
                                    lineTo(bottomX[i+1] * scaleX - 1.2f * scaleX, 39.5f * scaleY)
                                    lineTo(bottomX[i] * scaleX + 1.2f * scaleX, 39.5f * scaleY)
                                    close()
                                }
                                drawPath(path, color = greenColor)
                            }
                        }

                        // Use BiasAlignment to center the level number text exactly at the octagon's geometric center (9.758% from start)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.BiasAlignment(horizontalBias = -0.84f, verticalBias = 0f)
                        ) {
                            Text(
                                text = currentLevel.toString(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
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


            }
        }

        // ── Overlays (поза Row, щоб не впливати на лейаут хедера) ──────────
        if (isProfileOpen) {
            ProfileOverlay(
                onDismiss = { isProfileOpen = false },
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
                        text = "КВЕСТИ",
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activeQuests) { quest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Category Icon with Neon Glow
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(end = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val iconRes = when (quest.category) {
                                    "coding" -> R.drawable.questicocode
                                    "physical" -> R.drawable.questicogym
                                    else -> R.drawable.questicobook
                                }
                                
                                // Glow Layer
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.DECAL)
                                                    .asComposeRenderEffect()
                                            } else null
                                        },
                                    colorFilter = ColorFilter.tint(
                                        Color(0xFF00E6F0),
                                        blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                                    )
                                )
                                // Sharp original layer
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // 2. Title of the Quest
                            Text(
                                text = quest.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            // 3. Ratio [progress/target]
                            Text(
                                text = "[${quest.progress}/${quest.target}]",
                                color = Color(0xFFB0E0E6),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // 4. Clickable Custom Checkbox Box
                            Image(
                                painter = painterResource(
                                    id = if (quest.isCompleted) R.drawable.questboxcompletedcheck else R.drawable.questbox
                                ),
                                contentDescription = "Complete Quest Box",
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        val newCompleted = !quest.isCompleted
                                        val newProgress = if (newCompleted) quest.target else 0
                                        coroutineScope.launch {
                                            questRepository.updateQuest(
                                                quest.copy(isCompleted = newCompleted, progress = newProgress)
                                            )
                                            if (newCompleted) {
                                                val xpGain = if (quest.isPenalty) (quest.expReward * 1.5).toInt() else quest.expReward
                                                val user = userRepository.getUser()
                                                if (user != null) {
                                                    var newXp = user.currentXp + xpGain
                                                    var newLvl = user.currentLevel
                                                    var nextLvlThreshold = user.xpToNextLevel
                                                    while (newXp >= nextLvlThreshold) {
                                                        newXp -= nextLvlThreshold
                                                        newLvl += 1
                                                        nextLvlThreshold = 100 + 10 * newLvl
                                                    }
                                                    userRepository.updateUser(
                                                        user.copy(
                                                            currentLevel = newLvl,
                                                            currentXp = newXp,
                                                            xpToNextLevel = nextLvlThreshold
                                                        )
                                                    )
                                                } else {
                                                    // Initialize default if null
                                                    var newXp = xpGain
                                                    var newLvl = 0
                                                    var nextLvlThreshold = 100 + 10 * newLvl
                                                    if (newXp >= nextLvlThreshold) {
                                                        newXp -= nextLvlThreshold
                                                        newLvl = 1
                                                        nextLvlThreshold = 100 + 10 * newLvl
                                                    }
                                                    userRepository.insertUser(
                                                        UserEntity(
                                                            nickname = "Сон Джин Ву",
                                                            currentLevel = newLvl,
                                                            currentXp = newXp,
                                                            xpToNextLevel = nextLvlThreshold
                                                        )
                                                    )
                                                }
                                                // ── ACHIEVEMENT TRACKING ──
                                                val achievementId = when (quest.category) {
                                                    "coding"   -> AchievementIds.SYSTEM_ARCHITECT
                                                    "mental"   -> AchievementIds.MONARCH_LIBRARY
                                                    "languages"-> AchievementIds.POLYGLOT
                                                    "physical" -> AchievementIds.ROAD_TO_OLYMPUS
                                                    else       -> null
                                                }
                                                if (achievementId != null) {
                                                     val achv = achievementRepository.getAchievementById(achievementId)
                                                     if (achv != null) {
                                                         val addAmount = when (quest.category) {
                                                             "mental", "physical" -> quest.target.toLong()
                                                             else -> 1L
                                                         }
                                                         val newProgress = achv.progress + addAmount
                                                         val newRank = computeRank(achievementId, newProgress)
                                                         achievementRepository.updateAchievement(
                                                             achv.copy(
                                                                 progress = newProgress,
                                                                 currentRank = newRank,
                                                                 isCompleted = newRank == 5
                                                             )
                                                         )
                                                     }
                                                }
                                            } else {
                                                // If uncompleted, subtract XP
                                                val xpLoss = if (quest.isPenalty) (quest.expReward * 1.5).toInt() else quest.expReward
                                                val user = userRepository.getUser()
                                                if (user != null) {
                                                    var newXp = user.currentXp - xpLoss
                                                    var newLvl = user.currentLevel
                                                    var nextLvlThreshold = user.xpToNextLevel
                                                    while (newXp < 0 && newLvl > 0) {
                                                        newLvl -= 1
                                                        val prevThreshold = 100 + 10 * newLvl
                                                        newXp += prevThreshold
                                                        nextLvlThreshold = prevThreshold
                                                    }
                                                    if (newXp < 0) newXp = 0
                                                    userRepository.updateUser(
                                                        user.copy(
                                                            currentLevel = newLvl,
                                                            currentXp = newXp,
                                                            xpToNextLevel = nextLvlThreshold
                                                        )
                                                    )
                                                }
                                                // ── SUBTRACT PROGRESS FROM ACHIEVEMENTS ON UNCHECK ──
                                                val achievementId = when (quest.category) {
                                                    "coding"   -> AchievementIds.SYSTEM_ARCHITECT
                                                    "mental"   -> AchievementIds.MONARCH_LIBRARY
                                                    "languages"-> AchievementIds.POLYGLOT
                                                    "physical" -> AchievementIds.ROAD_TO_OLYMPUS
                                                    else       -> null
                                                }
                                                if (achievementId != null) {
                                                    val achv = achievementRepository.getAchievementById(achievementId)
                                                    if (achv != null) {
                                                        val subtractAmount = when (quest.category) {
                                                            "mental", "physical" -> quest.target.toLong()
                                                            else -> 1L
                                                        }
                                                        val newProgress = (achv.progress - subtractAmount).coerceAtLeast(0L)
                                                        val newRank = computeRank(achievementId, newProgress)
                                                        achievementRepository.updateAchievement(
                                                            achv.copy(
                                                                progress = newProgress,
                                                                currentRank = newRank,
                                                                isCompleted = newRank == 5
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                // Червоний текст попередження
                Text(
                    text = "УВАГА: Невиконання щоденного квесту призведе до відповідного покарання.",
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