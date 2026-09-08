package ua.zxcode.sololevelingapp.core.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {

    val healthConnectClient by lazy {
        if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        return try {
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(permissions)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTodaySteps(): Long {
        val client = healthConnectClient ?: return 0L
        try {
            val startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfToday = Instant.now()
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfToday)
                )
            )
            return response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    suspend fun getStepsHistory(startTime: Instant, endTime: Instant): List<StepsRecord> {
        val client = healthConnectClient ?: return emptyList()
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            return response.records
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getLastHeartRate(): Int {
        val client = healthConnectClient ?: return 0
        try {
            val start = Instant.now().minus(1, ChronoUnit.DAYS)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            val record = response.records.firstOrNull() ?: return 0
            return record.samples.lastOrNull()?.beatsPerMinute?.toInt() ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    suspend fun getHeartRateHistory(startTime: Instant, endTime: Instant): List<HeartRateRecord> {
        val client = healthConnectClient ?: return emptyList()
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            return response.records
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getLastOxygen(): Float {
        val client = healthConnectClient ?: return 0f
        try {
            val start = Instant.now().minus(7, ChronoUnit.DAYS)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            val record = response.records.firstOrNull() ?: return 0f
            return record.percentage.value.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0f
        }
    }

    suspend fun getOxygenHistory(startTime: Instant, endTime: Instant): List<OxygenSaturationRecord> {
        val client = healthConnectClient ?: return emptyList()
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            return response.records
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getLastSleepDurationHours(): Float {
        val client = healthConnectClient ?: return 0f
        try {
            val start = Instant.now().minus(2, ChronoUnit.DAYS)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            val session = response.records.firstOrNull() ?: return 0f
            val durationMillis = session.endTime.toEpochMilli() - session.startTime.toEpochMilli()
            return durationMillis.toFloat() / (1000 * 60 * 60)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0f
        }
    }

    suspend fun getSleepHistory(startTime: Instant, endTime: Instant): List<SleepSessionRecord> {
        val client = healthConnectClient ?: return emptyList()
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            return response.records
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getLastWeight(): Float {
        val client = healthConnectClient ?: return 0f
        try {
            val start = Instant.now().minus(30, ChronoUnit.DAYS)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            val record = response.records.firstOrNull() ?: return 0f
            return record.weight.inKilograms.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0f
        }
    }

    suspend fun getWeightHistory(startTime: Instant, endTime: Instant): List<WeightRecord> {
        val client = healthConnectClient ?: return emptyList()
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            return response.records
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
