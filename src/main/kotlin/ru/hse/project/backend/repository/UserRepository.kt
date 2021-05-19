package ru.hse.project.backend.repository

import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.*

@Repository
interface UserRepository {

    fun registerUser(request: RegisterUserRequest): TResult<String>

    fun signInUser(request: SignInUserRequest): TResult<User>

    fun updateNickName(request: UpdateNickNameRequest): TResult<Void>

    fun updateEmail(request: UpdateEmailRequest): TResult<Void>

    fun updatePassword(request: UpdatePasswordRequest): TResult<Void>

    fun deleteUser(id: String): TResult<Void>

    fun getRatingUsers(request: GetRatingRequest):TResult<List<RatingUsers>>

    fun getScoreUser(id:String):TResult<RatingUser>

    fun increaseScore(request: UserAndTrashRequest):TResult<Void>

    fun updateRating():TResult<Void>

    fun clearVisitedTable():TResult<Void>

}