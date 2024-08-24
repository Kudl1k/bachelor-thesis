package cz.kudladev.domain.repository

import cz.kudladev.data.models.ChargeTrackingID

interface ChargeTrackingDao {

    suspend fun getAllChargeTracking(): List<ChargeTrackingID>
    suspend fun getChargeTrackingById(id: Int): List<ChargeTrackingID>?
    suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): ChargeTrackingID?
    suspend fun updateChargeTracking(chargeTracking: ChargeTrackingID): ChargeTrackingID?


}