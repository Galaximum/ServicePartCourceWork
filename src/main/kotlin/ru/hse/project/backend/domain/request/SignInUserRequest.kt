package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
class SignInUserRequest(var email: String, var password: String)