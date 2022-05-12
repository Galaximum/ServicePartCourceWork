package ru.hse.project.backend.model

import org.hibernate.Hibernate
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.validation.constraints.NotNull

@Entity
data class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_id_seq")
    @SequenceGenerator(name = "token_id_seq", allocationSize = 1, initialValue = 1)
    @Column(nullable = false)
    val id: Long = 0,
    @NotNull
    @Column(unique = true)
    val refreshToken: String,
    val expiryDate: Instant,
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    val user: User
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Token

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    @Override
    override fun toString(): String {
        return "${this::class.simpleName}(id = $id, expiryDate = $expiryDate, user = $user)"
    }
}
