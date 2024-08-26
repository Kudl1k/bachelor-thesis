package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.ChargeRecordEntity
import cz.kudladev.data.entities.ChargeTrackingEntity
import cz.kudladev.data.entities.ChargeTrackings
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import java.time.Instant

class ChargeTrackingDaoImpl: ChargeTrackingDao {
    override suspend fun getAllChargeTracking(): List<ChargeTrackingID> {
        return try {
            dbQuery{
                ChargeTrackingEntity.all().map { EntityParser.toChargeTracking(it) }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeTrackingById(id: Int): List<ChargeTrackingID>? {
        return try {
            dbQuery {
                ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.map { EntityParser.toChargeTracking(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): ChargeTrackingID? {
        return try {
            dbQuery {
                ChargeTrackingEntity.new(Instant.now()) {
                    chargeRecordEntity = ChargeRecordEntity.findById(chargeTracking.charge_record_id) ?: throw IllegalArgumentException("No charge record found for id ${chargeTracking.charge_record_id}")
                    capacity = chargeTracking.capacity
                    voltage = chargeTracking.voltage
                    current = chargeTracking.current
                }
            }
            chargeTracking
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getLastChargeTrackingById(id: Int): ChargeTrackingID? {
        return try {
            dbQuery {
                ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq id }.sortedByDescending { it.timestamp }.firstOrNull()?.let { EntityParser.toChargeTracking(it) }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }


}