package cz.kudladev.domain

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.CellDaoImpl
import cz.kudladev.data.repository.ChargeRecordsDaoImpl
import cz.kudladev.data.repository.ChargeTrackingDaoImpl
import cz.kudladev.data.repository.ChargersDaoImpl
import cz.kudladev.data.repository.SizeDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.CellDao

object Global {
    val batteriesDao: BatteriesDao = BatteriesDaoImpl()
    val cellDao: CellDao = CellDaoImpl()
    val chargeRecordsDao = ChargeRecordsDaoImpl()
    val chargeTrackingDao = ChargeTrackingDaoImpl()
    val chargersDao = ChargersDaoImpl()
    val sizeDao = SizeDaoImpl()
    val typesDao = TypesDaoImpl()
}