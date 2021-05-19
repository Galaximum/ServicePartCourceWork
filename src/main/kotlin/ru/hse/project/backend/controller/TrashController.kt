package ru.hse.project.backend.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import ru.hse.project.backend.model.AddNewTrashRequest
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.UserAndTrashRequest
import ru.hse.project.backend.service.TrashService

@RestController
@RequestMapping(value = ["/trashcans"])
class TrashController @Autowired constructor(private val trashService: TrashService) {


    @GetMapping(value = ["allTrash"], produces = ["application/json"])
    fun getAllTrashCans(): ResponseEntity<List<TrashCan>> {
        val result = trashService.getAllTrashCans()
        when {
            result.isSuccess -> {
                return ResponseEntity(result.success!!, HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error!!.message)
            }
        }
    }

    @GetMapping(value = ["favorite"], produces = ["application/json"])
    fun getFavoriteTrashCans(@RequestParam("id") id: String): ResponseEntity<List<TrashCan>> {
        val result = trashService.getFavoriteTrashCans(id)
        when {
            result.isSuccess -> {
                return ResponseEntity(result.success!!, HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error!!.message)
            }
        }
    }

    @PostMapping(value = ["addFavorite"])
    fun addFavoriteTrashCan(@RequestBody request: UserAndTrashRequest): ResponseEntity<Void> {
        val result = trashService.addFavoriteTrashCan(request)
        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error!!.message)
            }
        }
    }

    @DeleteMapping(value = ["deleteFavorite"])
    fun deleteFavoriteTrashCan(@RequestBody request: UserAndTrashRequest): ResponseEntity<Void> {
        val result = trashService.deleteFavoriteTrashCan(request)
        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error!!.message)
            }
        }
    }

    @PostMapping(value = ["addNewTrash"])
    fun addNewTrash(@RequestBody request: AddNewTrashRequest): ResponseEntity<Void> {
        val result = trashService.addNewTrash(request)
        when {
            result.isSuccess -> {
                return ResponseEntity(HttpStatus.OK)
            }
            result.error!!.message.equals("the request was left earlier.\nIt is currently being considered") -> {
                throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, result.error.message)

            }
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.error.message)
            }
        }
    }


}