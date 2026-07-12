package ua.zxcode.sololevelingapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 5,
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

        // Міграція 4→5: додаємо поля для відстеження стану щоденного скиду
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user ADD COLUMN lastResetDate TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE user ADD COLUMN hadPenaltyYesterday INTEGER NOT NULL DEFAULT 0")
            }
        }

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
                .addMigrations(MIGRATION_4_5)
                .build()
        }
    }
}