package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatEntity(
    @PrimaryKey
    val id: String,           // "weight", "pulse", "oxygen", "steps", "sleep"
    val title: String,
    val iconResId: Int,
    val lastValue: Float
)