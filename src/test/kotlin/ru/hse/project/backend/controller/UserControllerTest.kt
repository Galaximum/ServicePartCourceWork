package ru.hse.project.backend.controller

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import ru.hse.project.backend.BackendApplicationTests
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.domain.response.ExceptionResponse
import ru.hse.project.backend.domain.response.RatingUsers
import ru.hse.project.backend.domain.response.UserResponse

class UserControllerTest : BackendApplicationTests() {

    private fun forbiddenResponse(response: ResponseEntity<ExceptionResponse>) {
        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body!!.message shouldBe "Full authentication is required to access this resource"
    }

    @Test
    fun `Edit nick name`() {
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateNickName?nickName=nIcKnAmE",
            HttpMethod.PATCH,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getUserOrElseThrow(user.id).nickName shouldBe "nIcKnAmE"
    }

    @Test
    fun `Edit nick name should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateNickName",
            HttpMethod.PATCH,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Edit email`() {
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateEmail?email=email@email.com",
            HttpMethod.PATCH,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getUserOrElseThrow(user.id).email shouldBe "email@email.com"
    }

    @Test
    fun `Edit email should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateEmail",
            HttpMethod.PATCH,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Edit password`() {
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updatePassword?password=1234",
            HttpMethod.PATCH,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getUserOrElseThrow(user.id).password shouldBe "1234"
    }

    @Test
    fun `Edit password should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updatePassword",
            HttpMethod.PATCH,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Delete user`() {
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/deleteUser",
            HttpMethod.DELETE,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userRepository.findById(user.id).isEmpty shouldBe true
    }

    @Test
    fun `Delete user should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/deleteUser",
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Post users rating`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user1 = createUser("nickname1", "1234567890")
        var user2 = createUser("nickname2", "1234567891")
        user2 = addTrashContainerToVisited(user2.id, trashCan)
        mockAuthentication(user1)

        val body = GetRatingRequest(0, 1)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/usersRating",
            HttpMethod.POST,
            HttpEntity<Any>(body, headers),
            object : ParameterizedTypeReference<List<RatingUsers>>() {}
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body!!.apply {
            size shouldBe 2
            any { it.nickName == user1.nickName && it.score == 0 && it.position == 1 } shouldBe true
            any { it.nickName == user2.nickName && it.score == 1 && it.position == 0 } shouldBe true
        }
    }

    @Test
    fun `Post users rating with shift`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val user1 = createUser("nickname1", "1234567890")
        var user2 = createUser("nickname2", "1234567891")
        val user3 = createUser("nickname3", "1234567892")
        user2 = addTrashContainerToVisited(user2.id, trashCan1)
        addTrashContainerToVisited(user3.id, trashCan1)
        addTrashContainerToVisited(user3.id, trashCan2)
        mockAuthentication(user1)

        val body = GetRatingRequest(1, 2)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/usersRating",
            HttpMethod.POST,
            HttpEntity<Any>(body, headers),
            object : ParameterizedTypeReference<List<RatingUsers>>() {}
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body!!.apply {
            size shouldBe 2
            any { it.nickName == user1.nickName && it.score == 0 && it.position == 2 } shouldBe true
            any { it.nickName == user2.nickName && it.score == 1 && it.position == 1 } shouldBe true
        }
    }

    @Test
    fun `Post users rating should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/usersRating",
            HttpMethod.POST,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Get users rating`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToVisited(user.id, trashCan1)
        user = addTrashContainerToVisited(user.id, trashCan2)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/userRating",
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            Int::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe 2
    }

    @Test
    fun `Get users rating should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/userRating",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Get users info`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToVisited(user.id, trashCan1)
        user = addTrashContainerToVisited(user.id, trashCan2)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/userInfo",
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            UserResponse::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe user.toUserResponse()
    }

    @Test
    fun `Get users info should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/userInfo",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Update users rating`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val trashCan3 = createTrashCan("The best trash can 3", 0.6, 5.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToVisited(user.id, trashCan1)
        user = addTrashContainerToVisited(user.id, trashCan2)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateUserRating?trashCanId=${trashCan3.id}",
            HttpMethod.POST,
            HttpEntity<Any>(headers),
            UserResponse::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getUserScore(user.id) shouldBe 3
    }

    @Test
    fun `Update users rating twice with the same trash can`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToVisited(user.id, trashCan1)
        user = addTrashContainerToVisited(user.id, trashCan2)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateUserRating?trashCanId=${trashCan2.id}",
            HttpMethod.POST,
            HttpEntity<Any>(headers),
            ExceptionResponse::class.java
        )

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body!!.message shouldBe "Trash can with id: ${trashCan2.id} was already visited by user with id: ${user.id}"
    }

    @Test
    fun `Update users rating should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/updateUserRating",
            HttpMethod.POST,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }
}