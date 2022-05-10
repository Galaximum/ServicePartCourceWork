package ru.hse.project.backend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.TrashCan

@Repository
interface TrashRepository : JpaRepository<TrashCan, Long>