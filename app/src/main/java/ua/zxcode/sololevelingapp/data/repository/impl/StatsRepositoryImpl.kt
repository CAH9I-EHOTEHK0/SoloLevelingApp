package ua.zxcode.sololevelingapp.data.repository.impl

import ua.zxcode.sololevelingapp.data.local.dao.StatDao
import ua.zxcode.sololevelingapp.data.local.dao.StatRecordDao
import ua.zxcode.sololevelingapp.data.local.entity.StatEntity
import ua.zxcode.sololevelingapp.data.local.entity.StatRecordEntity
import ua.zxcode.sololevelingapp.data.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statDao: StatDao,
    private val statRecordDao: StatRecordDao
) : StatsRepository {

    override fun observeAllStats(): Flow<List<StatEntity>> = statDao.observeAllStats()
    override fun observeStat(statId: String): Flow<StatEntity?> = statDao.observeStat(statId)
    override suspend fun insertStat(stat: StatEntity) = statDao.insertStat(stat)
    override suspend fun insertStats(stats: List<StatEntity>) = statDao.insertStats(stats)
    override suspend fun updateStat(stat: StatEntity) = statDao.updateStat(stat)

    override fun observeRecordsForStat(statId: String): Flow<List<StatRecordEntity>> =
        statRecordDao.observeRecordsForStat(statId)

    override fun observeLatestRecordsForStat(statId: String, limit: Int): Flow<List<StatRecordEntity>> =
        statRecordDao.observeLatestRecordsForStat(statId, limit)

    override fun observeRecordsInRange(statId: String, from: Long, to: Long): Flow<List<StatRecordEntity>> =
        statRecordDao.observeRecordsInRange(statId, from, to)

    override suspend fun insertRecord(record: StatRecordEntity): Long =
        statRecordDao.insertRecord(record)

    override suspend fun deleteAllRecordsForStat(statId: String) =
        statRecordDao.deleteAllRecordsForStat(statId)
}
