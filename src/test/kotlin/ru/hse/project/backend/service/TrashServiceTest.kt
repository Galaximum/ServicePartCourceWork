package ru.hse.project.backend.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import ru.hse.project.backend.BackendApplicationTests
import ru.hse.project.backend.exception.TrashCanException

class TrashServiceTest : BackendApplicationTests() {

    @Test
    fun `Get trash can if exists`() {
        val trashCan = createTrashCan("The best trash can", 0.0, 0.0)

        trashService.getTrashCanOrElseThrow(trashCan.id).apply {
            title shouldBe trashCan.title
            latitude shouldBe trashCan.latitude
            longitude shouldBe trashCan.longitude
        }
    }

    @Test
    fun `Get trash can if not exists`() {
        val exception = shouldThrow<TrashCanException> { trashService.getTrashCanOrElseThrow(1) }
        exception.message shouldBe "There is no trash can with id: 1"
    }

    @Test
    fun `Get all trash cans if there are some`() {
        val trashCan1 = createTrashCan("The best trash can 1", 0.1, 0.0)
        val trashCan2 = createTrashCan("The best trash can 2", 0.0, 0.0)
        val trashCan3 = createTrashCan("The best trash can 3", 0.1, 0.0)

        trashService.getAllTrashCans().apply {
            size shouldBe 3
            any { it.title == trashCan1.title && it.latitude == trashCan1.latitude && it.longitude == trashCan1.longitude } shouldBe true
            any { it.title == trashCan2.title && it.latitude == trashCan2.latitude && it.longitude == trashCan2.longitude } shouldBe true
            any { it.title == trashCan3.title && it.latitude == trashCan3.latitude && it.longitude == trashCan3.longitude } shouldBe true
        }
    }

    @Test
    fun `Get all trash cans if there are none`() {
        trashService.getAllTrashCans().size shouldBe 0
    }
}