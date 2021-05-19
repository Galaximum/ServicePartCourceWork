package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import ru.hse.project.backend.model.UpdateRating
import java.sql.ResultSet
import java.sql.SQLException

class UpdateRatingRowMapper:RowMapper<UpdateRating> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): UpdateRating {
        return UpdateRating(
            resultSet.getString("id"),
            i
        )
    }
}