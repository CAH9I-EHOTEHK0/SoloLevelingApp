package ua.zxcode.sololevelingapp.data.repository.impl

import ua.zxcode.sololevelingapp.data.local.dao.QuestDao
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import ua.zxcode.sololevelingapp.data.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestRepositoryImpl @Inject constructor(
    private val questDao: QuestDao
) : QuestRepository {

    override fun observeAllQuests(): Flow<List<QuestEntity>> = questDao.observeAllQuests()
    override fun observeActiveQuests(): Flow<List<QuestEntity>> = questDao.observeActiveQuests()
    override fun observeQuestsByCategory(category: String): Flow<List<QuestEntity>> =
        questDao.observeQuestsByCategory(category)
    override suspend fun insertQuest(quest: QuestEntity): Long = questDao.insertQuest(quest)
    override suspend fun insertQuests(quests: List<QuestEntity>) = questDao.insertQuests(quests)
    override suspend fun updateQuest(quest: QuestEntity) = questDao.updateQuest(quest)
    override suspend fun setQuestCompleted(questId: Long, isCompleted: Boolean) =
        questDao.setQuestCompleted(questId, isCompleted)
    override suspend fun deleteQuest(quest: QuestEntity) = questDao.deleteQuest(quest)
    override suspend fun deleteAllQuests() = questDao.deleteAllQuests()
    override suspend fun resetAllQuestsProgress() = questDao.resetAllQuestsProgress()
}