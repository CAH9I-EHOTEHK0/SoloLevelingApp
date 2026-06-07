package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val iconResId: Int,
    val isUnlocked: Boolean = false,
    val unlockDate: Long? = null
)