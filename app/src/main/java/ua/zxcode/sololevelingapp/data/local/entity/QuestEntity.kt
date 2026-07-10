package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val expReward: Int,
    val isCompleted: Boolean = false,
    val category: String,         // "mental", "coding", "languages", "physical"
    val progress: Int = 0,
    val target: Int = 1,
    val isPenalty: Boolean = false,
    val originalTarget: Int = 1
)