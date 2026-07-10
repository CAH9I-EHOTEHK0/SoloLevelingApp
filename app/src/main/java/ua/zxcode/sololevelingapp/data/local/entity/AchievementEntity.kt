package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ranks: E=0, D=1, C=2, B=3, A=4, S=5 (-1 means not started yet)
 * progress = total accumulated value (days, sets, pages)
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val iconResId: Int,
    val currentRank: Int = -1,      // -1=not started, 0=E, 1=D, 2=C, 3=B, 4=A, 5=S
    val progress: Long = 0L,        // accumulated progress (days, pages, sets)
    val isCompleted: Boolean = false // true when rank S is reached
)