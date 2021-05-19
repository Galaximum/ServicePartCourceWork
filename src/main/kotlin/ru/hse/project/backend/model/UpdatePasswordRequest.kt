package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UpdatePasswordRequest(
    var id: String,
    var password: String
)
