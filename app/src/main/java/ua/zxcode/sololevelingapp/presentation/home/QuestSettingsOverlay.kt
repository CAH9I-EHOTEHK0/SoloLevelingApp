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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import ua.zxcode.sololevelingapp.data.repository.impl.QuestRepositoryImpl

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

enum class QuestCategory {
    READING, CODING, LANGUAGES, PHYSICAL
}

private enum class QuestSettingsScreen {
    LIST,
    ADD,
    EDIT
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

    val allQuests by questRepository.observeAllQuests().collectAsState(initial = emptyList())

    var currentScreen by remember { mutableStateOf(QuestSettingsScreen.LIST) }
    var editingQuest by remember { mutableStateOf<QuestEntity?>(null) }

    var selectedCategory by remember { mutableStateOf(QuestCategory.READING) }
    var bookTitle by remember { mutableStateOf("") }
    var readingPages by remember { mutableStateOf("15") }
    var projectTitle by remember { mutableStateOf("") }
    var codingLines by remember { mutableStateOf("100") }
    var languageName by remember { mutableStateOf("") }
    var languageMetric by remember { mutableStateOf("Слів") }
    var languageTarget by remember { mutableStateOf("10") }
    var physicalExercise by remember { mutableStateOf("") }
    var physicalSets by remember { mutableStateOf("3") }
    var physicalReps by remember { mutableStateOf("12") }
    var exerciseSearchQuery by remember { mutableStateOf("") }
    var isExerciseDropdownExpanded by remember { mutableStateOf(false) }

    // Prepopulate form fields for editing
    fun enterEditMode(quest: QuestEntity) {
        editingQuest = quest
        when (quest.category) {
            "mental" -> {
                selectedCategory = QuestCategory.READING
                bookTitle = quest.title
                readingPages = quest.target.toString()
            }
            "coding" -> {
                selectedCategory = QuestCategory.CODING
                projectTitle = quest.title
                codingLines = quest.target.toString()
            }
            "languages" -> {
                selectedCategory = QuestCategory.LANGUAGES
                languageName = quest.title
                languageTarget = quest.target.toString()
            }
            "physical" -> {
                selectedCategory = QuestCategory.PHYSICAL
                physicalExercise = quest.title
                exerciseSearchQuery = quest.title
                physicalSets = "1"
                physicalReps = quest.target.toString()
            }
        }
        currentScreen = QuestSettingsScreen.EDIT
    }

    fun resetForm() {
        selectedCategory = QuestCategory.READING
        bookTitle = ""
        readingPages = "15"
        projectTitle = ""
        codingLines = "100"
        languageName = ""
        languageMetric = "Слів"
        languageTarget = "10"
        physicalExercise = ""
        exerciseSearchQuery = ""
        physicalSets = "3"
        physicalReps = "12"
        editingQuest = null
    }

    val filteredExercises = remember(exerciseSearchQuery) {
        if (exerciseSearchQuery.isBlank()) QuestExercises.list
        else QuestExercises.list.filter { it.contains(exerciseSearchQuery, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
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
                    .border(2.dp, Color(0xFF00E6F0), RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A0A12), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = when (currentScreen) {
                            QuestSettingsScreen.LIST -> "НАЛАШТУВАННЯ КВЕСТІВ"
                            QuestSettingsScreen.ADD  -> "НОВИЙ КВЕСТ"
                            QuestSettingsScreen.EDIT -> "РЕДАГУВАТИ КВЕСТ"
                        },
                        color = Color(0xFF00E6F0),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    if (currentScreen == QuestSettingsScreen.LIST) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF00FF66), RoundedCornerShape(10.dp))
                                .background(Color(0xFF00FF66).copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    resetForm()
                                    currentScreen = QuestSettingsScreen.ADD
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Додати квест",
                                    tint = Color(0xFF00FF66),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "ДОДАТИ КВЕСТ",
                                    color = Color(0xFF00FF66),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (currentScreen) {

                        QuestSettingsScreen.LIST -> {
                            if (allQuests.isEmpty()) {
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Квестів ще немає.\nНатисни «Додати» щоб створити перший.",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(allQuests, key = { it.id }) { quest ->
                                        QuestListItem(
                                            quest = quest,
                                            onEdit = { enterEditMode(quest) },
                                            onDelete = {
                                                coroutineScope.launch {
                                                    questRepository.deleteQuest(quest)
                                                    Toast.makeText(context, "Квест видалено", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ЗАКРИТИ", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                            }
                        }

                        QuestSettingsScreen.ADD,
                        QuestSettingsScreen.EDIT -> {
                            // Category buttons
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    CategoryButton("Книга",  selectedCategory == QuestCategory.READING,  { selectedCategory = QuestCategory.READING  }, Modifier.weight(1f))
                                    CategoryButton("Кодинг", selectedCategory == QuestCategory.CODING,   { selectedCategory = QuestCategory.CODING   }, Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    CategoryButton("Мова",  selectedCategory == QuestCategory.LANGUAGES, { selectedCategory = QuestCategory.LANGUAGES }, Modifier.weight(1f))
                                    CategoryButton("Спорт", selectedCategory == QuestCategory.PHYSICAL,  { selectedCategory = QuestCategory.PHYSICAL  }, Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                                                onValueChange = { readingPages = it.filter { c -> c.isDigit() } },
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
                                                onValueChange = { codingLines = it.filter { c -> c.isDigit() } },
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
                                                                1.dp,
                                                                if (languageMetric == metric) Color(0xFF00E6F0) else Color.Gray.copy(alpha = 0.5f),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .background(
                                                                if (languageMetric == metric) Color(0xFF00E6F0).copy(alpha = 0.1f) else Color.Transparent,
                                                                RoundedCornerShape(8.dp)
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
                                                onValueChange = { languageTarget = it.filter { c -> c.isDigit() } },
                                                label = { Text("Ціль ($languageMetric)") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = darkTextFieldColors()
                                            )
                                        }
                                        QuestCategory.PHYSICAL -> {
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
                                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Показати список", tint = Color(0xFF00E6F0))
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
                                                                    DropdownItem("[Додати власну]: $exerciseSearchQuery") {
                                                                        physicalExercise = exerciseSearchQuery
                                                                        isExerciseDropdownExpanded = false
                                                                    }
                                                                }
                                                            }
                                                            items(filteredExercises) { exercise ->
                                                                DropdownItem(exercise) {
                                                                    physicalExercise = exercise
                                                                    exerciseSearchQuery = exercise
                                                                    isExerciseDropdownExpanded = false
                                                                }
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
                                                    onValueChange = { physicalSets = it.filter { c -> c.isDigit() } },
                                                    label = { Text("Підходи") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    singleLine = true,
                                                    modifier = Modifier.weight(1f),
                                                    colors = darkTextFieldColors()
                                                )
                                                OutlinedTextField(
                                                    value = physicalReps,
                                                    onValueChange = { physicalReps = it.filter { c -> c.isDigit() } },
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        resetForm()
                                        currentScreen = QuestSettingsScreen.LIST
                                    },
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("НАЗАД", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val (title, reward, dbCategory, target) = when (selectedCategory) {
                                            QuestCategory.READING -> {
                                                val book = bookTitle.trim().ifEmpty { "Book" }
                                                val pages = readingPages.toIntOrNull() ?: 15
                                                Quadruple(book, pages * 4, "mental", pages)
                                            }
                                            QuestCategory.CODING -> {
                                                val proj = projectTitle.trim().ifEmpty { "Code" }
                                                val lines = codingLines.toIntOrNull() ?: 100
                                                Quadruple(proj, (lines * 0.5).toInt().coerceIn(10, 150), "coding", lines)
                                            }
                                            QuestCategory.LANGUAGES -> {
                                                val lang = languageName.trim().ifEmpty { "Language" }
                                                val targetVal = languageTarget.toIntOrNull() ?: 10
                                                Quadruple(lang, targetVal * 3, "languages", targetVal)
                                            }
                                            QuestCategory.PHYSICAL -> {
                                                val exercise = physicalExercise.trim().ifEmpty { "Push-ups" }
                                                val cleanName = exercise.split(" (").first()
                                                val sets = physicalSets.toIntOrNull() ?: 3
                                                val reps = physicalReps.toIntOrNull() ?: 12
                                                val displayTitle = "$cleanName ($reps повт.)"
                                                val reward = sets * reps * 2
                                                Quadruple(displayTitle, reward, "physical", sets)
                                            }
                                        }

                                        coroutineScope.launch {
                                            if (currentScreen == QuestSettingsScreen.EDIT && editingQuest != null) {
                                                // UPDATE existing quest — keep progress if target hasn't changed
                                                val existing = editingQuest!!
                                                val newProgress = if (target != existing.target) 0 else existing.progress
                                                questRepository.updateQuest(
                                                    existing.copy(
                                                        title = title,
                                                        expReward = reward,
                                                        category = dbCategory,
                                                        target = target,
                                                        progress = newProgress,
                                                        isCompleted = if (target != existing.target) false else existing.isCompleted,
                                                        isPenalty = if (target != existing.target) false else existing.isPenalty,
                                                        originalTarget = target
                                                    )
                                                )
                                                Toast.makeText(context, "Квест оновлено!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                questRepository.insertQuest(
                                                    QuestEntity(
                                                        title = title,
                                                        expReward = reward,
                                                        isCompleted = false,
                                                        category = dbCategory,
                                                        progress = 0,
                                                        target = target,
                                                        isPenalty = false,
                                                        originalTarget = target
                                                    )
                                                )
                                                Toast.makeText(context, "Квест збережено!", Toast.LENGTH_SHORT).show()
                                            }
                                            resetForm()
                                            currentScreen = QuestSettingsScreen.LIST
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00FF66).copy(alpha = 0.08f),
                                        contentColor = Color(0xFF00FF66)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .border(1.dp, Color(0xFF00FF66), RoundedCornerShape(8.dp))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            if (currentScreen == QuestSettingsScreen.EDIT) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF00FF66)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentScreen == QuestSettingsScreen.EDIT) "ЗБЕРЕГТИ" else "ДОДАТИ",
                                            color = Color(0xFF00FF66),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestListItem(
    quest: QuestEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFB0E0E6).copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .background(Color(0xFF131320).copy(alpha = 0.8f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .background(
                    color = when (quest.category) {
                        "physical"  -> Color(0xFF00FF99)
                        "coding"    -> Color(0xFF7B6FE8)
                        "languages" -> Color(0xFFFFD700)
                        else        -> Color(0xFF00E6F0)
                    },
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quest.title,
                color = if (quest.isCompleted) Color(0xFF00FF66) else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "[${quest.progress}/${quest.target}] • +${quest.expReward} EXP" +
                        if (quest.isCompleted) " ✓" else "",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        if (showDeleteConfirm) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Видалити?", color = Color(0xFFFF3366), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Підтвердити", tint = Color(0xFFFF3366))
                }
                IconButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Скасувати", tint = Color.Gray)
                }
            }
        } else {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Редагувати", tint = Color(0xFFB0E0E6))
            }
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = Color(0xFFFF3366).copy(alpha = 0.7f))
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
                1.dp,
                if (isSelected) Color(0xFF00E6F0) else Color(0xFFB0E0E6).copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) Color(0xFF00E6F0).copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
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
fun DropdownItem(text: String, onClick: () -> Unit) {
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
