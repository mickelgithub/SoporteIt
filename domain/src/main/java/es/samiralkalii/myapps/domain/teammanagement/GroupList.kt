package es.samiralkalii.myapps.domain.teammanagement

data class GroupList(val groups: List<Group>) {

    val isEmpty
        get() = groups.isEmpty()




}