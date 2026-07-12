package ua.zxcode.sololevelingapp.presentation.stats

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.health.connect.client.PermissionController
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.core.health.HealthConnectManager
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.StatEntity
import ua.zxcode.sololevelingapp.data.local.entity.StatRecordEntity
import ua.zxcode.sololevelingapp.data.repository.impl.StatsRepositoryImpl
import ua.zxcode.sololevelingapp.presentation.components.SoloLevelingBackground
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val statsRepository = remember(context) {
        StatsRepositoryImpl(
            AppDatabase.getInstance(context).statDao(),
            AppDatabase.getInstance(context).statRecordDao()
        )
    }

    val healthConnectManager = remember(context) { HealthConnectManager(context) }
    var hasHealthConnectPermission by remember { mutableStateOf(false) }

    val localStats by statsRepository.observeAllStats().collectAsState(initial = emptyList())

    var selectedStatForDetails by remember { mutableStateOf<StatEntity?>(null) }
    var isManualLogOpen by remember { mutableStateOf<StatEntity?>(null) }

    val hcClient = remember(healthConnectManager) { healthConnectManager.healthConnectClient }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedSet ->
        coroutineScope.launch {
            hasHealthConnectPermission = grantedSet.containsAll(healthConnectManager.permissions)
            if (hasHealthConnectPermission) {
                Toast.makeText(context, "Health Connect підключено!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Потрібно надати дозволи в Health Connect", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Populate initial stats if database is empty
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val existing = statsRepository.observeAllStats().firstOrNull() ?: emptyList()
            if (existing.isEmpty()) {
                statsRepository.insertStats(
                    listOf(
                        StatEntity("weight", "Маса", R.drawable.statweight, 0f),
                        StatEntity("pulse", "Пульс", R.drawable.statpulse, 0f),
                        StatEntity("steps", "Кількість кроків", R.drawable.statstepsamount, 0f),
                        StatEntity("sleep", "Сон", R.drawable.statsleeptime, 0f),
                        StatEntity("oxygen", "Рівень кисню", R.drawable.statoxygenlevel, 0f)
                    )
                )
            }
            hasHealthConnectPermission = healthConnectManager.hasAllPermissions()
        }
    }

    // Fetch and sync daily values from Health Connect
    LaunchedEffect(hasHealthConnectPermission) {
        if (hasHealthConnectPermission) {
            coroutineScope.launch {
                try {
                    val stepsVal = healthConnectManager.getTodaySteps().toFloat()
                    val pulseVal = healthConnectManager.getLastHeartRate().toFloat()
                    val sleepVal = healthConnectManager.getLastSleepDurationHours()
                    val oxygenVal = healthConnectManager.getLastOxygen()
                    val weightVal = healthConnectManager.getLastWeight()

                    val listToUpdate = listOf(
                        Triple("steps", "Кількість кроків", stepsVal),
                        Triple("pulse", "Пульс", pulseVal),
                        Triple("sleep", "Сон", sleepVal),
                        Triple("oxygen", "Рівень кисню", oxygenVal),
                        Triple("weight", "Маса", weightVal)
                    )

                    listToUpdate.forEach { (id, title, value) ->
                        if (value > 0f) {
                            val existing = statsRepository.observeStat(id).firstOrNull()
                            if (existing != null) {
                                statsRepository.updateStat(existing.copy(lastValue = value))
                                val todayStart = Instant.now().truncatedTo(ChronoUnit.DAYS).toEpochMilli()
                                val todayEnd = Instant.now().toEpochMilli()
                                val records = statsRepository.observeRecordsInRange(id, todayStart, todayEnd).firstOrNull() ?: emptyList()
                                if (records.isEmpty()) {
                                    statsRepository.insertRecord(
                                        StatRecordEntity(
                                            statId = id,
                                            value = value,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

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
                .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val weightStat = localStats.find { it.id == "weight" }
                val gridItems = localStats.filter { it.id != "weight" }

                weightStat?.let { stat ->
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        StatCard(
                            stat = stat,
                            isFullWidth = true,
                            onClick = { selectedStatForDetails = stat },
                            onAddClick = { isManualLogOpen = stat }
                        )
                    }
                }

                items(gridItems) { stat ->
                    StatCard(
                        stat = stat,
                        isFullWidth = false,
                        onClick = { selectedStatForDetails = stat },
                        onAddClick = { isManualLogOpen = stat }
                    )
                }
            }
        }

        selectedStatForDetails?.let { stat ->
            StatDetailsDialog(
                stat = stat,
                healthConnectManager = healthConnectManager,
                hasHealthConnectPermission = hasHealthConnectPermission,
                statsRepository = statsRepository,
                onDismiss = { selectedStatForDetails = null }
            )
        }

        isManualLogOpen?.let { stat ->
            ManualLogDialog(
                stat = stat,
                statsRepository = statsRepository,
                onDismiss = { isManualLogOpen = null }
            )
        }
    }
}

@Composable
fun StatCard(
    stat: StatEntity,
    isFullWidth: Boolean,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val neonColor = when (stat.id) {
        "weight" -> Color(0xFFFFB800)
        "pulse" -> Color(0xFFFF3366)
        "steps" -> Color(0xFF00FF66)
        "sleep" -> Color(0xFF00D2FF)
        "oxygen" -> Color(0xFFBD00FF)
        else -> Color(0xFF00E6F0)
    }

    val valueText = when (stat.id) {
        "weight" -> if (stat.lastValue > 0f) "%.1f кг".format(stat.lastValue) else "Ввести вагу"
        "pulse" -> if (stat.lastValue > 0f) "${stat.lastValue.toInt()} уд/хв" else "—"
        "steps" -> if (stat.lastValue > 0f) "${stat.lastValue.toInt()} кроків" else "0 кроків"
        "sleep" -> if (stat.lastValue > 0f) "%.1f год".format(stat.lastValue) else "—"
        "oxygen" -> if (stat.lastValue > 0f) "${stat.lastValue.toInt()}%" else "—"
        else -> "${stat.lastValue}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isFullWidth) 120.dp else 150.dp)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E283A).copy(alpha = 0.5f)
        ),
        border = borderStroke(1.dp, neonColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // "+" Button to add manual logs
            IconButton(
                onClick = { onAddClick() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ввести вручну",
                    tint = neonColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isFullWidth) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlowIcon(stat.iconResId, stat.title, neonColor)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stat.title.uppercase(),
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = valueText,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlowIcon(stat.iconResId, stat.title, neonColor)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stat.title,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = valueText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GlowIcon(resId: Int, contentDescription: String, color: Color) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Icon(
                painter = painterResource(id = resId),
                contentDescription = null,
                tint = color,
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
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            tint = color.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxSize(0.9f)
        )
    }
}

@Composable
fun StatDetailsDialog(
    stat: StatEntity,
    healthConnectManager: HealthConnectManager,
    hasHealthConnectPermission: Boolean,
    statsRepository: StatsRepositoryImpl,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Дн") } // "Дн", "Тиж", "Міс"
    var timeOffset by remember { mutableStateOf(0) }
    var historyRecords by remember { mutableStateOf<List<StatRecordEntity>>(emptyList()) }

    val neonColor = when (stat.id) {
        "weight" -> Color(0xFFFFB800)
        "pulse" -> Color(0xFFFF3366)
        "steps" -> Color(0xFF00FF66)
        "sleep" -> Color(0xFF00D2FF)
        "oxygen" -> Color(0xFFBD00FF)
        else -> Color(0xFF00E6F0)
    }

    LaunchedEffect(selectedTab) {
        timeOffset = 0
    }

    LaunchedEffect(selectedTab, timeOffset, hasHealthConnectPermission) {
        coroutineScope.launch {
            val days = when (selectedTab) {
                "Дн" -> 1
                "Тиж" -> 7
                "Міс" -> 30
                else -> 7
            }

            // Load local Room database records for selected range first
            val offsetMillis = days.toLong() * timeOffset.toLong() * 24 * 60 * 60 * 1000
            val endTime = System.currentTimeMillis() - offsetMillis
            val startTime = endTime - (days.toLong() * 24 * 60 * 60 * 1000)
            val localList = statsRepository.observeRecordsInRange(stat.id, startTime, endTime).firstOrNull() ?: emptyList()

            
            val localGrouped = localList.groupBy {
                java.time.LocalDate.ofInstant(Instant.ofEpochMilli(it.timestamp), java.time.ZoneId.systemDefault())
            }
            val localGroupedHour = localList.groupBy {
                java.time.LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), java.time.ZoneId.systemDefault()).hour
            }

            val recordsList = mutableListOf<StatRecordEntity>()

            if (hasHealthConnectPermission) {
                try {
                    val start = Instant.now().minus((days.toLong() * (timeOffset + 1)), ChronoUnit.DAYS)
                    val end = Instant.now().minus((days.toLong() * timeOffset), ChronoUnit.DAYS)
                    val queryDate = java.time.LocalDate.ofInstant(end, java.time.ZoneId.systemDefault())

                    when (stat.id) {
                        "steps" -> {
                            val hcRecords = healthConnectManager.getStepsHistory(start, end)
                            if (selectedTab == "Дн") {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDateTime.ofInstant(it.startTime, java.time.ZoneId.systemDefault()).hour
                                }
                                (0..23).forEach { hour ->
                                    val hcSum = grouped[hour]?.sumOf { it.count }?.toFloat() ?: 0f
                                    val localVal = localGroupedHour[hour]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcSum > 0f) hcSum else localVal
                                    val dummyTime = queryDate.atTime(hour, 0)
                                        .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    recordsList.add(StatRecordEntity(statId = "steps", value = finalVal, timestamp = dummyTime))
                                }
                            } else {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDate.ofInstant(it.startTime, java.time.ZoneId.systemDefault())
                                }
                                (0 until days).forEach { d ->
                                    val targetDate = queryDate.minusDays(d.toLong())
                                    val hcSum = grouped[targetDate]?.sumOf { it.count }?.toFloat() ?: 0f
                                    val localVal = localGrouped[targetDate]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcSum > 0f) hcSum else localVal
                                    recordsList.add(StatRecordEntity(statId = "steps", value = finalVal, timestamp = targetDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                                }
                            }
                        }
                        "pulse" -> {
                            val hcRecords = healthConnectManager.getHeartRateHistory(start, end)
                            if (selectedTab == "Дн") {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDateTime.ofInstant(it.startTime, java.time.ZoneId.systemDefault()).hour
                                }
                                (0..23).forEach { hour ->
                                    val list = grouped[hour] ?: emptyList()
                                    val hcAvg = if (list.isNotEmpty()) list.flatMap { it.samples }.map { it.beatsPerMinute }.average().toFloat() else 0f
                                    val localVal = localGroupedHour[hour]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcAvg > 0f) hcAvg else localVal
                                    val dummyTime = queryDate.atTime(hour, 0)
                                        .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    if (finalVal > 0f) {
                                        recordsList.add(StatRecordEntity(statId = "pulse", value = finalVal, timestamp = dummyTime))
                                    }
                                }
                            } else {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDate.ofInstant(it.startTime, java.time.ZoneId.systemDefault())
                                }
                                (0 until days).forEach { d ->
                                    val targetDate = queryDate.minusDays(d.toLong())
                                    val list = grouped[targetDate] ?: emptyList()
                                    val hcAvg = if (list.isNotEmpty()) list.flatMap { it.samples }.map { it.beatsPerMinute }.average().toFloat() else 0f
                                    val localVal = localGrouped[targetDate]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcAvg > 0f) hcAvg else localVal
                                    if (finalVal > 0f) {
                                        recordsList.add(StatRecordEntity(statId = "pulse", value = finalVal, timestamp = targetDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                                    }
                                }
                            }
                        }
                        "oxygen" -> {
                            val hcRecords = healthConnectManager.getOxygenHistory(start, end)
                            if (selectedTab == "Дн") {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDateTime.ofInstant(it.time, java.time.ZoneId.systemDefault()).hour
                                }
                                (0..23).forEach { hour ->
                                    val list = grouped[hour] ?: emptyList()
                                    val hcAvg = if (list.isNotEmpty()) list.map { it.percentage.value.toFloat() }.average().toFloat() else 0f
                                    val localVal = localGroupedHour[hour]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcAvg > 0f) hcAvg else localVal
                                    val dummyTime = queryDate.atTime(hour, 0)
                                        .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    if (finalVal > 0f) {
                                        recordsList.add(StatRecordEntity(statId = "oxygen", value = finalVal, timestamp = dummyTime))
                                    }
                                }
                            } else {
                                val grouped = hcRecords.groupBy {
                                    java.time.LocalDate.ofInstant(it.time, java.time.ZoneId.systemDefault())
                                }
                                (0 until days).forEach { d ->
                                    val targetDate = queryDate.minusDays(d.toLong())
                                    val list = grouped[targetDate] ?: emptyList()
                                    val hcAvg = if (list.isNotEmpty()) list.map { it.percentage.value.toFloat() }.average().toFloat() else 0f
                                    val localVal = localGrouped[targetDate]?.firstOrNull()?.value ?: 0f
                                    val finalVal = if (hcAvg > 0f) hcAvg else localVal
                                    if (finalVal > 0f) {
                                        recordsList.add(StatRecordEntity(statId = "oxygen", value = finalVal, timestamp = targetDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                                    }
                                }
                            }
                        }
                        "sleep" -> {
                            val hcRecords = healthConnectManager.getSleepHistory(start, end)
                            val grouped = hcRecords.groupBy {
                                java.time.LocalDate.ofInstant(it.startTime, java.time.ZoneId.systemDefault())
                            }
                            (0 until days).forEach { d ->
                                val targetDate = queryDate.minusDays(d.toLong())
                                val list = grouped[targetDate] ?: emptyList()
                                val hcSum = list.sumOf { (it.endTime.toEpochMilli() - it.startTime.toEpochMilli()).toDouble() / (1000 * 60 * 60) }.toFloat()
                                val localVal = localGrouped[targetDate]?.firstOrNull()?.value ?: 0f
                                val finalVal = if (hcSum > 0f) hcSum else localVal
                                recordsList.add(StatRecordEntity(statId = "sleep", value = finalVal, timestamp = targetDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                            }
                        }
                        "weight" -> {
                            val hcRecords = healthConnectManager.getWeightHistory(start, end)
                            val grouped = hcRecords.groupBy {
                                java.time.LocalDate.ofInstant(it.time, java.time.ZoneId.systemDefault())
                            }
                            (0 until days).forEach { d ->
                                val targetDate = queryDate.minusDays(d.toLong())
                                val list = grouped[targetDate] ?: emptyList()
                                val hcAvg = if (list.isNotEmpty()) list.map { it.weight.inKilograms.toFloat() }.average().toFloat() else 0f
                                val localVal = localGrouped[targetDate]?.firstOrNull()?.value ?: 0f
                                val finalVal = if (hcAvg > 0f) hcAvg else localVal
                                if (finalVal > 0f) {
                                    recordsList.add(StatRecordEntity(statId = "weight", value = finalVal, timestamp = targetDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()))
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (recordsList.isNotEmpty()) {
                historyRecords = recordsList.sortedBy { it.timestamp }
            } else {
                historyRecords = localList.sortedBy { it.timestamp }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1524).copy(alpha = 0.97f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (Title & Close button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрити", tint = Color.White)
                    }

                    Text(
                        text = stat.title.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(48.dp)) // Equal spacing
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive paginated Date Label
                val dateLabelText = remember(selectedTab, timeOffset) {
                    val targetLocalDate = java.time.LocalDate.now().minusDays(
                        when (selectedTab) {
                            "Дн" -> timeOffset.toLong()
                            "Тиж" -> timeOffset.toLong() * 7
                            "Міс" -> timeOffset.toLong() * 30
                            else -> 0L
                        }
                    )
                    when (selectedTab) {
                        "Дн" -> {
                            if (timeOffset == 0) {
                                "Сьогодні"
                            } else {
                                val sFormat = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy р.", java.util.Locale("uk"))
                                targetLocalDate.format(sFormat)
                            }
                        }
                        "Тиж" -> {
                            val startOfWeek = targetLocalDate.minusDays(6)
                            val formatter = java.time.format.DateTimeFormatter.ofPattern("d MMMM", java.util.Locale("uk"))
                            val yearFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy р.", java.util.Locale("uk"))
                            "${startOfWeek.format(formatter)} – ${targetLocalDate.format(formatter)} ${targetLocalDate.format(yearFormatter)}"
                        }
                        "Міс" -> {
                            val formatter = java.time.format.DateTimeFormatter.ofPattern("LLLL yyyy р.", java.util.Locale("uk"))
                            targetLocalDate.format(formatter).replaceFirstChar { it.uppercase() }
                        }
                        else -> ""
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { timeOffset++ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Попередній період",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = dateLabelText,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { if (timeOffset > 0) timeOffset-- },
                        enabled = timeOffset > 0,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Наступний період",
                            tint = if (timeOffset > 0) Color.White else Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tab Selector (Day / Week / Month)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E283A).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Дн", "Тиж", "Міс").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(
                                    color = if (isSelected) Color(0xFF28364F) else Color.Transparent,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedTab = tab },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Value Summary
                val lastValText = when (stat.id) {
                    "steps" -> "${historyRecords.sumOf { it.value.toInt() }} кроків"
                    "pulse" -> if (historyRecords.isNotEmpty()) "${historyRecords.map { it.value.toInt() }.average().toInt()} уд/хв" else "—"
                    "oxygen" -> if (stat.lastValue > 0f) "${stat.lastValue.toInt()}%" else "—"
                    "sleep" -> if (historyRecords.isNotEmpty()) "%.1f год".format(historyRecords.map { it.value }.average()) else "—"
                    "weight" -> if (stat.lastValue > 0f) "%.1f кг".format(stat.lastValue) else "—"
                    else -> "${stat.lastValue}"
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lastValText,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (stat.id) {
                            "steps" -> "Всього за цей період"
                            "pulse" -> "Середній пульс"
                            "sleep" -> "Середній сон"
                            else -> "Останнє значення"
                        },
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful custom Canvas chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283A).copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (historyRecords.isEmpty()) {
                            Text(
                                text = "Немає даних за цей період",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 12.sp
                            )
                        } else {
                            StatHistoryChart(records = historyRecords, neonColor = neonColor, statId = stat.id)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Metrics / summary grid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283A).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ЗВЕДЕННЯ",
                            color = neonColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render extra dynamic statistics metrics
                        when (stat.id) {
                            "steps" -> {
                                val totalSteps = historyRecords.sumOf { it.value.toInt() }
                                val distanceKm = totalSteps * 0.00075f
                                SummaryRow("Всього кроків", "$totalSteps")
                                SummaryRow("Відстань", "%.2f км".format(distanceKm))
                            }
                            "pulse" -> {
                                val avg = if (historyRecords.isNotEmpty()) historyRecords.map { it.value }.average().toFloat() else 0f
                                val max = historyRecords.maxOfOrNull { it.value }?.toInt() ?: 0
                                val min = historyRecords.minOfOrNull { it.value }?.toInt() ?: 0
                                SummaryRow("Середній пульс", if (avg > 0f) "${avg.toInt()} уд/хв" else "—")
                                SummaryRow("Макс / Мін", if (max > 0) "$max / $min уд/хв" else "—")
                            }
                            "oxygen" -> {
                                SummaryRow("Останній рівень кисню", if (stat.lastValue > 0f) "${stat.lastValue.toInt()}%" else "—")
                                SummaryRow("Нормальний діапазон", "95% – 100%")
                            }
                            "weight" -> {
                                val values = historyRecords.map { it.value }
                                SummaryRow("Поточна вага", if (stat.lastValue > 0f) "%.1f кг".format(stat.lastValue) else "—")
                                SummaryRow("Варіація за період", if (values.size > 1) "%.1f кг".format(values.last() - values.first()) else "0.0 кг")
                            }
                            "sleep" -> {
                                val avg = if (historyRecords.isNotEmpty()) historyRecords.map { it.value }.average().toFloat() else 0f
                                val max = historyRecords.maxOfOrNull { it.value } ?: 0f
                                SummaryRow("Середній сон", if (avg > 0f) "%.1f год".format(avg) else "—")
                                SummaryRow("Найдовший сон", if (max > 0f) "%.1f год".format(max) else "—")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatHistoryChart(records: List<StatRecordEntity>, neonColor: Color, statId: String) {
    var selectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(records) {
        if (records.isNotEmpty()) selectedIndex = records.size - 1
    }

    val isHourly = records.size > 7
    val dateFormat = remember { java.text.SimpleDateFormat(if (isHourly) "HH:mm" else "d.MM", java.util.Locale("uk")) }

    if (records.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Немає даних", color = Color.Gray, fontSize = 12.sp)
        }
        return
    }

    val maxVal = records.maxOf { it.value }.coerceAtLeast(1f)
    val minVal = records.minOf { it.value }.coerceAtMost(maxVal - 1f)
    val range = (maxVal - minVal).coerceAtLeast(1f)

    // Format value for tooltip / Y-axis labels
    fun formatVal(v: Float): String = when (statId) {
        "steps" -> v.toInt().toString()
        "weight" -> "%.1f".format(v)
        "sleep" -> "%.1f г".format(v)
        "pulse" -> "${v.toInt()} уд"
        "oxygen" -> "${v.toInt()}%"
        else -> "%.1f".format(v)
    }

    // How many x-labels to show
    val maxLabels = 6
    val labelStep = (records.size / maxLabels).coerceAtLeast(1)

    Column(modifier = Modifier.fillMaxSize()) {
        // Tooltip row
        val sel = if (selectedIndex >= 0 && selectedIndex < records.size) records[selectedIndex] else null
        if (sel != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(java.util.Date(sel.timestamp)),
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatVal(sel.value),
                    color = neonColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(modifier = Modifier.weight(1f)) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatVal(maxVal), color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                Text(formatVal((maxVal + minVal) / 2f), color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                Text(formatVal(minVal), color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Chart canvas
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* handled by pointer */ }
                    .pointerInput(records) {
                        detectTapGestures { offset ->
                            val n = records.size
                            val barW = size.width.toFloat() / n
                            val idx = (offset.x / barW).toInt().coerceIn(0, n - 1)
                            selectedIndex = idx
                        }
                    }
            ) {
                val n = records.size
                val barW = size.width / n
                val chartH = size.height

                // Horizontal grid lines
                val gridAlpha = 0.12f
                drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, chartH / 2f), Offset(size.width, chartH / 2f), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = gridAlpha), Offset(0f, chartH), Offset(size.width, chartH), strokeWidth = 1f)

                records.forEachIndexed { i, rec ->
                    val normalized = ((rec.value - minVal) / range).coerceIn(0f, 1f)
                    val barH = (normalized * chartH).coerceAtLeast(4f)
                    val left = i * barW + barW * 0.1f
                    val right = (i + 1) * barW - barW * 0.1f
                    val top = chartH - barH

                    // Bar gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                neonColor.copy(alpha = if (i == selectedIndex) 1f else 0.75f),
                                neonColor.copy(alpha = if (i == selectedIndex) 0.4f else 0.2f)
                            ),
                            startY = top,
                            endY = chartH
                        ),
                        topLeft = Offset(left, top),
                        size = Size(right - left, barH)
                    )

                    // Highlight selected bar with top cap line
                    if (i == selectedIndex) {
                        drawLine(
                            color = neonColor,
                            start = Offset(left, top),
                            end = Offset(right, top),
                            strokeWidth = 2.5f
                        )
                    }
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .padding(start = 44.dp, top = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val step = (records.size / maxLabels.toFloat()).coerceAtLeast(1f)
            val indices = (0 until maxLabels).map { (it * step).toInt().coerceAtMost(records.size - 1) }
            indices.forEach { idx ->
                Text(
                    text = dateFormat.format(java.util.Date(records[idx].timestamp)),
                    color = Color.Gray,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ManualLogDialog(
    stat: StatEntity,
    statsRepository: StatsRepositoryImpl,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var inputValue by remember { mutableStateOf("") }

    val neonColor = when (stat.id) {
        "weight" -> Color(0xFFFFB800)
        "pulse" -> Color(0xFFFF3366)
        "steps" -> Color(0xFF00FF66)
        "sleep" -> Color(0xFF00D2FF)
        "oxygen" -> Color(0xFFBD00FF)
        else -> Color(0xFF00E6F0)
    }

    val labelText = when (stat.id) {
        "weight" -> "Вага (кг)"
        "pulse" -> "Пульс (уд/хв)"
        "steps" -> "Кількість кроків"
        "sleep" -> "Тривалість сну (год)"
        "oxygen" -> "Рівень кисню (%)"
        else -> "Значення"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "НОВИЙ ЗАПИС: ${stat.title.uppercase()}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Введіть поточне значення для цього метричного показника:",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text(labelText) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fVal = inputValue.toFloatOrNull()
                    if (fVal != null && fVal > 0f) {
                        coroutineScope.launch {
                            statsRepository.updateStat(stat.copy(lastValue = fVal))
                            statsRepository.insertRecord(
                                StatRecordEntity(
                                    statId = stat.id,
                                    value = fVal,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            onDismiss()
                        }
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonColor.copy(alpha = 0.2f)),
                modifier = Modifier.border(1.dp, neonColor, RoundedCornerShape(8.dp))
            ) {
                Text("ЗБЕРЕГТИ", color = neonColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("СКАСУВАТИ", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF1E283A),
        shape = RoundedCornerShape(12.dp)
    )
}

// Utility stroke builder for Compose border
private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00E6F0),
    unfocusedBorderColor = Color(0xFFB0E0E6).copy(alpha = 0.3f),
    focusedLabelColor = Color(0xFF00E6F0),
    unfocusedLabelColor = Color.Gray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color(0xFF101622),
    unfocusedContainerColor = Color(0xFF101622)
)