package ru.hse.project.backend.service

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.project.backend.domain.TokenForUser
import ru.hse.project.backend.domain.response.TokenRefreshResponse

@Service
class CookieService {

    fun createCookieFromUserToken(token: TokenForUser) = createCookie(
        token.refreshToken,
        token.refreshExpiresIn!!.div(1000),
        TokenRefreshResponse(
            token.expiresIn,
            token.refreshExpiresIn
        ),
        token.accessToken
    )

    fun <T> createCookie(
        refreshToken: String = "",
        maxSeconds: Long = 0,
        body: T,
        accessToken: String? = null
    ): ResponseEntity<T> {
        val cookie = ResponseCookie.from("refreshToken", refreshToken)

        with(cookie) {
            httpOnly(true)
            maxAge(maxSeconds)
            path("/api/auth/refresh-token")
            // todo сделать это включенным для прод деплоя
            secure(false)
        }

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.build().toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .body(body)
    }
}