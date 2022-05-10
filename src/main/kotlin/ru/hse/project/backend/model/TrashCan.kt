package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


//@JsonAutoDetect
//@Entity
//@JsonIgnoreProperties("hibernateLazyInitializer")
//data class TrashCan (
//    @Id
//    @GeneratedValue
//    var id: String? = null,
//    var address:String?=null,
//    var title:String?=null,
//    var latitude:Double?=null,
//    var longitude:Double?=null,
//    var image:String?=null,
//    var paper:Boolean?=null,
//    var glass:Boolean?=null,
//    var plastic:Boolean?=null,
//    var metal:Boolean?=null,
//    )

@Entity
data class TrashCan(
    @Id
    @GeneratedValue
    val id: Long = 0,
    var address: String? = null,
    var title: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var image: String? = null,
    var paper: Boolean? = null,
    var glass: Boolean? = null,
    var plastic: Boolean? = null,
    var metal: Boolean? = null,
    var verified: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TrashCan

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "${this::class.simpleName}(id = $id , latitude=$latitude, longitude=$longitude, verified=$verified)"
    }
}