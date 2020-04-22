package es.samiralkalii.myapps.domain.teammanagement

data class Profiles(val profiles: List<Profile>) {
}

data class Profile(val name: String, val level: Int)