package ua.zxcode.sololevelingapp.di

import android.content.Context
import ua.zxcode.sololevelingapp.data.local.dao.*
import ua.zxcode.sololevelingapp.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideStatDao(db: AppDatabase): StatDao = db.statDao()
    @Provides fun provideStatRecordDao(db: AppDatabase): StatRecordDao = db.statRecordDao()
    @Provides fun provideQuestDao(db: AppDatabase): QuestDao = db.questDao()
    @Provides fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
}
