package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UserAndTrashRequest(
    var id: Long,
    var trashCanId: Long
)