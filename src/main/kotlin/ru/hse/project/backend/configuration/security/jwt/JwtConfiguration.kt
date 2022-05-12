package ru.hse.project.backend.configuration.security.jwt

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfiguration(
    @Value("\${jwt.secretkey}")
    val jwtSecret: String,
) {
    @Bean
    fun parserJwt(): JwtParser {
        return Jwts.parser().setSigningKey(jwtSecret)
    }

    @Bean
    fun builderJwt(): JwtBuilder {
        return Jwts.builder().signWith(SignatureAlgorithm.HS512, jwtSecret)
    }
}