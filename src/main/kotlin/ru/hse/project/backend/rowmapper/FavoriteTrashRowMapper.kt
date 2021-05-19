package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class FavoriteTrashRowMapper: RowMapper<String> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): String {
        return resultSet.getString("id_trashcan")
    }


}