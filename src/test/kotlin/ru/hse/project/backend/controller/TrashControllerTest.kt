package ru.hse.project.backend.controller

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import ru.hse.project.backend.BackendApplicationTests
import ru.hse.project.backend.domain.request.AddNewTrashRequest
import ru.hse.project.backend.domain.response.ExceptionResponse
import ru.hse.project.backend.model.TrashCan

class TrashControllerTest : BackendApplicationTests() {

    private fun forbiddenResponse(response: ResponseEntity<ExceptionResponse>) {
        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body!!.message shouldBe "Full authentication is required to access this resource"
    }

    @Test
    fun `Get all trash cans`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val trashCan3 = createTrashCan("The best trash can 3", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/allTrash",
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<List<TrashCan>>() {}
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body!!.apply {
            size shouldBe 3
            any { it.title == trashCan1.title && it.latitude == trashCan1.latitude && it.longitude == trashCan1.longitude } shouldBe true
            any { it.title == trashCan2.title && it.latitude == trashCan2.latitude && it.longitude == trashCan2.longitude } shouldBe true
            any { it.title == trashCan3.title && it.latitude == trashCan3.latitude && it.longitude == trashCan3.longitude } shouldBe true
        }
    }

    @Test
    fun `Get all trash cans should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/allTrash",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Get favorite trash cans`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToFavorite(user.id, trashCan)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/favorite",
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<List<TrashCan>>() {}
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body!!.apply {
            size shouldBe 1
            first().apply {
                title shouldBe trashCan.title
                latitude shouldBe trashCan.latitude
                longitude shouldBe trashCan.longitude
            }
        }
    }

    @Test
    fun `Get favorite trash cans should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/favorite",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Add trash can to favorites`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/addFavorite?trashCanId=${trashCan.id}",
            HttpMethod.POST,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getFavoriteTrashCans(user.id).apply {
            size shouldBe 1
            first().apply {
                title shouldBe trashCan.title
                latitude shouldBe trashCan.latitude
                longitude shouldBe trashCan.longitude
            }
        }
    }

    @Test
    fun `Add trash can to favorites if it's already in favorites`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToFavorite(user.id, trashCan)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/addFavorite?trashCanId=${trashCan.id}",
            HttpMethod.POST,
            HttpEntity<Any>(headers),
            ExceptionResponse::class.java
        )

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body!!.message shouldBe "Trash with id: ${trashCan.id} is already in users favorites"
    }

    @Test
    fun `Add trash can to favorites should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/addFavorite",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Remove trash can from user favorites`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToFavorite(user.id, trashCan)
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/deleteFavorite?trashCanId=${trashCan.id}",
            HttpMethod.DELETE,
            HttpEntity<Any>(headers),
            Void::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        userService.getFavoriteTrashCans(user.id).size shouldBe 0
    }

    @Test
    fun `Remove trash can from user favorites if it's already not in the favorites`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")
        mockAuthentication(user)

        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/deleteFavorite?trashCanId=${trashCan.id}",
            HttpMethod.DELETE,
            HttpEntity<Any>(headers),
            ExceptionResponse::class.java
        )

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body!!.message shouldBe "Trash with id: ${trashCan.id} is not in users favorites"
    }

    @Test
    fun `Delete trash can from favorites should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/deleteFavorite",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }

    @Test
    fun `Add new trash can`() {
        val trashCan = createTrashCan("The best trash can 1", 0.1, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToFavorite(user.id, trashCan)
        mockAuthentication(user)

        val body = AddNewTrashRequest(
            0.1, 2.0, "KILL ME PLS", "MOSCOW",
            paper = true,
            glass = true,
            plastic = true,
            metal = true
        )
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/addNewTrash",
            HttpMethod.POST,
            HttpEntity<Any>(body, headers),
            TrashCan::class.java
        )

        response.statusCode shouldBe HttpStatus.OK
        response.body!!.apply {
            latitude shouldBe body.latitude
            longitude shouldBe body.longitude
            title shouldBe body.title
            address shouldBe body.address
            paper shouldBe body.paper
            glass shouldBe body.glass
            plastic shouldBe body.plastic
            metal shouldBe body.metal
        }
    }

    @Test
    fun `Add new trash can should be protected`() {
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/trashcans/addNewTrash",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            ExceptionResponse::class.java
        )

        forbiddenResponse(response)
    }
}