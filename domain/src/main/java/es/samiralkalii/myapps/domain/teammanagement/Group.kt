package es.samiralkalii.myapps.domain.teammanagement

import es.samiralkalii.myapps.domain.User

data class Group(
    var id: String,
    var name: String,
    var creationDate: String= "",
    var members: List<User?> = emptyList()
)