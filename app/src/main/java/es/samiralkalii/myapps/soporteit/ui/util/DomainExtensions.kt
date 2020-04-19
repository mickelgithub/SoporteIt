package es.samiralkalii.myapps.soporteit.ui.util

import android.os.Bundle
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

fun getFirstName(fullName: String?)= StringTokenizer(fullName, " ").nextToken()

val User.bossVerified: Boolean
    get() = (this.bossConfirmation== SI)




