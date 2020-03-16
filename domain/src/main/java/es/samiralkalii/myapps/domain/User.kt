package es.samiralkalii.myapps.domain

import es.samiralkalii.myapps.domain.notification.Reply

data class User(
    val email: String= "",
    val password: String= "",
    val name: String= "",
    val id: String= "",
    val profileImage: String= "",
    val remoteProfileImage: String= "",
    val createdAt: Long= 0,
    val isEmailVerified: Boolean= false,
    val profile: String= "",
    val bossVerified: Boolean= false,
    val bossLevel: BossLevel= BossLevel.PROJECT_MANAGER,
    val isBoss: Boolean= false,
    val messagingToken: String= "",
    val holidayDaysPerYear: Long= DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS,
    val internalEmployee: Boolean= true
                ) {

    companion object {
        val EMPTY= User()
        val DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS= 22L
        val DEFAULT_HOLIDAY_DAYS_FOR_INTERNALS= 26L
    }

    enum class BossLevel {
        PROJECT_MANAGER,
        PROJECT_MANAGER_BOSS,
        IT_DIRECTOR
    }



}



