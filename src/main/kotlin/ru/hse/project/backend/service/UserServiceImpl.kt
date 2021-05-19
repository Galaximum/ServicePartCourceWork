package ru.hse.project.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.hse.project.backend.model.*
import ru.hse.project.backend.repository.UserRepository

@Service
class UserServiceImpl @Autowired constructor(private val userRepository: UserRepository) : UserService {

    override fun registerUser(request: RegisterUserRequest): TResult<String> {
        return userRepository.registerUser(request)
    }

    override fun signInUser(request: SignInUserRequest): TResult<User> {
        return userRepository.signInUser(request)
    }

    override fun updateNickName(request: UpdateNickNameRequest): TResult<Void> {
        return userRepository.updateNickName(request)
    }

    override fun updateEmail(request: UpdateEmailRequest): TResult<Void> {
        return userRepository.updateEmail(request)
    }

    override fun updatePassword(request: UpdatePasswordRequest): TResult<Void> {
        return userRepository.updatePassword(request)
    }

    override fun deleteUser(id:String): TResult<Void> {
        return userRepository.deleteUser(id)
    }

    override fun getRatingUsers(request: GetRatingRequest): TResult<List<RatingUsers>> {
        return userRepository.getRatingUsers(request)
    }

    override fun getScoreUser(id: String): TResult<RatingUser> {
        return userRepository.getScoreUser(id)
    }

    override fun increaseScore(request: UserAndTrashRequest): TResult<Void> {
        return userRepository.increaseScore(request)
    }

    override fun updateRating(): TResult<Void> {
        return userRepository.updateRating()
    }

    override fun clearVisitedTable(): TResult<Void> {
        return userRepository.clearVisitedTable()
    }

}