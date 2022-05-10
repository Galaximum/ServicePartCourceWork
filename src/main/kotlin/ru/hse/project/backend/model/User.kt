package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.Table

//@JsonAutoDetect
//@Entity
//@JsonIgnoreProperties("hibernateLazyInitializer")
//data class User(
//    @Id
//    @GeneratedValue
//    var id: String? = null,
//    @Column(unique = true)
//    var nickName: String? = null,
//    var firstName: String? = null,
//    var secondName: String? = null,
//    var password: String? = null,
//    @Column(unique = true)
//    var email: String? = null,
//    var urlImage: String? = null
//)

@Entity
@Table(name = "trash_user")
data class User(
    @Id
    @GeneratedValue
    var id: Long = 0,
    @Column(unique = true)
    var nickName: String? = null,
    var firstName: String? = null,
    var secondName: String? = null,
    var password: String? = null,
    @Column(unique = true)
    var email: String? = null,
    var urlImage: String? = null
) {

    @ManyToMany
    val favoriteTrashCans: MutableSet<TrashCan> = mutableSetOf()

    @ManyToMany
    val visitedTrashCans: MutableSet<TrashCan> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "${this::class.simpleName}(id = $id , nickName=$nickName)"
    }
}