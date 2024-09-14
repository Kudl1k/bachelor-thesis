package cz.kudladev.domain.repository

import cz.kudladev.data.models.CellModel
import cz.kudladev.data.models.CellTrackingModel
import cz.kudladev.data.models.FormatedCellTracking

interface CellDao {

    suspend fun createCell(cell: CellModel): CellModel

    suspend fun createCellTracking(cellTracking: CellTrackingModel): FormatedCellTracking

}