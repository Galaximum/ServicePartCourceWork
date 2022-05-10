package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
class RegisterUserRequest(
    val nickName: String,
    val firstName: String,
    val secondName: String,
    val password: String,
    val email: String
)