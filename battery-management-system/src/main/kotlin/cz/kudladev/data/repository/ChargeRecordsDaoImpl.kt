package cz.kudladev.data.repository

import cz.kudladev.data.*
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.models.ChargeRecord
import cz.kudladev.data.models.ChargeRecordInsert
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Timestamp
import java.time.Clock

class ChargeRecordsDaoImpl: ChargeRecordsDao {
    override suspend fun getAllChargeRecords(): List<ChargeRecord> {
        return try {
            dbQuery {
                (ChargeRecords innerJoin Batteries innerJoin Types innerJoin Chargers).selectAll().map {
                    ResultRowParser.resultRowToChargerRecord(it)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeRecordById(id: Int): ChargeRecord? {
        return try {
            dbQuery {
                (ChargeRecords innerJoin Batteries innerJoin Types innerJoin Chargers).select {
                    ChargeRecords.idChargeRecord eq id
                }.map {
                    ResultRowParser.resultRowToChargerRecord(it)
                }.singleOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeRecord(chargeRecord: ChargeRecordInsert): ChargeRecord {
        val time = Clock.systemUTC().instant()
        return try {
            if (chargeRecord.battery_id == null) {
                throw IllegalArgumentException("Battery id is null")
            }
            if (chargeRecord.battery_id == null) {
                throw IllegalArgumentException("Charger id is null")
            }
            val insertedId = dbQuery {
                Batteries.selectAll().where { Batteries.idBattery eq chargeRecord.battery_id }.singleOrNull()
                    ?: throw IllegalArgumentException("No battery found for id ${chargeRecord.battery_id}")
                Chargers.selectAll().where { Chargers.idCharger eq chargeRecord.charger_id }.singleOrNull()
                    ?: throw IllegalArgumentException("No charger found for id ${chargeRecord.charger_id}")
                ChargeRecords.insert {
                    it[program] = chargeRecord.program
                    it[slot] = chargeRecord.slot
                    it[startedAt] = time
                    it[finishedAt] = null
                    it[chargedCapacity] = 0
                    it[idBattery] = chargeRecord.battery_id
                    it[idCharger] = chargeRecord.charger_id
                } get ChargeRecords.idChargeRecord
            }
            getChargeRecordById(insertedId)!!
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateChargeRecord(chargeRecord: ChargeRecord): ChargeRecord {
        return try {
            dbQuery {
                ChargeRecords.update({ ChargeRecords.idChargeRecord eq chargeRecord.idChargeRecord!! }) {
                    it[program] = chargeRecord.program
                    it[slot] = chargeRecord.slot
                    it[startedAt] = chargeRecord.startedAt.toInstant()
                    it[finishedAt] = chargeRecord.finishedAt?.toInstant()
                    it[chargedCapacity] = chargeRecord.chargedCapacity
                    it[idBattery] = chargeRecord.battery.id!!
                    it[idCharger] = chargeRecord.charger.id!!
                }
            }
            chargeRecord
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteChargeRecord(id: Int): ChargeRecord {
        return try {
            val chargeRecord = getChargeRecordById(id) ?: throw IllegalArgumentException("No charge record found for id $id")
            dbQuery {
                ChargeRecords.deleteWhere { ChargeRecords.idChargeRecord eq id }
            }
            chargeRecord
        } catch (e: Exception) {
            throw e
        }
    }
}