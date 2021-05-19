package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@JsonAutoDetect
@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
data class User(
    @Id
    @GeneratedValue
    var id: String? = null,
    @Column(unique = true)
    var nickName: String? = null,
    var firstName: String? = null,
    var secondName: String? = null,
    var password: String? = null,
    @Column(unique = true)
    var email: String? = null,
    var urlImage: String? = null
)