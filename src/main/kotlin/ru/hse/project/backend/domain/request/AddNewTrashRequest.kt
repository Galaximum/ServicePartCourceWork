package ru.hse.project.backend.domain.request

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect
data class AddNewTrashRequest(
    var idUser: String,
    var latitude:Double,
    var longitude:Double,
    var title: String,
    var address: String,
    var paper: Boolean,
    var glass: Boolean,
    var plastic: Boolean,
    var metal: Boolean
)
