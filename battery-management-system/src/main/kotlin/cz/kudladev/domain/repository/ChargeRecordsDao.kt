package cz.kudladev.domain.repository

import cz.kudladev.data.models.ChargeRecord
import cz.kudladev.data.models.ChargeRecordInsert
import cz.kudladev.data.models.ChargeRecordWithTracking

interface ChargeRecordsDao {
    suspend fun getAllChargeRecords(): List<ChargeRecord>
    suspend fun getChargeRecordById(id: Int): ChargeRecord?
    suspend fun createChargeRecord(chargeRecord: ChargeRecordInsert): ChargeRecord
    suspend fun updateChargeRecord(chargeRecord: ChargeRecord): ChargeRecord
    suspend fun deleteChargeRecord(id: Int): ChargeRecord?

    suspend fun endChargeRecord(id: Int, capacity: Int): ChargeRecord?

    suspend fun getNotEndedChargeRecordsWithTracking(): List<ChargeRecordWithTracking>
}