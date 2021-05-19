package ru.hse.project.backend.model

class TResult<T>(
        val success: T? = null,
        val error: Exception? = null,
        val isSuccess: Boolean = error == null
)