package ru.hse.project.backend.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import ru.hse.project.backend.BackendApplicationTests
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.exception.UserException


class UserServiceTest : BackendApplicationTests() {

    @Test
    fun `Get user if exists`() {
        val user = createUser("nickname", "1234567890")

        userService.getUserOrElseThrow(user.id).apply {
            nickName shouldBe user.nickName
            googleId shouldBe user.googleId
        }
    }

    @Test
    fun `Get user if there are no`() {
        val exception = shouldThrow<UserException> { userService.getUserOrElseThrow(1) }
        exception.message shouldBe "There is no user with id: 1"
    }

    @Test
    fun `Get users favorite trash cans if there are some`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val trashCan3 = createTrashCan("The best trash can 3", 0.1, 0.0)
        var user = createUser("nickname", "1234567890")
        user = addTrashContainerToFavorite(user.id, trashCan1)
        user = addTrashContainerToFavorite(user.id, trashCan2)
        user = addTrashContainerToFavorite(user.id, trashCan3)

        userService.getFavoriteTrashCans(user.id).apply {
            size shouldBe 3
            any { it.title == trashCan1.title && it.latitude == trashCan1.latitude && it.longitude == trashCan1.longitude } shouldBe true
            any { it.title == trashCan2.title && it.latitude == trashCan2.latitude && it.longitude == trashCan2.longitude } shouldBe true
            any { it.title == trashCan3.title && it.latitude == trashCan3.latitude && it.longitude == trashCan3.longitude } shouldBe true
        }
    }

    @Test
    fun `Get users favorite trash cans if there are none`() {
        val user = createUser("nickname", "1234567890")

        userService.getFavoriteTrashCans(user.id).size shouldBe 0
    }

    @Test
    fun `Add trash can to user favorites`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")

        userService.addCanToFavorites(user.id, trashCan)

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
    fun `Add trash can to user favorites if it's already in favorites`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")
        addTrashContainerToFavorite(user.id, trashCan)

        val exception = shouldThrow<UserException> { userService.addCanToFavorites(user.id, trashCan) }
        exception.message shouldBe "Trash with id: ${trashCan.id} is already in users favorites"
    }

    @Test
    fun `Remove trash can from user favorites`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")
        addTrashContainerToFavorite(user.id, trashCan)

        userService.removeCanFromFavorites(user.id, trashCan)
        userService.getFavoriteTrashCans(user.id).size shouldBe 0
    }

    @Test
    fun `Remove trash can from user favorites if it's already not in the favorites`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user = createUser("nickname", "1234567890")

        val exception = shouldThrow<UserException> { userService.removeCanFromFavorites(user.id, trashCan) }
        exception.message shouldBe "Trash with id: ${trashCan.id} is not in users favorites"
    }

    @Test
    fun `Get users rating`() {
        val trashCan = createTrashCan("The best trash can", 0.1, 0.0)
        val user1 = createUser("nickname1", "1234567890")
        var user2 = createUser("nickname2", "1234567891")
        user2 = addTrashContainerToVisited(user2.id, trashCan)

        userService.getRatingUsers(GetRatingRequest(0, 1)).apply {
            size shouldBe 2
            any { it.nickName == user1.nickName && it.score == 0 && it.position == 1 } shouldBe true
            any { it.nickName == user2.nickName && it.score == 1 && it.position == 0 } shouldBe true
        }
    }

    @Test
    fun `Get users rating with shift`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val user1 = createUser("nickname1", "1234567890")
        var user2 = createUser("nickname2", "1234567891")
        val user3 = createUser("nickname3", "1234567892")
        user2 = addTrashContainerToVisited(user2.id, trashCan1)
        addTrashContainerToVisited(user3.id, trashCan1)
        addTrashContainerToVisited(user3.id, trashCan2)

        userService.getRatingUsers(GetRatingRequest(1, 2)).apply {
            size shouldBe 2
            any { it.nickName == user1.nickName && it.score == 0 && it.position == 2 } shouldBe true
            any { it.nickName == user2.nickName && it.score == 1 && it.position == 1 } shouldBe true
        }
    }

    @Test
    fun `Get users score`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val user = createUser("nickname1", "1234567890")
        addTrashContainerToVisited(user.id, trashCan1)
        addTrashContainerToVisited(user.id, trashCan2)

        userService.getUserScore(user.id) shouldBe 2
    }

    @Test
    fun `Increase users score`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val trashCan3 = createTrashCan("The best trash can 3", 0.0, 0.3)
        val user = createUser("nickname1", "1234567890")
        addTrashContainerToVisited(user.id, trashCan1)
        addTrashContainerToVisited(user.id, trashCan2)

        userService.increaseScore(user.id, trashCan3.id)
        userService.getUserScore(user.id) shouldBe 3
    }

    @Test
    fun `Increase users score when trash can visited again`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val user = createUser("nickname1", "1234567890")
        addTrashContainerToVisited(user.id, trashCan1)
        addTrashContainerToVisited(user.id, trashCan2)

        val exception = shouldThrow<UserException> { userService.increaseScore(user.id, trashCan2.id) }
        exception.message shouldBe "Trash can with id: ${trashCan2.id} was already visited by user with id: ${user.id}"
    }
}