package ua.zxcode.sololevelingapp.presentation.achievements

import ua.zxcode.sololevelingapp.R
import ua.zxcode.sololevelingapp.data.local.entity.AchievementEntity

/**
 * Achievement IDs (stable identifiers in the DB)
 */
object AchievementIds {
    const val SYSTEM_ARCHITECT = "system_architect"   // coding days
    const val MONARCH_LIBRARY = "monarch_library"     // pages read
    const val POLYGLOT = "polyglot"                   // language days
    const val ROAD_TO_OLYMPUS = "road_to_olympus"     // gym sets
    const val PLAYERS_IRL = "players_irl"             // perfect days (all quests done)
}

/**
 * Rank index → display label
 */
val RANK_LABELS = listOf("E", "D", "C", "B", "A", "S")

/**
 * Rank index → color hex
 */
val RANK_COLORS = listOf(
    0xFF8A95A5.toLong(),  // E — steel grey
    0xFFCD7F32.toLong(),  // D — bronze
    0xFF00FF66.toLong(),  // C — neon lime
    0xFF00D2FF.toLong(),  // B — electric blue
    0xFFBD00FF.toLong(),  // A — magic purple
    0xFFFFB800.toLong()   // S — legendary gold
)

/**
 * Thresholds per achievement:
 * index 0 = threshold to reach rank E
 * index 1 = threshold to reach rank D
 * ...
 * index 5 = threshold to reach rank S
 */
val ACHIEVEMENT_THRESHOLDS = mapOf(
    AchievementIds.SYSTEM_ARCHITECT  to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.MONARCH_LIBRARY   to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.POLYGLOT          to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.ROAD_TO_OLYMPUS   to longArrayOf(10, 50, 100, 500, 1_000, 10_000),
    AchievementIds.PLAYERS_IRL       to longArrayOf(10, 50, 100, 500, 1_000, 10_000)
)

/** Returns rank index (-1..5) for given progress */
fun computeRank(achievementId: String, progress: Long): Int {
    val thresholds = ACHIEVEMENT_THRESHOLDS[achievementId] ?: return -1
    var rank = -1
    for (i in thresholds.indices) {
        if (progress >= thresholds[i]) rank = i else break
    }
    return rank
}

/** Next threshold for progress display, null if S rank reached */
fun nextThreshold(achievementId: String, progress: Long): Long? {
    val thresholds = ACHIEVEMENT_THRESHOLDS[achievementId] ?: return null
    return thresholds.firstOrNull { it > progress }
}

/** Human-readable progress text for the achievement card */
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

/** Default list of achievements to seed the DB on first run */
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
