package ru.hse.project.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.domain.request.RegisterUserRequest
import ru.hse.project.backend.domain.request.SignInUserRequest
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.*
import ru.hse.project.backend.service.UserService

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
class UserController @Autowired constructor(private val userService: UserService) {

    @PostMapping("/registerUser")
    fun registerUser(@RequestBody request: RegisterUserRequest) {
        throw UserException("Deprecated API method")
    }

    @PostMapping("/signInUser")
    fun signInUser(@RequestBody request: SignInUserRequest) {
        throw UserException("Deprecated API method")
    }

    @PatchMapping("/updateNickName")
    fun updateNickName(@RequestParam nickName: String, @AuthenticationPrincipal user: User) {
        user.nickName = nickName
        userService.save(user)
    }

    @PatchMapping("/updateEmail")
    fun updateEmail(@RequestParam email: String, @AuthenticationPrincipal user: User) {
        user.nickName = email
        userService.save(user)
    }

    @PatchMapping("/updatePassword")
    fun updatePassword(@RequestParam password: String, @AuthenticationPrincipal user: User) {
        user.nickName = password
        userService.save(user)
    }

    @DeleteMapping("/deleteUser")
    fun deleteUser(@AuthenticationPrincipal user: User) = userService.deleteById(user.id)

    @PostMapping("/usersRating")
    fun getRatingUsers(@RequestBody request: GetRatingRequest, @AuthenticationPrincipal user: User) = userService.getRatingUsers(request)

    @GetMapping("/userRating")
    fun getScoreUser(@AuthenticationPrincipal user: User) = userService.getUserScore(user.id)

    @GetMapping("/userInfo")
    fun getUserInfo(@AuthenticationPrincipal user: User) = userService.getUserOrElseThrow(user.id).toUserResponse()

    @PostMapping("/updateUserRating")
    fun increaseScore(@RequestParam trashCanId: Long, @AuthenticationPrincipal user: User) = userService.increaseScore(user.id, trashCanId)

    @GetMapping("/updateRating")
    fun updateRating(): ResponseEntity<Void> {
        throw UserException("Deprecated API method")
    }

    @GetMapping("/clearVisitedTable")
    fun clearVisitedTable() {
        throw UserException("Deprecated API method")
    }
}