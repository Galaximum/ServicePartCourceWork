package ru.hse.project.backend.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import ru.hse.project.backend.model.User


data class CustomOAuth2User(
    private val oAuthAuthorities: MutableSet<GrantedAuthority>,
    private val oAuthAttributes: Map<String, Any>,
    private val nameAttributeKey: String,
    /**
     * false - если пользователь авторизован, т.е. у него уже есть пара токенов
     */
    val needToGenerateTokensPair: Boolean,
    var user: User? = null
) : OAuth2User {
    override fun getName(): String = nameAttributeKey

    override fun getAttributes() = oAuthAttributes

    override fun getAuthorities() = oAuthAuthorities
}