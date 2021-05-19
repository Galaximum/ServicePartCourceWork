package ru.hse.project.backend.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@JsonAutoDetect
@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
data class TrashCan (
    @Id
    @GeneratedValue
    var id: String? = null,
    var address:String?=null,
    var title:String?=null,
    var latitude:Double?=null,
    var longitude:Double?=null,
    var image:String?=null,
    var paper:Boolean?=null,
    var glass:Boolean?=null,
    var plastic:Boolean?=null,
    var metal:Boolean?=null,
    )