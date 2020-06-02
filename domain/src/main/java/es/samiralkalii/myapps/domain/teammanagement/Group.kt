package es.samiralkalii.myapps.domain.teammanagement

import es.samiralkalii.myapps.domain.User

const val KEY_GROUP_NAME= "name"
const val KEY_GROUP_ID= "id"
const val KEY_GROUP_MAP_ID= "groupId"
const val KEY_GROUP_CREATEDAT= "createdAt"
const val KEY_GROUP_MEMBERS= "members"

data class Group(
    var id: String,
    var name: String,
    var area: String,
    var department: String,
    var members: List<User> = emptyList()
)