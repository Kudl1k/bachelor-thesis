package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.SizeEntity
import cz.kudladev.data.entities.Sizes
import cz.kudladev.data.models.Size
import cz.kudladev.data.models.SizeWithBatteries
import cz.kudladev.domain.repository.SizeDao
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class SizeDaoImpl: SizeDao {
    override suspend fun getAllSizes(): List<Size> {
        return try {
            dbQuery{
                SizeEntity.all().map { EntityParser.toSize(it) }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getSizeByName(name: String): Size? {
        return try {
            dbQuery {
                SizeEntity.find { Sizes.name eq name }.singleOrNull()?.let { EntityParser.toSize(it) }
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
                SizeEntity.new(name) {}
                Size(name = name)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun deleteSize(name: String): Boolean {
        return try {
            dbQuery {
                SizeEntity.find { Sizes.name eq name }.singleOrNull()?.delete() ?: false
                true
            }
        } catch (e: Exception) {
            println(e)
            false
        }
    }


}