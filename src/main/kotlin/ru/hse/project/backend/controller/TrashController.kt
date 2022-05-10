package ru.hse.project.backend.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.hse.project.backend.domain.request.AddNewTrashRequest
import ru.hse.project.backend.domain.request.UserAndTrashRequest
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.service.TrashService
import ru.hse.project.backend.service.UserService

@RestController
@RequestMapping(value = ["/trashcans"])
class TrashController @Autowired constructor(
    private val trashService: TrashService,
    private val userService: UserService
) {


    @GetMapping(value = ["allTrash"], produces = ["application/json"])
    fun getAllTrashCans() = trashService.getAllTrashCans()

    @GetMapping(value = ["favorite"], produces = ["application/json"])
    fun getFavoriteTrashCans(@RequestParam("id") id: Long) = userService.getFavoriteTrashCans(id)

    @PostMapping(value = ["addFavorite"])
    fun addFavoriteTrashCan(@RequestBody request: UserAndTrashRequest) {
        val can = trashService.getTrashCanOrElseThrow(request.trashCanId)
        userService.addCanToFavorites(request.id, can)
    }

    @DeleteMapping(value = ["deleteFavorite"])
    fun deleteFavoriteTrashCan(@RequestBody request: UserAndTrashRequest) {
        val can = trashService.getTrashCanOrElseThrow(request.trashCanId)
        userService.removeCanFromFavorites(request.id, can)
    }

    @PostMapping(value = ["addNewTrash"])
    fun addNewTrash(@RequestBody request: AddNewTrashRequest) = trashService.save(
        TrashCan(
            address = request.address,
            title = request.title,
            latitude = request.latitude,
            longitude = request.longitude,
            paper = request.paper,
            plastic = request.plastic,
            metal = request.metal,
            glass = request.glass
        )
    )
}