package ru.hse.project.backend.domain.response

data class UserResponse(
    val nickName: String?,
    var firstName: String? = null,
    var secondName: String? = null,
    var email: String? = null,
    var urlImage: String? = null,
)