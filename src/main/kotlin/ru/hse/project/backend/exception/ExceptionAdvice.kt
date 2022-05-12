package ru.hse.project.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import ru.hse.project.backend.domain.response.ExceptionResponse

@RestControllerAdvice
class ExceptionAdvice {

    @ExceptionHandler(value = [UserException::class, TrashCanException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleCustomException(ex: UserException, request: WebRequest): ExceptionResponse {
        println("BAD REQUEST: ${ex.message}")
        return ExceptionResponse(HttpStatus.BAD_REQUEST.value(), ex.message)
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingFieldRequest(ex: Exception) = ExceptionResponse(
        HttpStatus.BAD_REQUEST.value(),
        ex.message
    ).also {
        ex.printStackTrace()
    }
}