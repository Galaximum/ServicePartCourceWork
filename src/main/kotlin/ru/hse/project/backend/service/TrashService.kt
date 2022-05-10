package ru.hse.project.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.hse.project.backend.exception.TrashCanException
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.repository.TrashRepository

@Service
class TrashService @Autowired constructor(private val trashRepository: TrashRepository) {

    fun getTrashCanOrElseThrow(trashCanId: Long): TrashCan =
        trashRepository.findById(trashCanId).orElseThrow { TrashCanException("There is no trash can with id: $trashCanId") }

    fun getAllTrashCans(): List<TrashCan> = trashRepository.findAll()

    fun save(can: TrashCan) = trashRepository.save(can)
}