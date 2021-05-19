package ru.hse.project.backend.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import ru.hse.project.backend.model.*
import ru.hse.project.backend.service.UserService

@RestController
@RequestMapping(value = ["/users"])
class UserController @Autowired constructor(private val userService: UserService) {

    @PostMapping(value = ["registerUser"])
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<String> {
        val result = userService.registerUser(request)
        when {
            result.isSuccess -> {
                return ResponseEntity(result.success!!, HttpStatus.OK)
            }
            result.error!!.message.equals("Email or nickname is not available") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @PostMapping(value = ["signInUser"], produces = ["application/json"])
    fun signInUser(@RequestBody request: SignInUserRequest): ResponseEntity<User> {
        val result = userService.signInUser(request)
        when {
            result.isSuccess -> {
                return ResponseEntity(result.success!!, HttpStatus.OK)
            }
            result.error!!.message.equals("Wrong email or password") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @PatchMapping(value = ["updateNickName"])
    fun updateNickName(@RequestBody request: UpdateNickNameRequest): ResponseEntity<Void> {

        val result = userService.updateNickName(request)

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }

            result.error!!.message.equals("NickName don't updated") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @PatchMapping(value = ["updateEmail"])
    fun updateEmail(@RequestBody request: UpdateEmailRequest): ResponseEntity<Void> {

        val result = userService.updateEmail(request)

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }

            result.error!!.message.equals("Email don't updated") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @PatchMapping(value = ["updatePassword"])
    fun updatePassword(@RequestBody request: UpdatePasswordRequest): ResponseEntity<Void> {
        val result = userService.updatePassword(request)

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error?.message)

            }
        }
    }

    @DeleteMapping(value = ["deleteUser"])
    fun deleteUser(@RequestParam("id") id: String): ResponseEntity<Void> {
        val result = userService.deleteUser(id)

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }

            result.error!!.message.equals("User not found") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }

    }

    @PostMapping(value = ["usersRating"], produces = ["application/json"])
    fun getRatingUsers(@RequestBody request: GetRatingRequest): ResponseEntity<List<RatingUsers>> {
        val result = userService.getRatingUsers(request)

        when {
            result.isSuccess -> {
                return ResponseEntity(result.success, HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error?.message)

            }
        }
    }

    @GetMapping(value = ["userRating"], produces = ["application/json"])
    fun getScoreUser(@RequestParam("id") id: String): ResponseEntity<RatingUser> {
        val result = userService.getScoreUser(id)

        when {
            result.isSuccess -> {
                return ResponseEntity(result.success, HttpStatus.OK)
            }

            result.error!!.message.equals("User not found") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @PostMapping(value = ["updateUserRating"])
    fun increaseScore(@RequestBody request: UserAndTrashRequest): ResponseEntity<Void> {
        val result = userService.increaseScore(request)

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }

            result.error!!.message.equals("this place was visited earlier") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)
            }

            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)

            }
        }
    }

    @GetMapping(value = ["updateRating"])
    fun updateRating(): ResponseEntity<Void> {
        val result = userService.updateRating()

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error?.message)

            }
        }
    }

    @GetMapping(value = ["clearVisitedTable"])
    fun clearVisitedTable():ResponseEntity<Void>{
        val result = userService.clearVisitedTable();

        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error?.message)

            }
        }
    }

}