package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.CellTracking
import cz.kudladev.data.models.Cell
import cz.kudladev.domain.repository.CellDao
import org.jetbrains.exposed.sql.insert
import java.sql.Timestamp

class CellDaoImpl: CellDao {
    override suspend fun createCell(cell: Cell): Cell {
        return try {
            dbQuery{
                CellTracking.insert {
                    it[timestamp] = cell.timestamp.toInstant()
                    it[idChargeRecord] = cell.idChargeRecord
                    it[number] = cell.number
                    it[voltage] = cell.voltage
                }
                cell
            }
        } catch (e: Exception) {
            throw e
        }
    }
}