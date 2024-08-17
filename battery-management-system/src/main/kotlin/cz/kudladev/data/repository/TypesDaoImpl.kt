package cz.kudladev.data.repository

import cz.kudladev.data.Batteries
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Types
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries
import cz.kudladev.domain.repository.TypesDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.sql.Timestamp

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

    override suspend fun getTypeById(id: Int): Type? {
        return try {
            dbQuery {
                Types.selectAll().where { Types.idType eq id }.map { ResultRowParser.resultRowToType(it) }.singleOrNull()
            }
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    override suspend fun getTypeByIdWithBatteries(id: Int): TypeBatteries? {
        return try {
            dbQuery {
                val result = (Types leftJoin Batteries).select {
                    Types.idType eq id
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
                        id = type.id,
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
            val insertedId = dbQuery {
                Types.insert {
                    it[shortcut] = type.shortcut
                    it[name] = type.name
                } get Types.idType
            }
            type.copy(id = insertedId)
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    override suspend fun updateType(type: Type): Type? {
        return try {
            dbQuery {
                Types.update({ Types.idType eq type.id!! }) {
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

    override suspend fun deleteType(id: Int): Type? {
        return try {
            val type = getTypeById(id) ?: throw IllegalArgumentException("No type found for id $id")
            dbQuery {
                Types.deleteWhere { Types.idType eq id }
            }
            type
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

}