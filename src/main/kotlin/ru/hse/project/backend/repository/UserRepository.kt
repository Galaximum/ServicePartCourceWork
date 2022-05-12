package ru.hse.project.backend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.*
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByPasswordOwnAndEmail(password: String, email: String): Optional<User>

    fun findByGoogleId(id: String): Optional<User>

    fun findByNickName(nickName: String): Optional<User>
}