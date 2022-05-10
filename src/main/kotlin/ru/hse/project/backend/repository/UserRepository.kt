package ru.hse.project.backend.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.*
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByPasswordAndEmail(password: String, email: String): Optional<User>
}