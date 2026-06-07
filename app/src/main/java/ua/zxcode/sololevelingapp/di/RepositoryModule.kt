package ua.zxcode.sololevelingapp.di

import ua.zxcode.sololevelingapp.data.repository.*
import ua.zxcode.sololevelingapp.data.repository.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    @Binds @Singleton
    abstract fun bindQuestRepository(impl: QuestRepositoryImpl): QuestRepository

    @Binds @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository
}