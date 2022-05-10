package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UpdatePasswordRequest(
    var id: Long,
    var password: String
)
