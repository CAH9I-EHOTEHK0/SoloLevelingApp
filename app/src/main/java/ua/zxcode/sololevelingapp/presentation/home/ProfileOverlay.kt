package ua.zxcode.sololevelingapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
@Composable
fun ProfileOverlay(
    onDismiss: () -> Unit, // Функція, яка закриє оверлей
    onQuestSettingsClick: () -> Unit // <--- Додаємо сюди функцію для відкриття налаштувань квестів
) {
    var username by remember { mutableStateOf("Сон Джин Ву") }
    var isEditing by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf(username) }

    var selectedGender by remember { mutableStateOf("Male") }

    var isSoundEnabled by remember { mutableStateOf(true) } // true = Увімк, false = Вимк

    // Замість Dialog використовуємо Popup, який не багує з координатами курсора
    Popup(
        onDismissRequest = { onDismiss() },
        properties = PopupProperties(
            focusable = true, // Щоб відкривалася клавіатура і працювало введення
            excludeFromSystemGesture = true
        )
    ) {
        // Задній темний фон на весь екран, щоб затінити все ззаду і зловити кліки для закриття
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            // Контент картки профілю
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
                    .imePadding() // Тепер відступ клавіатури працюватиме адекватно всередині Popup
                    .border(
                        width = 2.dp,
                        color = Color(0xFFB0E0E6),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = Color(0xFF0A0A12),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "User Profile",
                        color = Color(0xFF00E6F0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!isEditing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Користувач: ",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = username,
                                    color = Color(0xFF00E6F0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            IconButton(onClick = {
                                textInput = username
                                isEditing = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Редагувати",
                                    tint = Color(0xFFB0E0E6)
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                label = { Text("Користувач") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color(0xFF00E6F0),
                                    unfocusedLabelColor = Color.Gray,
                                    focusedBorderColor = Color(0xFF00E6F0),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            IconButton(onClick = {
                                if (textInput.isNotBlank()) {
                                    username = textInput
                                }
                                isEditing = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Зберегти",
                                    tint = Color(0xFF00FF66)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- СТАНІ ДЛЯ ДАТИ НАРОДЖЕННЯ (додай до інших remember на початку ProfileOverlay) ---
                    var birthDate by remember { mutableStateOf("12.05.2004") }
                    var isEditingDate by remember { mutableStateOf(false) }

// Списки для барабанів
                    val days = remember { (1..31).map { it.toString().padStart(2, '0') } }
                    val months = remember { (1..12).map { it.toString().padStart(2, '0') } }
                    val years = remember { (1970..2026).map { it.toString() }.reversed() } // Від нових до старих

// Тимчасові індекси для вибору в барабанах
                    var selectedDayIndex by remember { mutableStateOf(days.indexOf(birthDate.split(".")[0])) }
                    var selectedMonthIndex by remember { mutableStateOf(months.indexOf(birthDate.split(".")[1])) }
                    var selectedYearIndex by remember { mutableStateOf(years.indexOf(birthDate.split(".")[2])) }

                    Spacer(modifier = Modifier.height(16.dp))

// --- РЯДОК ДАТИ НАРОДЖЕННЯ ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!isEditingDate) {
                            // Режим перегляду дати
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Дата народження: ",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = birthDate,
                                    color = Color(0xFF00E6F0), // Твій фірмовий неон
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            IconButton(onClick = {
                                // Розпарсимо поточну дату, щоб барабани стали на правильні місця при відкритті
                                val parts = birthDate.split(".")
                                selectedDayIndex = days.indexOf(parts[0]).coerceAtLeast(0)
                                selectedMonthIndex = months.indexOf(parts[1]).coerceAtLeast(0)
                                selectedYearIndex = years.indexOf(parts[2]).coerceAtLeast(0)
                                isEditingDate = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Редагувати дату",
                                    tint = Color(0xFFB0E0E6)
                                )
                            }
                        } else {
                            // Режим редагування (Барабани)
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Барабан Днів
                                WheelPicker(
                                    items = days,
                                    initialIndex = selectedDayIndex,
                                    onItemSelected = { selectedDayIndex = it },
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = "/", color = Color.Gray, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 4.dp))

                                // Барабан Місяців
                                WheelPicker(
                                    items = months,
                                    initialIndex = selectedMonthIndex,
                                    onItemSelected = { selectedMonthIndex = it },
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = "/", color = Color.Gray, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 4.dp))

                                // Барабан Років
                                WheelPicker(
                                    items = years,
                                    initialIndex = selectedYearIndex,
                                    onItemSelected = { selectedYearIndex = it },
                                    modifier = Modifier.weight(1.2f) // Трохи ширше, бо там 4 цифри
                                )
                            }

                            IconButton(onClick = {
                                // Збираємо дату докупи при збереженні
                                val d = days[selectedDayIndex]
                                val m = months[selectedMonthIndex]
                                val y = years[selectedYearIndex]
                                birthDate = "$d.$m.$y"
                                isEditingDate = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Зберегти дату",
                                    tint = Color(0xFF00FF66)
                                )
                            }
                        }
                    }

//                    Text(
//                        text = "Поточний титул: Радість згасання",
//                        color = Color.Gray,
//                        fontSize = 14.sp,
//                        modifier = Modifier.align(Alignment.Start)
//                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- ПЕРЕМИКАЧ СТАТІ (MALE / FEMALE) ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Стать персонажа",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Робимо панель кнопок трохи вужчою за картку
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFB0E0E6).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = Color(0xFF121224), // Темніший підклад під кнопки
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp), // Падінг навколо кнопок всередині рамки
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Кнопка MALE
                            TextButton(
                                onClick = { selectedGender = "Male" },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedGender == "Male") Color(0xFF00E6F0).copy(alpha = 0.2f) else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = if (selectedGender == "Male") 1.dp else 0.dp,
                                        color = if (selectedGender == "Male") Color(0xFF00E6F0) else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    text = "MALE",
                                    color = if (selectedGender == "Male") Color(0xFF00E6F0) else Color.Gray,
                                    fontWeight = if (selectedGender == "Male") FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }

                            // Кнопка FEMALE
                            TextButton(
                                onClick = { selectedGender = "Female" },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedGender == "Female") Color(0xFF00E6F0).copy(alpha = 0.2f) else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = if (selectedGender == "Female") 1.dp else 0.dp,
                                        color = if (selectedGender == "Female") Color(0xFF00E6F0) else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    text = "FEMALE",
                                    color = if (selectedGender == "Female") Color(0xFF00E6F0) else Color.Gray,
                                    fontWeight = if (selectedGender == "Female") FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Створюємо клікабельний контейнер для звукових ефектів
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.85f) // Робимо ширину красивою та симетричною
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // Прибираємо стандартне біле коло розмиття при кліку, щоб зберегти строгий RPG стиль
                            ) {
                                isSoundEnabled = !isSoundEnabled // Перемикаємо стан при натисканні
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Центруємо весь напис
                    ) {
                        Text(
                            text = "Сповіщення: ",
                            color = Color(0xFFB0E0E6), // Твій базовий колір інтерфейсу
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isSoundEnabled) "[ УВІМК ]" else "[ ВИМК ]",
                            color = if (isSoundEnabled) Color(0xFF99FF99) else Color(0xFFAA6666), // Зелений неоновий якщо увімкнено, сірий якщо вимкнено
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "[ Налаштування квестів ]",
                        color = Color(0xFFB0E0E6), // Твій фірмовий колір інтерфейсу
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // Без стандартного андроїдівського білого кола при кліку
                            ) {
                                onQuestSettingsClick() // Викликаємо відкриття іншого оверлея
                            }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { onDismiss() }) {
                        Text(text = "ЗАКРИТИ", color = Color(0xFFFF3366), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 40.dp
    // Додаємо порожні елементи зверху і знизу, щоб поточний елемент завжди був по центру
    val listItems = remember(items) { listOf("") + items + listOf("") }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Відстежуємо, який елемент зараз по центру
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            if (centerIndex in items.indices) {
                onItemSelected(centerIndex)
            }
        }
    }

    Box(
        modifier = modifier.height(itemHeight * 3), // Показуємо 3 елементи одночасно
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            items(listItems.size) { index ->
                val itemText = listItems[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    // Визначаємо, чи елемент зараз по центру (активний)
                    val isSelected = index == listState.firstVisibleItemIndex + 1
                    Text(
                        text = itemText,
                        color = if (isSelected) Color(0xFF00E6F0) else Color.Gray, // Неоновий фокус
                        fontSize = if (isSelected) 18.sp else 14.sp
                    )
                }
            }
        }
    }
}