package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import ru.hse.project.backend.model.TrashCan
import java.sql.ResultSet
import java.sql.SQLException

class TrashRowMapper : RowMapper<TrashCan> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): TrashCan {
        return TrashCan(
            resultSet.getString("id"),
            resultSet.getString("address"),
            resultSet.getString("title"),
            resultSet.getDouble("latitude"),
            resultSet.getDouble("longitude"),
            resultSet.getString("image"),
            resultSet.getBoolean("paper"),
            resultSet.getBoolean("glass"),
            resultSet.getBoolean("plastic"),
            resultSet.getBoolean("metal")
        )
    }
}