package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.Cell
import cz.kudladev.data.entities.CellTracking
import cz.kudladev.data.models.CellModel
import cz.kudladev.data.models.CellTrackingModel
import cz.kudladev.data.models.FormatedCellTracking
import cz.kudladev.data.models.convertVoltageToVolt
import cz.kudladev.domain.repository.CellDao
import org.jetbrains.exposed.sql.insert
import java.time.Instant

class CellDaoImpl: CellDao {
    override suspend fun createCell(cell: CellModel): CellModel {
        return try {
            dbQuery{
                Cell.insert {
                    it[idChargeRecord] = cell.idChargeRecord
                    it[number] = cell.number
                }
                cell
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createCellTracking(cellTracking: CellTrackingModel): FormatedCellTracking {
        return try {
            dbQuery{
                CellTracking.insert {
                    it[timestamp] = cellTracking.timestamp.toInstant()
                    it[idChargeRecord] = cellTracking.idChargeRecord
                    it[number] = cellTracking.number
                    it[voltage] = cellTracking.voltage
                }
                FormatedCellTracking(
                    timestamp = cellTracking.timestamp,
                    idChargeRecord = cellTracking.idChargeRecord,
                    number = cellTracking.number,
                    voltage = convertVoltageToVolt(cellTracking.voltage)
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }
}