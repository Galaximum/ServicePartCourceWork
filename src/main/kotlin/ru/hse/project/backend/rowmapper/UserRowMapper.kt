package ru.hse.project.backend.rowmapper

import org.springframework.jdbc.core.RowMapper
import ru.hse.project.backend.model.User
import java.sql.ResultSet
import java.sql.SQLException

class UserRowMapper : RowMapper<User> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): User {
        return User(
            resultSet.getString("id"),
            resultSet.getString("nick_name"),
            resultSet.getString("first_name"),
            resultSet.getString("second_name"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getString("url_image")
        )
    }
}