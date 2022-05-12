package ru.hse.project.backend.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import ru.hse.project.backend.exception.UserException
import java.util.*


@Configuration
class JwtService(
    @Value("\${jwt.access.expiration.millis}")
    val jwtExpiration: Int
) {
    @Autowired
    private lateinit var jwtParser: JwtParser

    @Autowired
    private lateinit var jwtBuilder: JwtBuilder

    fun generateToken(id_user: Long): String =
        jwtBuilder
            .setSubject(id_user.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .compact()

    /**
     * Проверить, что токен не поддельный и не с истекшим сроком
     */
    fun validateToken(token: String) {
        try {
            jwtParser.parse(token)
        } catch (ex: ExpiredJwtException) {
            throw UserException("Исчерпанный срок для access токена. Токен: $token")
        } catch (ex: SignatureException) {
            throw UserException("Неверный секретный код для JWT аутентификации. Токен: $token")
        }
    }

    fun getIdFromToken(token: String) = jwtParser.parseClaimsJws(token).body.subject.toLong()
}