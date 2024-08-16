package cz.kudladev.data.repository

import cz.kudladev.data.Batteries
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Types
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries
import cz.kudladev.domain.repository.TypesDao
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.sql.Timestamp

class TypesDaoImpl(): TypesDao {

    private fun resultRowToType(row: ResultRow): Type {
        return Type(
            id = row[Types.idType],
            shortcut = row[Types.shortcut],
            name = row[Types.name]
        )
    }

    private fun resultRowToTypeBatteries(row: ResultRow): TypeBatteries {
        return TypeBatteries(
            id = row[Types.idType],
            shortcut = row[Types.shortcut],
            name = row[Types.name],
            batteries = listOf(
                Battery(
                    id = row[Batteries.idBattery],
                    type = Type(
                        id = row[Types.idType],
                        shortcut = row[Types.shortcut],
                        name = row[Types.name]
                    ),
                    size = row[Batteries.size],
                    factory_capacity = row[Batteries.factoryCapacity],
                    voltage = row[Batteries.voltage],
                    last_charged_capacity = row[Batteries.lastChargedCapacity],
                    last_time_charged_at = if (row[Batteries.lastTimeChargedAt] == null) null else Timestamp.from(row[Batteries.lastTimeChargedAt]),
                    created_at = Timestamp.from(row[Batteries.createdAt])
                )
            )
        )
    }


    override suspend fun getAllTypes(): List<Type> {
        return try {
            dbQuery {
                Types.selectAll().map { resultRowToType(it) }
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun getTypeById(id: Int): Type? {
        return try {
            dbQuery {
                Types.selectAll().where { Types.idType eq id }.map { resultRowToType(it) }.singleOrNull()
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun getTypeByIdWithBatteries(id: Int): TypeBatteries? {
        return try {
            dbQuery {
                (Types innerJoin Batteries).slice(
                                Types.idType,
                                Types.shortcut,
                                Types.name,
                                Batteries.idBattery,
                                Batteries.size,
                                Batteries.factoryCapacity,
                                Batteries.voltage,
                                Batteries.lastChargedCapacity,
                                Batteries.lastTimeChargedAt,
                                Batteries.createdAt
                            ).selectAll().where { Types.idType eq id }.map { resultRowToTypeBatteries(it) }.singleOrNull()
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun insertType(type: Type): Type {
        return try {
            val insertedId = dbQuery {
                Types.insert {
                    it[shortcut] = type.shortcut
                    it[name] = type.name
                } get Types.idType
            }
            type.copy(id = insertedId)
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun updateType(type: Type): Type {
        return try {
            dbQuery {
                Types.update({ Types.idType eq type.id!! }) {
                    it[shortcut] = type.shortcut
                    it[name] = type.name
                }
            }
            type
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun deleteType(id: Int): Type {
        return try {
            val type = getTypeById(id) ?: throw IllegalArgumentException("No type found for id $id")
            dbQuery {
                Types.deleteWhere { Types.idType eq id }
            }
            type
        } catch (e: Throwable) {
            throw e
        }
    }


}