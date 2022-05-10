package ru.hse.project.backend.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.domain.request.RegisterUserRequest
import ru.hse.project.backend.domain.request.SignInUserRequest
import ru.hse.project.backend.domain.request.UpdateEmailRequest
import ru.hse.project.backend.domain.request.UpdateNickNameRequest
import ru.hse.project.backend.domain.request.UpdatePasswordRequest
import ru.hse.project.backend.domain.request.UserAndTrashRequest
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.*
import ru.hse.project.backend.service.UserService

@RestController
@RequestMapping(value = ["/users"])
class UserController @Autowired constructor(private val userService: UserService) {

    @PostMapping(value = ["registerUser"])
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<Long> {
        val user = userService.save(
            User(
                nickName = request.nickName,
                firstName = request.firstName,
                secondName = request.secondName,
                password = request.password,
                email = request.email
            )
        )
        return ResponseEntity(user.id, HttpStatus.OK)
    }

    @PostMapping(value = ["signInUser"], produces = ["application/json"])
    fun signInUser(@RequestBody request: SignInUserRequest) = userService.signInUser(request)

    @PatchMapping(value = ["updateNickName"])
    fun updateNickName(@RequestBody request: UpdateNickNameRequest) {
        val user = userService.getUserOrElseThrow(request.id)
        user.nickName = request.nickName
        userService.save(user)
    }

    @PatchMapping(value = ["updateEmail"])
    fun updateEmail(@RequestBody request: UpdateEmailRequest) {
        val user = userService.getUserOrElseThrow(request.id)
        user.nickName = request.email
        userService.save(user)
    }

    @PatchMapping(value = ["updatePassword"])
    fun updatePassword(@RequestBody request: UpdatePasswordRequest) {
        val user = userService.getUserOrElseThrow(request.id)
        user.nickName = request.password
        userService.save(user)
    }

    @DeleteMapping(value = ["deleteUser"])
    fun deleteUser(@RequestParam("id") id: Long) = userService.deleteById(id)

    @PostMapping(value = ["usersRating"], produces = ["application/json"])
    fun getRatingUsers(@RequestBody request: GetRatingRequest) = userService.getRatingUsers(request)

    @GetMapping(value = ["userRating"], produces = ["application/json"])
    fun getScoreUser(@RequestParam("id") id: Long) = userService.getUserScore(id)

    @PostMapping(value = ["updateUserRating"])
    fun increaseScore(@RequestBody request: UserAndTrashRequest) = userService.increaseScore(request)

    @GetMapping(value = ["updateRating"])
    fun updateRating(): ResponseEntity<Void> {
        throw UserException("Deprecated API method")
    }

    @GetMapping(value = ["clearVisitedTable"])
    fun clearVisitedTable() {
        throw UserException("Deprecated API method")
    }
}