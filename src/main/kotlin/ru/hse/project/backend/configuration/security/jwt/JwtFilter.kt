package ru.hse.project.backend.configuration.security.jwt

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.User
import ru.hse.project.backend.repository.UserRepository
import ru.hse.project.backend.service.JwtService
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val handlerExceptionResolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Получаем хедер авторизации
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header.isNullOrEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val user: User

        try {
            // Проверяем, что токен не просроченный и не подделанный
            val token = header.split(" ")[1].trim()

            jwtService.validateToken(token)

            user = getUserFromToken(token)
        } catch (ex: UserException) {
            handlerExceptionResolver.resolveException(request, response, null, ex)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(
            user, null, user.authorities
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    private fun getUserFromToken(
        token: String
    ): User {
        val accountId = jwtService.getIdFromToken(token)
        return userRepository.findById(accountId).orElseThrow { UserException("Аккаунта с id $accountId из токена не существует. Токен: $token") }
    }
}