package ua.zxcode.sololevelingapp.data.repository

import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun observeAllQuests(): Flow<List<QuestEntity>>
    fun observeActiveQuests(): Flow<List<QuestEntity>>
    fun observeQuestsByCategory(category: String): Flow<List<QuestEntity>>
    suspend fun insertQuest(quest: QuestEntity): Long
    suspend fun insertQuests(quests: List<QuestEntity>)
    suspend fun updateQuest(quest: QuestEntity)
    suspend fun setQuestCompleted(questId: Long, isCompleted: Boolean)
    suspend fun deleteQuest(quest: QuestEntity)
    suspend fun deleteAllQuests()
}