package ua.zxcode.sololevelingapp.data.local.dao

import androidx.room.*
import ua.zxcode.sololevelingapp.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests")
    fun observeAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE isCompleted = 0")
    fun observeActiveQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE category = :category")
    fun observeQuestsByCategory(category: String): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :questId")
    suspend fun getQuestById(questId: Long): QuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("UPDATE quests SET isCompleted = :isCompleted WHERE id = :questId")
    suspend fun setQuestCompleted(questId: Long, isCompleted: Boolean)

    @Delete
    suspend fun deleteQuest(quest: QuestEntity)

    @Query("DELETE FROM quests")
    suspend fun deleteAllQuests()
}