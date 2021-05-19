package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class CountRowMapper: RowMapper<Int> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): Int {
        return resultSet.getInt("count")
    }

}