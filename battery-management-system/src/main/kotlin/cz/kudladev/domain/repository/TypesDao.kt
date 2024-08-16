package cz.kudladev.domain.repository

import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries

interface TypesDao {

    suspend fun getAllTypes(): List<Type>

    suspend fun getTypeById(id: Int): Type?

    suspend fun getTypeByIdWithBatteries(id: Int): TypeBatteries?

    suspend fun insertType(type: Type): Type?

    suspend fun updateType(type: Type): Type?

    suspend fun deleteType(id: Int): Type?

}