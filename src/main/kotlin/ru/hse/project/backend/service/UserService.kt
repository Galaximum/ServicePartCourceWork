package ru.hse.project.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.domain.request.SignInUserRequest
import ru.hse.project.backend.domain.request.UserAndTrashRequest
import ru.hse.project.backend.domain.response.RatingUsers
import ru.hse.project.backend.domain.response.TResult
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.*
import ru.hse.project.backend.repository.UserRepository

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val trashService: TrashService
) {
    @Transactional
    fun getUserOrElseThrow(userId: Long): User =
        userRepository.findById(userId).orElseThrow { UserException("There is no user with id: $userId") }

    @Transactional
    fun getFavoriteTrashCans(userId: Long) =
        getUserOrElseThrow(userId).favoriteTrashCans

    @Transactional
    fun addCanToFavorites(userId: Long, can: TrashCan) {
        val user = getUserOrElseThrow(userId)
        if (!user.favoriteTrashCans.add(can)) {
            throw UserException("Trash with id: ${can.id} is already in users favorites")
        }
        userRepository.save(user)
    }

    @Transactional
    fun removeCanFromFavorites(userId: Long, can: TrashCan) {
        val user = getUserOrElseThrow(userId)
        if (!user.favoriteTrashCans.remove(can)) {
            throw UserException("Trash with id: ${can.id} is not in users favorites")
        }
        userRepository.save(user)
    }

    fun save(user: User) = try {
        userRepository.save(user)
    } catch (e: DuplicateKeyException) {
        throw UserException("Nickname: ${user.nickName} is already used")
    }

    fun signInUser(request: SignInUserRequest): User =
        userRepository.findByPasswordAndEmail(request.password, request.email)
            .orElseThrow { UserException("There is no user with such credentials") }

    fun deleteById(id: Long) = userRepository.deleteById(id)

    @Transactional
    fun getRatingUsers(request: GetRatingRequest) =
        userRepository.findAll().sortedBy { it.visitedTrashCans.size }.take(request.endPosition + 1)
            .takeLast(request.endPosition - request.startPosition + 1).mapIndexed { index, user ->
                RatingUsers(
                    user.nickName,
                    user.urlImage,
                    user.visitedTrashCans.size,
                    index + request.startPosition
                )
            }

    @Transactional
    fun getUserScore(id: Long) = getUserOrElseThrow(id).visitedTrashCans.size

    fun increaseScore(request: UserAndTrashRequest) {
        val can = trashService.getTrashCanOrElseThrow(request.trashCanId)
        val user = getUserOrElseThrow(request.id)
        if (!user.visitedTrashCans.add(can)) {
            throw UserException("Trash can with id: ${request.trashCanId} was already visited by user with id: ${request.id}")
        }
    }
}