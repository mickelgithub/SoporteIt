package es.samiralkalii.myapps.domain.teammanagement

import java.util.*

data class Group(
    var id: String,
    var name: String,
    var creationDate: Long= Calendar.getInstance().time.time,
    var members: List<String>?= emptyList()
)