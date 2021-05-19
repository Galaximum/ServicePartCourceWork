package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UpdateNickNameRequest(
    var id: String,
    var nickName: String
)
