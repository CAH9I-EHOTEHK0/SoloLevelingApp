package ua.zxcode.sololevelingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stat_records",
    foreignKeys = [
        ForeignKey(
            entity = StatEntity::class,
            parentColumns = ["id"],
            childColumns = ["statId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("statId")]
)
data class StatRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val statId: String,
    val value: Float,
    val timestamp: Long
)