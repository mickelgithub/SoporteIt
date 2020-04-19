package es.samiralkalii.myapps.domain

import java.util.*

const val STATE_SUBSCRIBED= "S"
const val STATE_UNSUBSCRIBED= "U"

data class User(
    val email: String= "",
    val password: String= "",
    val name: String= "",
    val id: String= "",
    val profileImage: String= "",
    val remoteProfileImage: String= "",
    val profileBackColor: Int= Integer.MIN_VALUE,
    val profileTextColor: Int= Integer.MIN_VALUE,
    val createdAt: String= "",
    val isEmailVerified: Boolean= false,
    val profile: String= "",
    val profileId: String= "",
    val bossCategory: String= "",
    val bossCategoryId: String= "",
    val bossLevel: Int= 0,
    val bossConfirmation: String= "",
    val isBoss: Boolean= false,
    val bossVerifiedAt: String= "",
    val messagingToken: String= "",
    val holidayDays: Int= DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS,
    val internalEmployee: Boolean= false,
    val area: String= "",
    val areaId: String= "",
    val department: String= "",
    val departmentId: String= "",
    val stateChangedAt: String= "",
    val membershipConfirmation: String= "",
    val membershipConfirmedAt: String= "",
    val state: String= STATE_SUBSCRIBED) {

    val firstName: String
        get() = if (name.isNullOrBlank()) "" else StringTokenizer(name, " ").nextToken()


    companion object {
        val EMPTY= User()
        val DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS= 22
        //val DEFAULT_HOLIDAY_DAYS_FOR_INTERNALS= 26
    }
}



