package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class GetRatingRequest(
    var startPosition: Int,
    var endPosition: Int
)