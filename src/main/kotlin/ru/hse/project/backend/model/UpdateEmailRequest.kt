package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UpdateEmailRequest(
    var id: String,
    var email: String
)
