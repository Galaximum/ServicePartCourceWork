package ru.hse.project.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.hse.project.backend.domain.request.AddNewTrashRequest
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.User
import ru.hse.project.backend.service.TrashService
import ru.hse.project.backend.service.UserService

@RestController
@RequestMapping("/trashcans")
@SecurityRequirement(name = "bearerAuth")
class TrashController @Autowired constructor(
    private val trashService: TrashService,
    private val userService: UserService
) {


    @GetMapping("/allTrash")
    fun getAllTrashCans(@AuthenticationPrincipal user: User) = trashService.getAllTrashCans()

    @GetMapping("/favorite")
    fun getFavoriteTrashCans(@AuthenticationPrincipal user: User) = userService.getFavoriteTrashCans(user.id)

    @PostMapping("/addFavorite")
    fun addFavoriteTrashCan(@RequestParam trashCanId: Long, @AuthenticationPrincipal user: User) {
        val can = trashService.getTrashCanOrElseThrow(trashCanId)
        userService.addCanToFavorites(user.id, can)
    }

    @DeleteMapping("/deleteFavorite")
    fun deleteFavoriteTrashCan(@RequestParam trashCanId: Long, @AuthenticationPrincipal user: User) {
        val can = trashService.getTrashCanOrElseThrow(trashCanId)
        userService.removeCanFromFavorites(user.id, can)
    }

    @PostMapping("/addNewTrash")
    fun addNewTrash(@RequestBody request: AddNewTrashRequest, @AuthenticationPrincipal user: User) = trashService.save(
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