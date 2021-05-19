package ru.hse.project.backend.repository

import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.AddNewTrashRequest
import ru.hse.project.backend.model.TResult
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.UserAndTrashRequest

@Repository
interface TrashRepository {
    fun getAllTrashCans(): TResult<List<TrashCan>>

    fun getFavoriteTrashCans(userId: String): TResult<List<TrashCan>>

    fun addFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void>

    fun deleteFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void>

    fun addNewTrash(request: AddNewTrashRequest):TResult<Void>
}