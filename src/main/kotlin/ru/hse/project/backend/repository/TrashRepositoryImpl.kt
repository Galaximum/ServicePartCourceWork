package ru.hse.project.backend.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.hse.project.backend.model.AddNewTrashRequest
import ru.hse.project.backend.model.TResult
import ru.hse.project.backend.model.TrashCan
import ru.hse.project.backend.model.UserAndTrashRequest
import ru.hse.project.backend.rowmapper.TrashRowMapper

@Repository
class TrashRepositoryImpl @Autowired constructor(private val jdbcTemplate: JdbcTemplate) : TrashRepository {
    override fun getAllTrashCans(): TResult<List<TrashCan>> {
        val trashcans: List<TrashCan> = jdbcTemplate.query(
            "SELECT * FROM trashcan",
            TrashRowMapper()
        )
        return TResult(success = trashcans)
    }

    override fun getFavoriteTrashCans(userId: String): TResult<List<TrashCan>> {
        val trashcans: List<TrashCan> = jdbcTemplate.query(
            "SELECT distinct t1.id_trashcan, t2.id,t2.address,t2.title,t2.latitude,t2.longitude,t2.image,t2.paper,t2.glass,t2.plastic,t2.metal from favorite_trashcan_array t1 inner join trashcan t2 on t1.id_trashcan = t2.id where t1.id_user=?",
            TrashRowMapper(),
            userId
        )

        return TResult(success = trashcans)
    }

    override fun addFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void> {
        val count = jdbcTemplate.update(
            "INSERT IGNORE INTO favorite_trashcan_array (id_user,id_trashcan) VALUES (?, ?)",
            request.id,
            request.trashCanId
        )

        return if(count==0){
            TResult(error = IllegalArgumentException(""))
        }else{
            TResult(success = null)
        }

    }

    override fun deleteFavoriteTrashCan(request: UserAndTrashRequest): TResult<Void> {
        val count = jdbcTemplate.update(
            "DELETE FROM favorite_trashcan_array WHERE id_user=? AND id_trashcan=?",
            request.id,
            request.trashCanId
        )

        return if(count==0){
            TResult(error = IllegalArgumentException("Trashcan for this user not found"))
        }else{
            TResult(success = null)
        }
    }

    override fun addNewTrash(request: AddNewTrashRequest): TResult<Void> {
        val count = jdbcTemplate.update(
            "INSERT IGNORE INTO application_for_new_trashcans (id_user,latitude,longitude,title,address,paper,glass,plastic,metal) VALUES (?, ?,?,?,?,?,?,?,?)",
            request.idUser,
            request.latitude,
            request.longitude,
            request.title,
            request.address,
            request.paper,
            request.glass,
            request.plastic,
            request.metal
        )

        return if(count==0){
            TResult(error = IllegalArgumentException("the request was left earlier.\nIt is currently being considered"))
        }else{
            TResult(success = null)
        }

    }

}