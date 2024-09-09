package cz.kudladev.domain.repository

import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.data.models.FormatedChargeTracking

interface ChargeTrackingDao {

    suspend fun getAllChargeTracking(): List<FormatedChargeTracking>
    suspend fun getChargeTrackingById(id: Int): List<FormatedChargeTracking>?
    suspend fun createChargeTracking(chargeTracking: ChargeTrackingID): FormatedChargeTracking?

    suspend fun getLastChargeTrackingById(id: Int): FormatedChargeTracking?

    suspend fun updateDischargeTrackingValues(id_charge_record: Int, capacity: Int): List<FormatedChargeTracking>;
    suspend fun updateChargeTrackingValues(id_charge_record: Int, capacity: Int): List<FormatedChargeTracking>;

}