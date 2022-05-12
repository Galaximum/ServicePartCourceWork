package ru.hse.project.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.hse.project.backend.domain.TokenForUser
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.Token
import ru.hse.project.backend.model.User
import ru.hse.project.backend.repository.TokenRepository
import java.time.Instant
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: TokenRepository,
    private val jwtService: JwtService,
    @Value("\${jwt.refresh.expiration.millis}")
    private val refreshTokenDuration: Long
) {
    @Autowired
    private lateinit var tokenForUser: TokenForUser

    fun findByRefreshToken(token: String) =
        refreshTokenRepository.findByRefreshToken(token)
            ?: throw UserException("Такого refresh-токена нет в БД. Refresh-токен: $token")

    fun createRefreshToken(user: User): Token {
        val refreshToken = Token(
            user = user,
            expiryDate = Instant.now().plusMillis(refreshTokenDuration),
            // todo сделать другой рандом
            refreshToken = UUID.randomUUID().toString()
        )

        return refreshTokenRepository.save(refreshToken)
    }

    fun verifyToken(token: Token, fingerPrint: String) {
        with(token) {
            if (expiryDate.isBefore(Instant.now())) {
                refreshTokenRepository.delete(token)
                throw UserException("Срок действия refresh token вышел. Refresh-токен: $token")
            }
        }
    }

    fun createTokenForUser(user: User): TokenForUser {
        val accessToken = jwtService.generateToken(user.id)
        val refreshToken = createRefreshToken(user)

        with(tokenForUser) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken.refreshToken
        }

        return tokenForUser
    }

    fun deleteToken(token: Token) {
        refreshTokenRepository.delete(token)
    }
}