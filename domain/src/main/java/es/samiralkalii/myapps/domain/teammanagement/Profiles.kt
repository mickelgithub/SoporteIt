package es.samiralkalii.myapps.domain.teammanagement

data class Profiles(val profiles: List<Profile>) {

    val isEmpty: Boolean
        get() = profiles.isEmpty()

    fun getProfileId(profile: String)= profiles.filter { it.name== profile }.first().id
}

data class Profile(val id: String= "", val name: String= "", val level: Int= -1)