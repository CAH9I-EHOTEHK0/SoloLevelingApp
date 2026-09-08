package ua.zxcode.sololevelingapp.data.local.dao

import androidx.room.*
import ua.zxcode.sololevelingapp.data.local.entity.StatRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatRecordDao {
    @Query("SELECT * FROM stat_records WHERE statId = :statId ORDER BY timestamp ASC")
    fun observeRecordsForStat(statId: String): Flow<List<StatRecordEntity>>

    @Query("""
        SELECT * FROM stat_records 
        WHERE statId = :statId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun observeLatestRecordsForStat(statId: String, limit: Int): Flow<List<StatRecordEntity>>

    @Query("""
        SELECT * FROM stat_records 
        WHERE statId = :statId 
          AND timestamp BETWEEN :from AND :to 
        ORDER BY timestamp ASC
    """)
    fun observeRecordsInRange(statId: String, from: Long, to: Long): Flow<List<StatRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: StatRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<StatRecordEntity>)

    @Update
    suspend fun updateRecord(record: StatRecordEntity)

    @Delete
    suspend fun deleteRecord(record: StatRecordEntity)

    @Query("DELETE FROM stat_records WHERE statId = :statId")
    suspend fun deleteAllRecordsForStat(statId: String)
}