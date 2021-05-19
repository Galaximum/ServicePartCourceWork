package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import ru.hse.project.backend.model.RatingUsers
import java.sql.ResultSet
import java.sql.SQLException

class RatingRowMapper : RowMapper<RatingUsers> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): RatingUsers {
        return RatingUsers(
            resultSet.getString("nick_name"),
            resultSet.getString("url_image"),
            resultSet.getInt("score"),
            resultSet.getInt("number_row")
        )
    }
}