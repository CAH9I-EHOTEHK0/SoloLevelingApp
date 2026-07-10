package ua.zxcode.sololevelingapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.zxcode.sololevelingapp.data.local.dao.*
import ua.zxcode.sololevelingapp.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        StatEntity::class,
        StatRecordEntity::class,
        QuestEntity::class,
        AchievementEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun statDao(): StatDao
    abstract fun statRecordDao(): StatRecordDao
    abstract fun questDao(): QuestDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        private const val DATABASE_NAME = "solo_leveling.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // замінити на Migration при production
                .build()
        }
    }
}