package cz.kudladev.domain.repository

import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries

interface TypesDao {

    suspend fun getAllTypes(): List<Type?>

    suspend fun getTypeByShortcut(shortcut: String): Type?

    suspend fun getTypeByShortcutWithBatteries(shortcut: String): TypeBatteries?

    suspend fun insertType(type: Type): Type?

    suspend fun updateType(type: Type): Type?

    suspend fun deleteType(shortcut: String): Type?

}