package ru.hse.project.backend.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.*
import ru.hse.project.backend.rowmapper.*
import java.util.*


@Repository
class UserRepositoryImpl @Autowired constructor(private val jdbcTemplate: JdbcTemplate) : UserRepository {

    override fun registerUser(request: RegisterUserRequest): TResult<String> {
        val id = UUID.randomUUID().toString()
        return try {

            val tableSize = jdbcTemplate.query("SELECT count(id) AS 'count' FROM user", CountRowMapper())[0]


            val count = jdbcTemplate.update(
                "INSERT IGNORE INTO user (id, nick_name, first_name, second_name, email, password, url_image,score,number_row) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)",
                id,
                request.nickName,
                request.firstName,
                request.secondName,
                request.email,
                request.password,
                "https://storage.yandexcloud.net/imagesecoapp/user_images/$id.jpg",
                0,
                tableSize
            )
            if (count != 0) {
                TResult(success = id)
            } else {
                TResult(error = IllegalArgumentException("Email or nickname is not available"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun signInUser(request: SignInUserRequest): TResult<User> {
        return try {
            val users = jdbcTemplate.query(
                "SELECT * FROM user WHERE email = ? AND password = ?",
                UserRowMapper(),
                request.email,
                request.password
            )
            if (users.size != 0) {
                TResult(success = users[0])
            } else {
                TResult(error = IllegalArgumentException("Wrong email or password"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun updateNickName(request: UpdateNickNameRequest): TResult<Void> {
        return try {
            val count =
                jdbcTemplate.update(
                    "update user set " +
                            "nick_name='${request.nickName}' " +
                            "where id='${request.id}'"
                )

            if (count != 0) {
                TResult(success = null)
            } else {
                TResult(error = IllegalArgumentException("NickName don't updated"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun updateEmail(request: UpdateEmailRequest): TResult<Void> {
        return try {
            val count =
                jdbcTemplate.update(
                    "update user set " +
                            "email='${request.email}' " +
                            "where id='${request.id}'"
                )

            if (count != 0) {
                TResult(success = null)
            } else {
                TResult(error = IllegalArgumentException("Email don't updated"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun updatePassword(request: UpdatePasswordRequest): TResult<Void> {
        return try {
            jdbcTemplate.update(
                "update user set " +
                        "password='${request.password}' " +
                        "where id='${request.id}'"
            )
            TResult(success = null)
        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun deleteUser(id: String): TResult<Void> {
        return try {
            val count =
                jdbcTemplate.update(
                    "delete from user where id=?",
                    id
                )

            jdbcTemplate.update(
                "delete from favorite_trashcan_array where id_user=?",
                id
            )


            if (count != 0) {
                TResult(success = null)
            } else {
                TResult(error = IllegalArgumentException("User not found"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }
    }


    override fun getRatingUsers(request: GetRatingRequest): TResult<List<RatingUsers>> {
        val start = request.startPosition
        var end = request.endPosition
        val tableSize = jdbcTemplate.query("SELECT count(id) AS 'count' FROM user", CountRowMapper())[0]
        if (end > tableSize - 1) {
            end = tableSize - 1
        }


        return try {
            val result = jdbcTemplate.query(
                "select nick_name, url_image, score,number_row FROM user WHERE number_row>=? and number_row<=?",
                RatingRowMapper(),
                start,
                end
            )

            if (result.size != 0) {
                TResult(success = result)
            } else {
                TResult(error = IllegalArgumentException("Error bounds"))
            }
        } catch (e: Exception) {
            TResult(error = e)
        }

    }

    override fun getScoreUser(id: String): TResult<RatingUser> {

        return try {
            val result = jdbcTemplate.query(
                "SELECT score,number_row FROM user where id='$id'",
                ScoreRowMapper()
            )
            if (result.isNotEmpty()) {
                TResult(success = result[0])
            } else {
                TResult(error = Exception("user not found"))
            }
        } catch (e: Exception) {
            TResult(error = e)

        }
    }

    override fun increaseScore(request: UserAndTrashRequest): TResult<Void> {
        return try {
            val count = jdbcTemplate.update(
                "INSERT IGNORE INTO visited_trashcans (id_user, id_trashcan) VALUES (?, ?)",
                request.id,
                request.trashCanId
            )

            if (count != 0) {
                jdbcTemplate.update("update user set score=score+1 where id ='${request.id}' LIMIT 1 ")
                TResult(success = null)
            } else {
                TResult(error = java.lang.IllegalArgumentException("this place was visited earlier"))
            }

        } catch (e: Exception) {
            TResult(error = e)
        }
    }

    override fun updateRating(): TResult<Void> {

        return try {
            val result = jdbcTemplate.query("SELECT* FROM user ORDER BY score DESC", UpdateRatingRowMapper())
            for (res in result) {
                jdbcTemplate.update("update user set number_row=? where id =? LIMIT 1 ", res.numberRow, res.id)
            }
            TResult(success = null)
        } catch (ex: Exception) {
            TResult(error = ex)
        }
    }

    override fun clearVisitedTable(): TResult<Void> {
        return try {
            jdbcTemplate.update("")
            TResult(success = null)
        } catch (ex: Exception) {
            TResult(error = ex)
        }


    }
}

