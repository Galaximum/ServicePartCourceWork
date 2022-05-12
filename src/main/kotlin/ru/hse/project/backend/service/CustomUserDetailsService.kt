package ru.hse.project.backend.service

import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.hse.project.backend.model.User
import ru.hse.project.backend.repository.UserRepository

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(nickName: String): User = userRepository.findByNickName(nickName)
        .orElseThrow { InternalAuthenticationServiceException("Пользователя с nickName: $nickName не существует") }
}