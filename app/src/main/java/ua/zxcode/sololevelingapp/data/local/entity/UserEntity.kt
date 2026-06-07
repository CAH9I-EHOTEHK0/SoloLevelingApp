package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val nickname: String,
    val currentLevel: Int,
    val currentXp: Int,
    val xpToNextLevel: Int
)