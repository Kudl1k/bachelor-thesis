package cz.kudladev.backend.service

import cz.kudladev.backend.exceptions.DbElementInsertException
import cz.kudladev.backend.exceptions.DbElementNotFoundException
import cz.kudladev.backend.models.entities.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

class TypeService(private val connection: Connection) {

    companion object {
        private val SELECT_ALL_TYPES = "SELECT * FROM type"
        private val SELECT_TYPE_BY_ID = "SELECT * FROM type WHERE id = ?"
        private val INSERT_TYPE = "INSERT INTO type (shortcut, name) VALUES (?, ?)"
        private val DELETE_TYPE = "DELETE FROM type WHERE id = ?"
    }


    suspend fun getAllTypes(): List<Type> = withContext(Dispatchers.IO) {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(SELECT_ALL_TYPES)
        val types = mutableListOf<Type>()
        while (resultSet.next()) {
            val id_type = resultSet.getInt("id_type")
            val shortcut = resultSet.getString("shortcut")
            val name = resultSet.getString("name")
            types.add(
                Type(
                    id_type = id_type,
                    shortcut = shortcut,
                    name = name
                )
            )
        }
        return@withContext types
    }

    suspend fun getTypeById(id: Int): Type = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_TYPE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            val id_type = resultSet.getInt("id_type")
            val shortcut = resultSet.getString("shortcut")
            val name = resultSet.getString("name")
            return@withContext Type(
                id_type = id_type,
                shortcut = shortcut,
                name = name
            )
        } else {
            throw DbElementNotFoundException("Type with id $id not found")
        }
    }

    suspend fun createType(type: Type): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_TYPE)
        statement.setString(1, type.shortcut)
        statement.setString(2, type.name)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw DbElementInsertException("Unable to retrive the id of the newly created type")
        }
    }

    suspend fun deleteType(id: Int): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_TYPE)
        statement.setInt(1, id)
        return@withContext statement.executeUpdate()
    }



}