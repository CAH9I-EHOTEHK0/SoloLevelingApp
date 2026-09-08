package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val iconResId: Int,
    val currentRank: Int = -1,
    val progress: Long = 0L,
    val isCompleted: Boolean = false
)