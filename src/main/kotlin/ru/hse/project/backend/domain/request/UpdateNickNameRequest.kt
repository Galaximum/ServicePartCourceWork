package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UpdateNickNameRequest(
    var id: Long,
    var nickName: String
)
