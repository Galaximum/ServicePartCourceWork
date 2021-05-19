package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import ru.hse.project.backend.model.RatingUser
import java.sql.ResultSet
import java.sql.SQLException

class ScoreRowMapper:RowMapper<RatingUser> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): RatingUser {
        return RatingUser(
            resultSet.getInt("score"),
            resultSet.getInt("number_row")
        )
    }
}