package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.ChargeRecordEntity
import cz.kudladev.data.entities.ChargeTrackingEntity
import cz.kudladev.data.entities.ChargeTrackings
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.data.models.FormatedChargeTracking
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import java.sql.Timestamp
import java.time.Instant

class ChargeTrackingDaoImpl: ChargeTrackingDao {
    override suspend fun getAllChargeTracking(): List<FormatedChargeTracking> {
        return try {
            dbQuery{
                ChargeTrackingEntity.all().map { EntityParser.toFormatedChargeTracking(it) }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeTrackingById(id: Int): List<FormatedChargeTracking>? {
        return try {
            dbQuery {
                ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.map { EntityParser.toFormatedChargeTracking(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): FormatedChargeTracking? {
        return try {
            val timestamp = Instant.now()
            dbQuery {
                ChargeTrackingEntity.new(timestamp) {
                    chargeRecordEntity = ChargeRecordEntity.findById(chargeTracking.charge_record_id) ?: throw IllegalArgumentException("No charge record found for id ${chargeTracking.charge_record_id}")
                    charging = chargeTracking.charging
                    realCapacity = chargeTracking.real_capacity
                    capacity = chargeTracking.capacity
                    voltage = chargeTracking.voltage
                    current = chargeTracking.current
                }
            }
            FormatedChargeTracking(
                timestamp = Timestamp.from(timestamp),
                charge_record_id = chargeTracking.charge_record_id,
                charging = chargeTracking.charging,
                real_capacity = chargeTracking.real_capacity.toFloat() / 100.0f,
                capacity = chargeTracking.capacity.toFloat() / 100.0f,
                voltage = chargeTracking.voltage.toFloat() / 1000.0f,
                current = chargeTracking.current.toFloat() / 1000.0f
            )
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getLastChargeTrackingById(id: Int): FormatedChargeTracking? {
        return try {
            dbQuery {
                ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.sortedByDescending { it.timestamp }.firstOrNull()?.let { EntityParser.toFormatedChargeTracking(it) }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateDischargeTrackingValues(id_charge_record: Int, capacity: Int): List<FormatedChargeTracking> {
        return try {
            dbQuery {
                val chargeRecord = ChargeRecordEntity.findById(id_charge_record) ?: throw IllegalArgumentException("No charge record found for id $id_charge_record")
                ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq id_charge_record) and (ChargeTrackings.charging eq Op.FALSE) }.sortedByDescending { it.timestamp }.map {
                    it.realCapacity = capacity - it.capacity
                }
                ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq id_charge_record) and (ChargeTrackings.charging eq Op.FALSE) }.orderBy( ChargeTrackings.id to SortOrder.ASC ).map { EntityParser.toFormatedChargeTracking(it) }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }
}