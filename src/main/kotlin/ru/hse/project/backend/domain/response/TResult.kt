package ru.hse.project.backend.domain.response

class TResult<T>(
        val success: T? = null,
        val error: Exception? = null,
        val isSuccess: Boolean = error == null
)