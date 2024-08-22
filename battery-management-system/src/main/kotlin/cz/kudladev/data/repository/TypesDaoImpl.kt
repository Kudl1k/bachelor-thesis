package cz.kudladev.data.repository

import cz.kudladev.data.Batteries
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Sizes
import cz.kudladev.data.Types
import cz.kudladev.data.models.Size
import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries
import cz.kudladev.domain.repository.TypesDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TypesDaoImpl(): TypesDao {

    override suspend fun getAllTypes(): List<Type?> {
        return try {
            dbQuery {
                Types.selectAll().map { ResultRowParser.resultRowToType(it) }
            }
        } catch (e: Throwable) {
            println(e)
            return emptyList()
        }
    }

    override suspend fun getTypeByShortcut(shortcut: String): Type? {
        return try {
            dbQuery {
                Types.selectAll().where { Types.shortcut eq shortcut }.map { ResultRowParser.resultRowToType(it) }.singleOrNull()
            }
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    override suspend fun getTypeByShortcutWithBatteries(shortcut: String): TypeBatteries? {
        return try {
            dbQuery {
                val result = (Types leftJoin Batteries leftJoin Sizes).select {
                    Types.shortcut eq shortcut
                }.map { it }

                if (result.isEmpty()) {
                    null
                } else {
                    val type = ResultRowParser.resultRowToType(result.first())
                    print(result.first())
                    val batteries = result.mapNotNull { row ->
                        if (row[Batteries.idBattery] != null) {
                            ResultRowParser.resultRowToBattery(row)
                        } else {
                            null
                        }
                    }

                    TypeBatteries(
                        shortcut = type.shortcut,
                        name = type.name,
                        batteries = batteries
                    )
                }
            }
        } catch (e: Throwable) {
            println(e)
            null
        }
    }

    override suspend fun insertType(type: Type): Type? {
        return try {
            dbQuery {
                Types.insert {
                    it[shortcut] = type.shortcut
                    it[name] = type.name
                }
            }
            type
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    override suspend fun updateType(type: Type): Type? {
        return try {
            dbQuery {
                Types.update({ Types.shortcut eq type.shortcut }) {
                    it[shortcut] = type.shortcut
                    it[name] = type.name
                }
            }
            type
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    override suspend fun deleteType(shortcut: String): Type? {
        return try {
            val type = getTypeByShortcut(shortcut) ?: throw IllegalArgumentException("No type found for shortcut $shortcut")
            dbQuery {
                Types.deleteWhere { Types.shortcut eq shortcut }
            }
            type
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

}