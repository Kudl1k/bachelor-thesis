package cz.kudladev.domain.repository

import cz.kudladev.data.models.Cell

interface CellDao {

    suspend fun createCell(cell: Cell): Cell

}