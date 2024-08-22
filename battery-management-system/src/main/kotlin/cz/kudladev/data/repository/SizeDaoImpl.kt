package cz.kudladev.data.repository

import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Sizes
import cz.kudladev.data.models.Size
import cz.kudladev.data.models.SizeWithBatteries
import cz.kudladev.domain.repository.SizeDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class SizeDaoImpl: SizeDao {
    override suspend fun getAllSizes(): List<Size> {
        return try {
            dbQuery{
                Sizes.selectAll().map {
                    ResultRowParser.resultRowToSize(it)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getSizeByName(name: String): Size? {
        return try {
            dbQuery {
                Sizes.select(Sizes.name eq name).map {
                    ResultRowParser.resultRowToSize(it)
                }.firstOrNull()
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getSizeByNameWithBatteries(name: String): SizeWithBatteries? {
        return try {
            dbQuery {
                val result = Sizes.selectAll().map { it }
                if (result.isEmpty()) {
                    null
                } else {
                    val size = ResultRowParser.resultRowToSize(result.first())
                    val batteries = result.mapNotNull { row ->
                        if (row != null) {
                            ResultRowParser.resultRowToBattery(row)
                        } else {
                            null
                        }
                    }
                    SizeWithBatteries(
                        name = size.name,
                        batteries = batteries
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun insertSize(name: String): Size? {
        return try {
            dbQuery {
                Sizes.insert {
                    it[Sizes.name] = name
                }
                Size(name)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun deleteSize(name: String): Boolean {
        return try {
            dbQuery {
                Sizes.deleteWhere { Sizes.name eq name } > 0
            }
        } catch (e: Exception) {
            println(e)
            false
        }
    }


}