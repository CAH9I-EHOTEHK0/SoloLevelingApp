package ua.zxcode.sololevelingapp.data.local.dao

import androidx.room.*
import ua.zxcode.sololevelingapp.data.local.entity.StatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatDao {

    @Query("SELECT * FROM stats")
    fun observeAllStats(): Flow<List<StatEntity>>

    @Query("SELECT * FROM stats WHERE id = :statId")
    fun observeStat(statId: String): Flow<StatEntity?>

    @Query("SELECT * FROM stats WHERE id = :statId")
    suspend fun getStat(statId: String): StatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: StatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: List<StatEntity>)

    @Update
    suspend fun updateStat(stat: StatEntity)

    @Delete
    suspend fun deleteStat(stat: StatEntity)
}