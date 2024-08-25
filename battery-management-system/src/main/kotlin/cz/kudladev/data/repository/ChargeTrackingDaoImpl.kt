package cz.kudladev.data.repository

import cz.kudladev.data.ChargeTracking
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant

class ChargeTrackingDaoImpl: ChargeTrackingDao {
    override suspend fun getAllChargeTracking(): List<ChargeTrackingID> {
        return try {
            dbQuery{
                ChargeTracking.selectAll().map {
                    ResultRowParser.resultRowToChargeTracking(it)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeTrackingById(id: Int): List<ChargeTrackingID>? {
        return try {
            dbQuery {
                ChargeTracking.select {
                    ChargeTracking.idChargeRecord eq id
                }.map {
                    ResultRowParser.resultRowToChargeTracking(it)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): ChargeTrackingID? {
        return try {
            dbQuery {
                ChargeTracking.insert {
                    it[timestamp] = Instant.now()
                    it[idChargeRecord] = chargeTracking.charge_record_id
                    it[capacity] = chargeTracking.capacity
                    it[voltage] = chargeTracking.voltage
                    it[current] = chargeTracking.current
                }
            }
            chargeTracking
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateChargeTracking(chargeTracking: ChargeTrackingID): ChargeTrackingID? {
        return try {
            dbQuery {
                ChargeTracking.update({ ChargeTracking.idChargeRecord eq chargeTracking.charge_record_id }) {
                    it[timestamp] = Instant.now()
                    it[idChargeRecord] = chargeTracking.charge_record_id
                    it[capacity] = chargeTracking.capacity
                    it[voltage] = chargeTracking.voltage
                    it[current] = chargeTracking.current
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
                ChargeTracking.select {
                    ChargeTracking.idChargeRecord eq id
                }.orderBy(ChargeTracking.timestamp, SortOrder.DESC).limit(1).map {
                    ResultRowParser.resultRowToChargeTracking(it)
                }.firstOrNull()
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }


}