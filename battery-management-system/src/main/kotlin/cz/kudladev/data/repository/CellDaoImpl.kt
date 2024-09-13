package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.CellTracking
import cz.kudladev.data.models.CellModel
import cz.kudladev.domain.repository.CellDao
import org.jetbrains.exposed.sql.insert

class CellDaoImpl: CellDao {
    override suspend fun createCell(cell: CellModel): CellModel {
        return try {
            dbQuery{
                CellTracking.insert {
                    it[idChargeRecord] = cell.idChargeRecord
                    it[number] = cell.number
                }
                cell
            }
        } catch (e: Exception) {
            throw e
        }
    }
}