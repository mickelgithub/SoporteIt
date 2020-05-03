package es.samiralkalii.myapps.soporteit.ui.util

import android.os.Bundle
import es.samiralkalii.myapps.domain.STATE_SUBSCRIBED
import es.samiralkalii.myapps.domain.User
import java.util.*


const val KEY_EMAIL= "email"
const val KEY_NAME= "name"
const val KEY_ID= "id"
const val KEY_PASS= "password"
const val KEY_PROFILE_IMAGE= "profileImage"
const val KEY_REMOTE_PROFILE_IMAGE= "remoteProfileImage"
const val KEY_CREATED_AT= "createdAt"
const val KEY_IS_EMAIL_VERIFIED= "emailVerified"
const val KEY_MESSAGING_TOKEN= "messagingToken"
const val KEY_PROFILE= "profile"
const val KEY_PROFILE_ID= "profileId"
const val KEY_BOSS_CATEGORY= "bossCategory"
const val KEY_BOSS_CATEGORY_ID= "bossCategoryId"
const val KEY_BOSS_LEVEL= "bossLevel"
const val KEY_AREA= "area"
const val KEY_AREA_ID= "areaId"
const val KEY_DEPARTMENT= "department"
const val KEY_DEPARTMENT_ID= "departmentId"
const val KEY_IS_BOSS= "boss"
const val KEY_BOSS_VERIFIED_AT= "bossVerifiedAt"
const val KEY_TEAM= "team"
const val KEY_TEAM_ID= "teamId"
const val KEY_HOLIDAY_DAYS= "holidayDays"
const val KEY_INTERNAL_EMPLOYEE= "internalEmployee"
const val KEY_TEAM_INVITATION_STATE= "teamInvitationState"
const val KEY_BOSS= "boss"
const val KEY_PROFILE_BACK_COLOR= "profileBackColor"
const val KEY_PROFILE_TEXT_COLOR= "profileTextColor"
const val KEY_STATE= "state"
const val KEY_STATE_CHANGED_AT= "stateChangedAt"
const val KEY_BOSS_CONFIRMATION= "bossConfirmation"
const val KEY_MEMBERSHIP_CONFIRMATION= "membershipConfirmation"
const val KEY_MEMBERSHIP_CONFIRMED_AT= "membershipConfirmedAt"

const val KEY_TEAM_NAME_INSENSITIVE= "nameInsensitive"


fun User.toBundle()= Bundle().apply {
    putString(KEY_EMAIL, email)
    putString(KEY_NAME, name)
    putString(KEY_ID, id)
    putString(KEY_PROFILE_IMAGE, profileImage)
    putString(KEY_REMOTE_PROFILE_IMAGE, remoteProfileImage)
    putString(KEY_CREATED_AT, createdAt)
    putBoolean(KEY_IS_EMAIL_VERIFIED, isEmailVerified)
    putString(KEY_PROFILE, profile)
    //putString(KEY_BOSS_VERIFICATION, bossVerified)
    //putString(KEY_TEAM, team)
    //putString(KEY_TEAM_ID, teamId)
    //putString(KEY_BOSS, isBoss)
    //putString(KEY_TEAM_INVITATION_STATE, teamInvitationState.toString())
    putInt(KEY_HOLIDAY_DAYS, holidayDays)
    putBoolean(KEY_INTERNAL_EMPLOYEE, internalEmployee)
    putString(KEY_MESSAGING_TOKEN, messagingToken)
}

fun Bundle.toUser()= User(
    email= getString(KEY_EMAIL, ""),
    name = getString(KEY_NAME, ""),
    id= getString(KEY_ID, ""),
    profileImage = getString(KEY_PROFILE_IMAGE, ""),
    remoteProfileImage = getString(KEY_REMOTE_PROFILE_IMAGE, ""),
    createdAt = getString(KEY_CREATED_AT, ""),
    isEmailVerified = getBoolean(KEY_IS_EMAIL_VERIFIED, false),
    profile = getString(KEY_PROFILE, ""),
    //bossVerified = getString(KEY_BOSS_VERIFICATION, ""),
    //team = getString(KEY_TEAM, ""),
    //teamId = getString(KEY_TEAM_ID, ""),
    //isBoss = getString(KEY_BOSS, ""),
    //teamInvitationState = Reply.valueOf(getString(KEY_TEAM_INVITATION_STATE, Reply.NONE.toString())),
    holidayDays = getInt(KEY_HOLIDAY_DAYS, User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS),
    internalEmployee = getBoolean(KEY_INTERNAL_EMPLOYEE, false),
    messagingToken = getString(KEY_MESSAGING_TOKEN, "")

)

fun Map<String, Any>.toUser()= User(
    id = this[KEY_ID] as String? ?: "",
    email = this[KEY_EMAIL] as String? ?: "",
    password = this[KEY_PASS] as String? ?: "",
    name= this[KEY_NAME] as String? ?: "",
    profileImage = this[KEY_PROFILE_IMAGE] as String? ?: "",
    remoteProfileImage = this[KEY_REMOTE_PROFILE_IMAGE] as String? ?: "",
    profileBackColor = (this[KEY_PROFILE_BACK_COLOR] as Long? ?: Integer.MIN_VALUE.toLong()).toInt(),
    profileTextColor= (this[KEY_PROFILE_TEXT_COLOR] as Long? ?: Integer.MIN_VALUE.toLong()).toInt(),
    createdAt = this[KEY_CREATED_AT] as String? ?: "",
    isEmailVerified= this[KEY_IS_EMAIL_VERIFIED] as Boolean? ?: false,
    profile = this[KEY_PROFILE] as String? ?: "",
    profileId = this[KEY_PROFILE_ID] as String? ?: "",
    bossCategory = this[KEY_BOSS_CATEGORY] as String? ?: "",
    bossCategoryId = this[KEY_BOSS_CATEGORY_ID] as String? ?: "",
    bossLevel = (this[KEY_BOSS_LEVEL] as Long? ?: 0L).toInt(),
    bossConfirmation = this[KEY_BOSS_CONFIRMATION] as String? ?: "",
    isBoss = this[KEY_BOSS] as Boolean? ?: false,
    bossVerifiedAt = this[KEY_BOSS_VERIFIED_AT] as String? ?: "",
    holidayDays = (this[KEY_HOLIDAY_DAYS] as Long? ?: User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS.toLong()).toInt(),
    internalEmployee = this[KEY_INTERNAL_EMPLOYEE] as Boolean? ?: false,
    area= this[KEY_AREA] as String? ?: "",
    areaId = this[KEY_AREA_ID] as String? ?: "",
    department = this[KEY_DEPARTMENT] as String? ?: "",
    departmentId = this[KEY_DEPARTMENT_ID] as String? ?: "",
    stateChangedAt = this[KEY_STATE_CHANGED_AT] as String? ?: "",
    state = this[KEY_STATE] as String? ?: STATE_SUBSCRIBED,
    membershipConfirmation = this[KEY_MEMBERSHIP_CONFIRMATION] as String? ?: "",
    membershipConfirmedAt = this[KEY_MEMBERSHIP_CONFIRMED_AT] as String? ?: ""
)

fun getFirstName(fullName: String?)= StringTokenizer(fullName, " ").nextToken()

val User.bossVerified: Boolean
    get() = (this.bossConfirmation== SI)




