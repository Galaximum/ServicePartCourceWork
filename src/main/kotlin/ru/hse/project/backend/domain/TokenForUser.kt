package ru.hse.project.backend.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
data class TokenForUser(
    var accessToken: String = "",
    @Value("\${jwt.access.expiration.millis}")
    val expiresIn: Int? = null,
    var refreshToken: String = "",
    @Value("\${jwt.refresh.expiration.millis}")
    val refreshExpiresIn: Long? = null,
)