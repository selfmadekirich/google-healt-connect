package com.example.googlehealthconnect.repository

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GoogleHealthRepository(private val context: Context) {

    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    suspend fun addSteps(count: Long, from: Instant, to: Instant) {
        try {
            val stepsRecord = StepsRecord(
                count = count,
                startTime = from,
                endTime = to,
                startZoneOffset = null,
                endZoneOffset = null,
            )
            healthConnectClient.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            e.message?.let { Log.v("GoogleHealthConnect", "Insert error: $it") }
        }
    }

    fun convertStringToDate(s: String): Long{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return try {
            val localDate = LocalDate.parse(s, formatter)
            localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        } catch (e: Exception) {
            0L
        }
    }
    suspend fun readStepsByTimeRange(
        startTimeString: String,
        endTimeString: String
    ) : MutableList<StepsRecord> {
        val res = mutableListOf<StepsRecord>()
        var startTime = if(startTimeString == "") 0L else
            convertStringToDate(startTimeString)
        var endTime = if(endTimeString == "")
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        else
            convertStringToDate(endTimeString)
        try {
            val s = Instant.ofEpochSecond(startTime)
            val t = Instant.ofEpochSecond(endTime + 1)
            val response =
                healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(
                            s,
                            t)
                    )
                )
            for (stepRecord in response.records) {
                res.add(stepRecord)
            }
        } catch (e: Exception) {
            Log.d("GoogleHealthConnect", "Reading error: " + e.message)
        }
        finally {
            return res
        }
    }

    suspend fun deleteStep(record: StepsRecord
    ) {
        try {
            healthConnectClient.deleteRecords(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    record.startTime.minusSeconds(5),
                    record.endTime.plusSeconds(5))
            )
        } catch (e: Exception) {
            Log.d("GoogleHealthConnect", "Deleting error: " + e.message)
        }
    }
}