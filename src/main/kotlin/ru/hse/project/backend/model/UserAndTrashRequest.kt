package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class UserAndTrashRequest(
    var id: String,
    var trashCanId: String
)