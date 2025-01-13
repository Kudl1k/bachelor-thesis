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

    suspend fun endChargeRecord(id: Int, charged_capacity: Int, discharged_capacity: Int): ChargeRecord?

    suspend fun getNotEndedChargeRecordsWithTracking(): List<ChargeRecordWithTracking>

    suspend fun checkChargeRecords(): List<ChargeRecord>

    suspend fun getNewChargeGroup(): Int

    suspend fun endGroupChargeRecords(group: Int): Boolean
}