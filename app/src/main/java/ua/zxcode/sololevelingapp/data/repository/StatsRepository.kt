package ua.zxcode.sololevelingapp.data.repository

import ua.zxcode.sololevelingapp.data.local.entity.StatEntity
import ua.zxcode.sololevelingapp.data.local.entity.StatRecordEntity
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeAllStats(): Flow<List<StatEntity>>
    fun observeStat(statId: String): Flow<StatEntity?>
    suspend fun insertStat(stat: StatEntity)
    suspend fun insertStats(stats: List<StatEntity>)
    suspend fun updateStat(stat: StatEntity)

    fun observeRecordsForStat(statId: String): Flow<List<StatRecordEntity>>
    fun observeLatestRecordsForStat(statId: String, limit: Int): Flow<List<StatRecordEntity>>
    fun observeRecordsInRange(statId: String, from: Long, to: Long): Flow<List<StatRecordEntity>>
    suspend fun insertRecord(record: StatRecordEntity): Long
    suspend fun deleteAllRecordsForStat(statId: String)
}