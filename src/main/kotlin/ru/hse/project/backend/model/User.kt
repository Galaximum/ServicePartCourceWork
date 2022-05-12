package ru.hse.project.backend.model

import org.hibernate.Hibernate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.hse.project.backend.domain.response.UserResponse
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.Table

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
    var passwordOwn: String? = null,
    @Column(unique = true)
    var email: String? = null,
    var urlImage: String? = null,
    @Column(unique = true)
    var googleId: String? = null
) : UserDetails {

    @ManyToMany
    val favoriteTrashCans: MutableSet<TrashCan> = mutableSetOf()

    @ManyToMany
    val visitedTrashCans: MutableSet<TrashCan> = mutableSetOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var tokens: MutableSet<Token> = mutableSetOf()

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

    override fun getAuthorities() = mutableListOf<GrantedAuthority>()

    override fun getPassword() = passwordOwn

    override fun getUsername() = nickName

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    fun toUserResponse() = UserResponse(
        nickName,
        firstName,
        secondName,
        email,
        urlImage
    )
}