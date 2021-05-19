package ru.hse.project.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.hse.project.backend.model.AddNewTrashRequest
import ru.hse.project.backend.model.TResult
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.UserAndTrashRequest
import ru.hse.project.backend.repository.TrashRepository

@Service
class TrashServiceImpl @Autowired constructor(private val trashRepository: TrashRepository) : TrashService {

    override fun getAllTrashCans(): TResult<List<TrashCan>> {
        return trashRepository.getAllTrashCans()
    }

    override fun getFavoriteTrashCans(userId: String): TResult<List<TrashCan>> {
        return trashRepository.getFavoriteTrashCans(userId)
    }

    override fun addFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void> {
        return trashRepository.addFavoriteTrashCan(request)
    }

    override fun deleteFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void> {
        return trashRepository.deleteFavoriteTrashCan(request)
    }

    override fun addNewTrash(request: AddNewTrashRequest): TResult<Void> {
        return trashRepository.addNewTrash(request)
    }
}