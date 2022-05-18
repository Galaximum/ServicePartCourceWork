package ru.hse.project.backend.service

import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.project.backend.domain.request.GetRatingRequest
import ru.hse.project.backend.domain.response.RatingUsers
import ru.hse.project.backend.exception.UserException
import ru.hse.project.backend.model.*
import ru.hse.project.backend.repository.UserRepository

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val trashService: TrashService
) {
    @Transactional
    fun getUserOrElseThrow(userId: Long, initFavoriteTrashCans: Boolean = false, initVisitedTrashCans: Boolean = false): User =
        userRepository.findById(userId).orElseThrow { UserException("There is no user with id: $userId") }.apply {
            if (initFavoriteTrashCans) {
                Hibernate.initialize(favoriteTrashCans)
            }
            if (initVisitedTrashCans) {
                Hibernate.initialize(visitedTrashCans)
            }
        }

    @Transactional
    fun getFavoriteTrashCans(userId: Long) =
        getUserOrElseThrow(userId, true).favoriteTrashCans

    @Transactional
    fun addCanToFavorites(userId: Long, can: TrashCan) {
        val user = getUserOrElseThrow(userId, true)
        if (!user.favoriteTrashCans.add(can)) {
            throw UserException("Trash with id: ${can.id} is already in users favorites")
        }
        userRepository.save(user)
    }

    @Transactional
    fun removeCanFromFavorites(userId: Long, can: TrashCan) {
        val user = getUserOrElseThrow(userId, true)
        if (!user.favoriteTrashCans.remove(can)) {
            throw UserException("Trash with id: ${can.id} is not in users favorites")
        }
        userRepository.save(user)
    }

    fun save(user: User) = try {
        userRepository.save(user)
    } catch (e: DataIntegrityViolationException) {
        throw UserException(e.message ?: e.localizedMessage)
    }

    fun deleteById(id: Long) = userRepository.deleteById(id)

    @Transactional
    fun getRatingUsers(request: GetRatingRequest) =
        userRepository.findAll().sortedByDescending { it.visitedTrashCans.size }.take(request.endPosition + 1)
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

    @Transactional
    fun increaseScore(userId: Long, trashCanId: Long) {
        val can = trashService.getTrashCanOrElseThrow(trashCanId)
        val user = getUserOrElseThrow(userId, initVisitedTrashCans = true)
        if (!user.visitedTrashCans.add(can)) {
            throw UserException("Trash can with id: $trashCanId was already visited by user with id: $userId")
        }
    }

    fun findByGoogleId(id: String) = userRepository.findByGoogleId(id)
}