package ua.zxcode.sololevelingapp.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import ua.zxcode.sololevelingapp.data.repository.impl.QuestRepositoryImpl

enum class QuestCategory {
    READING, CODING, LANGUAGES, PHYSICAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestSettingsOverlay(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val questRepository = remember(context) {
        QuestRepositoryImpl(AppDatabase.getInstance(context).questDao())
    }

    var selectedCategory by remember { mutableStateOf(QuestCategory.READING) }

    // Reading inputs
    var bookTitle by remember { mutableStateOf("") }
    var readingPages by remember { mutableStateOf("15") }

    // Coding inputs
    var projectTitle by remember { mutableStateOf("") }
    var codingLines by remember { mutableStateOf("100") }

    // Language inputs
    var languageName by remember { mutableStateOf("") }
    var languageMetric by remember { mutableStateOf("Слів") } // "Слів", "Хвилин", "Уроків"
    var languageTarget by remember { mutableStateOf("10") }

    // Physical inputs
    var physicalExercise by remember { mutableStateOf("") }
    var physicalSets by remember { mutableStateOf("3") }
    var physicalReps by remember { mutableStateOf("12") }

    var exerciseSearchQuery by remember { mutableStateOf("") }
    var isExerciseDropdownExpanded by remember { mutableStateOf(false) }

    // Filtered exercise list
    val filteredExercises = remember(exerciseSearchQuery) {
        if (exerciseSearchQuery.isBlank()) {
            QuestExercises.list
        } else {
            QuestExercises.list.filter { it.contains(exerciseSearchQuery, ignoreCase = true) }
        }
    }

    Popup(
        onDismissRequest = { onDismiss() },
        properties = PopupProperties(
            focusable = true,
            excludeFromSystemGesture = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .imePadding()
                    .border(
                        width = 2.dp,
                        color = Color(0xFF00E6F0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = Color(0xFF0A0A12),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "QUEST CREATOR",
                        color = Color(0xFF00E6F0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category tabs/buttons (Reading, Coding, Languages, Physical)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CategoryButton(
                                text = "Книга",
                                isSelected = selectedCategory == QuestCategory.READING,
                                onClick = { selectedCategory = QuestCategory.READING },
                                modifier = Modifier.weight(1f)
                            )
                            CategoryButton(
                                text = "Кодинг",
                                isSelected = selectedCategory == QuestCategory.CODING,
                                onClick = { selectedCategory = QuestCategory.CODING },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CategoryButton(
                                text = "Мова",
                                isSelected = selectedCategory == QuestCategory.LANGUAGES,
                                onClick = { selectedCategory = QuestCategory.LANGUAGES },
                                modifier = Modifier.weight(1f)
                            )
                            CategoryButton(
                                text = "Спорт",
                                isSelected = selectedCategory == QuestCategory.PHYSICAL,
                                onClick = { selectedCategory = QuestCategory.PHYSICAL },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (selectedCategory) {
                                QuestCategory.READING -> {
                                    OutlinedTextField(
                                        value = bookTitle,
                                        onValueChange = { bookTitle = it },
                                        label = { Text("Назва книги / Джерела") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )

                                    OutlinedTextField(
                                        value = readingPages,
                                        onValueChange = { readingPages = it.filter { char -> char.isDigit() } },
                                        label = { Text("Цільова кількість сторінок") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )
                                }
                                QuestCategory.CODING -> {
                                    OutlinedTextField(
                                        value = projectTitle,
                                        onValueChange = { projectTitle = it },
                                        label = { Text("Проект / Завдання") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )

                                    OutlinedTextField(
                                        value = codingLines,
                                        onValueChange = { codingLines = it.filter { char -> char.isDigit() } },
                                        label = { Text("Цільова кількість рядків коду") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )
                                }
                                QuestCategory.LANGUAGES -> {
                                    OutlinedTextField(
                                        value = languageName,
                                        onValueChange = { languageName = it },
                                        label = { Text("Мова (напр. Англійська)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )

                                    Text("Одиниця виміру цілі:", color = Color.Gray, fontSize = 14.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Слів", "Хвилин", "Уроків").forEach { metric ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (languageMetric == metric) Color(0xFF00E6F0) else Color.Gray.copy(alpha = 0.5f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .background(
                                                        color = if (languageMetric == metric) Color(0xFF00E6F0).copy(alpha = 0.1f) else Color.Transparent,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { languageMetric = metric }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = metric,
                                                    color = if (languageMetric == metric) Color(0xFF00E6F0) else Color.Gray,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = languageTarget,
                                        onValueChange = { languageTarget = it.filter { char -> char.isDigit() } },
                                        label = { Text("Ціль ($languageMetric)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = darkTextFieldColors()
                                    )
                                }
                                QuestCategory.PHYSICAL -> {
                                    // Searchable autocomplete exercise selection
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = exerciseSearchQuery,
                                            onValueChange = {
                                                exerciseSearchQuery = it
                                                physicalExercise = it
                                                isExerciseDropdownExpanded = true
                                            },
                                            label = { Text("Виберіть або введіть вправу") },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = {
                                                IconButton(onClick = { isExerciseDropdownExpanded = !isExerciseDropdownExpanded }) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDropDown,
                                                        contentDescription = "Показати список",
                                                        tint = Color(0xFF00E6F0)
                                                    )
                                                }
                                            },
                                            colors = darkTextFieldColors()
                                        )

                                        if (isExerciseDropdownExpanded) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .border(1.dp, Color(0xFF00E6F0), RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF0E0E18))
                                                    .padding(4.dp)
                                            ) {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    if (exerciseSearchQuery.isNotBlank() && !QuestExercises.list.contains(exerciseSearchQuery)) {
                                                        item {
                                                            DropdownItem(
                                                                text = "[Додати власну]: $exerciseSearchQuery",
                                                                onClick = {
                                                                    physicalExercise = exerciseSearchQuery
                                                                    isExerciseDropdownExpanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                    items(filteredExercises) { exercise ->
                                                        DropdownItem(
                                                            text = exercise,
                                                            onClick = {
                                                                physicalExercise = exercise
                                                                exerciseSearchQuery = exercise
                                                                isExerciseDropdownExpanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = physicalSets,
                                            onValueChange = { physicalSets = it.filter { char -> char.isDigit() } },
                                            label = { Text("Підходи") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = darkTextFieldColors()
                                        )

                                        OutlinedTextField(
                                            value = physicalReps,
                                            onValueChange = { physicalReps = it.filter { char -> char.isDigit() } },
                                            label = { Text("Повтори") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = darkTextFieldColors()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "НАЗАД", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                // Add validation & save to DB
                                val (title, reward, dbCategory) = when (selectedCategory) {
                                    QuestCategory.READING -> {
                                        val book = bookTitle.trim().ifEmpty { "Книгу" }
                                        val pages = readingPages.toIntOrNull() ?: 1
                                        Triple("Читати книгу \"$book\" ($pages стор.)", pages * 4, "mental")
                                    }
                                    QuestCategory.CODING -> {
                                        val proj = projectTitle.trim().ifEmpty { "Проект" }
                                        val lines = codingLines.toIntOrNull() ?: 100
                                        Triple("Написати код: \"$proj\" ($lines рядків)", (lines * 0.5).toInt().coerceIn(10, 150), "coding")
                                    }
                                    QuestCategory.LANGUAGES -> {
                                        val lang = languageName.trim().ifEmpty { "Мова" }
                                        val target = languageTarget.toIntOrNull() ?: 10
                                        val metric = languageMetric.lowercase()
                                        Triple("Вивчення $lang: $target $metric", target * 3, "languages")
                                    }
                                    QuestCategory.PHYSICAL -> {
                                        val exercise = physicalExercise.trim().ifEmpty { "Тренування" }
                                        val sets = physicalSets.toIntOrNull() ?: 3
                                        val reps = physicalReps.toIntOrNull() ?: 12
                                        Triple("Спорт: $exercise ($sets x $reps)", sets * reps * 2, "physical")
                                    }
                                }

                                coroutineScope.launch {
                                    questRepository.insertQuest(
                                        QuestEntity(
                                            title = title,
                                            expReward = reward,
                                            isCompleted = false,
                                            category = dbCategory
                                        )
                                    )
                                    Toast.makeText(context, "Квест збережено!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66).copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1.2f)
                                .border(1.dp, Color(0xFF00FF66), RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00FF66))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "ДОДАТИ", color = Color(0xFF00FF66), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF00E6F0) else Color(0xFFB0E0E6).copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) Color(0xFF00E6F0).copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF00E6F0) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun DropdownItem(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFF00E6F0),
    unfocusedLabelColor = Color.Gray,
    focusedBorderColor = Color(0xFF00E6F0),
    unfocusedBorderColor = Color.Gray
)
