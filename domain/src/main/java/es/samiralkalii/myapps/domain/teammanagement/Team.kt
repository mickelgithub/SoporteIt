package es.samiralkalii.myapps.domain.teammanagement

import java.util.*

data class Team(
            var id: String?= null,
            var name: String,
            var nameInsensitive: String,
            var boss: String,
            var creationDate: Long= Calendar.getInstance().time.time,
            var members: MutableList<String>?= MutableList<String>(0) {_ -> ""},
            var groups: List<Group>?= emptyList()
            )