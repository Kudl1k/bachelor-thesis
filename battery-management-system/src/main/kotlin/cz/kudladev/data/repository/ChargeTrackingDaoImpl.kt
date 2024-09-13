package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.CellTracking
import cz.kudladev.data.entities.ChargeRecordEntity
import cz.kudladev.data.entities.ChargeTrackingEntity
import cz.kudladev.data.entities.ChargeTrackings
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.data.models.FormatedChargeTracking
import cz.kudladev.data.models.convertVoltageToVolt
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import java.time.Instant

class ChargeTrackingDaoImpl: ChargeTrackingDao {
    override suspend fun getAllChargeTracking(): List<FormatedChargeTracking> {
        return try {
            dbQuery {
                ChargeTrackingEntity.all().map { chargeTrackingEntity ->
                    EntityParser.toFormatedChargeTracking(chargeTrackingEntity)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeTrackingById(id: Int): List<FormatedChargeTracking>? {
        return try {
            dbQuery {
                ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.map { chargeTrackingEntity ->
                    EntityParser.toFormatedChargeTracking(chargeTrackingEntity)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): FormatedChargeTracking? {
        return try {
            val current_timestamp = Instant.now()
            dbQuery {
                val chargeRecord = ChargeRecordEntity.findById(chargeTracking.charge_record_id) ?: throw IllegalArgumentException("No charge record found for id ${chargeTracking.charge_record_id}")
                val chargeTrackingEntity = ChargeTrackingEntity.new(current_timestamp) {
                    this.chargeRecordEntity = chargeRecord
                    this.charging = chargeTracking.charging
                    this.realCapacity = chargeTracking.real_capacity
                    this.capacity = chargeTracking.capacity
                    this.voltage = chargeTracking.voltage
                    this.current = chargeTracking.current
                }
                EntityParser.toFormatedChargeTracking(chargeTrackingEntity)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getLastChargeTrackingById(id: Int): FormatedChargeTracking? {
        return try {
            dbQuery {
                val chargeTrackingEntity = ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.sortedByDescending { it.timestamp }.firstOrNull()
                if (chargeTrackingEntity != null) {

                    EntityParser.toFormatedChargeTracking(chargeTrackingEntity)
                } else {
                    null
                }
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
                ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq id_charge_record) and (ChargeTrackings.charging eq Op.FALSE) }.orderBy( ChargeTrackings.id to SortOrder.ASC ).map {

                    EntityParser.toFormatedChargeTracking(it)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun updateChargeTrackingValues(
        id_charge_record: Int,
        capacity: Int
    ): List<FormatedChargeTracking> {
        return try {
            dbQuery {
                val chargeRecord = ChargeRecordEntity.findById(id_charge_record) ?: throw IllegalArgumentException("No charge record found for id $id_charge_record")
                ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq id_charge_record) and (ChargeTrackings.charging eq Op.TRUE) }.sortedByDescending { it.timestamp }.map {
                    it.realCapacity = capacity + it.capacity
                }
                ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq id_charge_record) and (ChargeTrackings.charging eq Op.TRUE) }.orderBy( ChargeTrackings.id to SortOrder.ASC ).map {

                    EntityParser.toFormatedChargeTracking(it)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }

    }
}