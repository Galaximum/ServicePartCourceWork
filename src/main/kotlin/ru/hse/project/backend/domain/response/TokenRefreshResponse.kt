package ru.hse.project.backend.domain.response

data class TokenRefreshResponse(
    val expiresIn: Int?,
    val refreshExpiresIn: Long?,
)