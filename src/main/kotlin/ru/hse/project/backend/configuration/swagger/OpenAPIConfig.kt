package ru.hse.project.backend.configuration.swagger

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Авторизация через JWT. Здесь вводится токен, который затем будет добавлен в заголовок Authorization, после слова Bearer."
)
class OpenAPIConfig {
}