package ua.zxcode.sololevelingapp.presentation.achievements

import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity

object AchievementIds {
    const val SYSTEM_ARCHITECT = "system_architect"
    const val MONARCH_LIBRARY = "monarch_library"
    const val POLYGLOT = "polyglot"
    const val ROAD_TO_OLYMPUS = "road_to_olympus"
    const val PLAYERS_IRL = "players_irl"
}

val RANK_LABELS = listOf("E", "D", "C", "B", "A", "S")

val RANK_COLORS = listOf(
    0xFF8A95A5.toLong(),
    0xFFCD7F32.toLong(),
    0xFF00FF66.toLong(),
    0xFF00D2FF.toLong(),
    0xFFBD00FF.toLong(),
    0xFFFFB800.toLong()
)

val ACHIEVEMENT_THRESHOLDS = mapOf(
    AchievementIds.SYSTEM_ARCHITECT  to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.MONARCH_LIBRARY   to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.POLYGLOT          to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.ROAD_TO_OLYMPUS   to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.PLAYERS_IRL       to longArrayOf(10, 50, 100, 500, 1_000, 10_000)
)

fun computeRank(achievementId: String, progress: Long): Int {
    val thresholds = ACHIEVEMENT_THRESHOLDS[achievementId] ?: return -1
    var rank = -1
    for (i in thresholds.indices) {
        if (progress >= thresholds[i]) rank = i else break
    }
    return rank
}

fun nextThreshold(achievementId: String, progress: Long): Long? {
    val thresholds = ACHIEVEMENT_THRESHOLDS[achievementId] ?: return null
    return thresholds.firstOrNull { it > progress }
}

fun progressText(achievement: AchievementEntity): String {
    if (achievement.currentRank == 5) return "Максимальний ранг отримано!"
    val next = nextThreshold(achievement.id, achievement.progress) ?: return "Максимальний ранг отримано!"
    val label = when (achievement.id) {
        AchievementIds.SYSTEM_ARCHITECT -> "днів кодингу"
        AchievementIds.MONARCH_LIBRARY  -> "сторінок прочитано"
        AchievementIds.POLYGLOT         -> "днів практики"
        AchievementIds.ROAD_TO_OLYMPUS  -> "підходів виконано"
        AchievementIds.PLAYERS_IRL      -> "ідеальних днів"
        else -> ""
    }
    return "${achievement.progress}/$next $label"
}

fun defaultAchievements(): List<AchievementEntity> = listOf(
    AchievementEntity(
        id = AchievementIds.SYSTEM_ARCHITECT,
        title = "Архітектор Системи",
        iconResId = R.drawable.achvsystemarchitect
    ),
    AchievementEntity(
        id = AchievementIds.MONARCH_LIBRARY,
        title = "Бібліотека Монарха",
        iconResId = R.drawable.achvmonarchlibrary
    ),
    AchievementEntity(
        id = AchievementIds.POLYGLOT,
        title = "Лінгвістичний Поліглот",
        iconResId = R.drawable.achvlinguisticpolyglot
    ),
    AchievementEntity(
        id = AchievementIds.ROAD_TO_OLYMPUS,
        title = "Шлях до Олімпу",
        iconResId = R.drawable.achvroadtoolympus
    ),
    AchievementEntity(
        id = AchievementIds.PLAYERS_IRL,
        title = "Гравці в реальне життя",
        iconResId = R.drawable.achvplayersirl
    )
)
