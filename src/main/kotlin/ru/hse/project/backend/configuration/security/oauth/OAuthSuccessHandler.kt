package ru.hse.project.backend.configuration.security.oauth

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import ru.hse.project.backend.domain.CustomOAuth2User
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.service.CookieService
import ru.hse.project.backend.service.RefreshTokenService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuthSuccessHandler(
    private val tokenService: RefreshTokenService,
    private val cookieService: CookieService
) : SimpleUrlAuthenticationSuccessHandler() {

    private val redirectStrategy = DefaultRedirectStrategy()

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val principal = (authentication.principal as? CustomOAuth2User)
            ?: throw UserException("Неизвестный способ авторизации: ${authentication.name}")
        if (!principal.needToGenerateTokensPair) {
            redirect(request, response)
            return
        }

        val user = principal.user!!
        val cookie = cookieService.createCookieFromUserToken(tokenService.createTokenForUser(user))
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.headers.getFirst(HttpHeaders.SET_COOKIE))
        response.addHeader(HttpHeaders.AUTHORIZATION, cookie.headers.getFirst(HttpHeaders.AUTHORIZATION))

        redirect(request, response)
    }

    private fun redirect(request: HttpServletRequest, response: HttpServletResponse) {
        if (!response.isCommitted) {
            redirectStrategy.sendRedirect(request, response, "/")
        }
    }
}