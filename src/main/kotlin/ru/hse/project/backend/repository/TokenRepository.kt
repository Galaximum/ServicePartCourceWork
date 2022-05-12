package ru.hse.project.backend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.Token
import java.time.Instant

@Repository
interface TokenRepository : JpaRepository<Token, Long> {
    fun findByRefreshToken(token: String): Token?
    fun getTokensByExpiryDateBefore(date: Instant): List<Token>
}