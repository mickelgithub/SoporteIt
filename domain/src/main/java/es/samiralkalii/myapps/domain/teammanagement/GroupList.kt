package es.samiralkalii.myapps.domain.teammanagement

data class GroupList(var groups: MutableList<Group>) {

    val isEmpty
        get() = groups.isEmpty()




}