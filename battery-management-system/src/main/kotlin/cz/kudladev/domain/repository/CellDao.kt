package cz.kudladev.domain.repository

import cz.kudladev.data.models.CellModel

interface CellDao {

    suspend fun createCell(cell: CellModel): CellModel

}