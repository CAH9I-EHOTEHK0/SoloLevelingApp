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
    val xpToNextLevel: Int,
    val gender: String = "Male",
    val birthDate: String = "12.05.2004",
    val isSoundEnabled: Boolean = true
)