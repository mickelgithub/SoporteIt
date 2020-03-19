package es.samiralkalii.myapps.soporteit.ui.util

import android.os.Bundle
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply


const val KEY_EMAIL= "email"
const val KEY_NAME= "name"
const val KEY_ID= "id"
const val KEY_PASS= "password"
const val KEY_LOCAL_PROFILE_IMAGE= "localProfileImage"
const val KEY_REMOTE_PROFILE_IMAGE= "remoteProfileImage"
const val KEY_CREATION_DATE= "creationDate"
const val KEY_EMAIL_VERIFIED= "emailVerified"
const val KEY_MESSAGING_TOKEN= "messagingToken"
const val KEY_PROFILE= "profile"
const val KEY_BOSS_VERIFICATION= "bossVerification"
const val KEY_TEAM= "team"
const val KEY_TEAM_ID= "teamId"
const val KEY_HOLIDAY_DAYS_PER_YEAR= "holidayDaysPerYear"
const val KEY_INTERNAL_EMPLOYEE= "internalEmployee"
const val KEY_TEAM_INVITATION_STATE= "teamInvitationState"
const val KEY_BOSS= "boss"

const val KEY_TEAM_NAME_INSENSITIVE= "nameInsensitive"


fun User.toBundle()= Bundle().apply {
    putString(KEY_EMAIL, email)
    putString(KEY_NAME, name)
    putString(KEY_ID, id)
    putString(KEY_LOCAL_PROFILE_IMAGE, profileImage)
    putString(KEY_REMOTE_PROFILE_IMAGE, remoteProfileImage)
    putString(KEY_CREATION_DATE, createdAt)
    putBoolean(KEY_EMAIL_VERIFIED, isEmailVerified)
    putString(KEY_PROFILE, profile)
    //putString(KEY_BOSS_VERIFICATION, bossVerified)
    //putString(KEY_TEAM, team)
    //putString(KEY_TEAM_ID, teamId)
    //putString(KEY_BOSS, isBoss)
    //putString(KEY_TEAM_INVITATION_STATE, teamInvitationState.toString())
    putInt(KEY_HOLIDAY_DAYS_PER_YEAR, holidayDays)
    putBoolean(KEY_INTERNAL_EMPLOYEE, internalEmployee)
    putString(KEY_MESSAGING_TOKEN, messagingToken)
}

fun Bundle.toUser()= User(
    email= getString(KEY_EMAIL, ""),
    name = getString(KEY_NAME, ""),
    id= getString(KEY_ID, ""),
    profileImage = getString(KEY_LOCAL_PROFILE_IMAGE, ""),
    remoteProfileImage = getString(KEY_REMOTE_PROFILE_IMAGE, ""),
    createdAt = getString(KEY_CREATION_DATE, ""),
    isEmailVerified = getBoolean(KEY_EMAIL_VERIFIED, false),
    profile = getString(KEY_PROFILE, ""),
    //bossVerified = getString(KEY_BOSS_VERIFICATION, ""),
    //team = getString(KEY_TEAM, ""),
    //teamId = getString(KEY_TEAM_ID, ""),
    //isBoss = getString(KEY_BOSS, ""),
    //teamInvitationState = Reply.valueOf(getString(KEY_TEAM_INVITATION_STATE, Reply.NONE.toString())),
    holidayDays = getInt(KEY_HOLIDAY_DAYS_PER_YEAR, User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS),
    internalEmployee = getBoolean(KEY_INTERNAL_EMPLOYEE, false),
    messagingToken = getString(KEY_MESSAGING_TOKEN, "")

)

val User.teamCreated
    get() = true//this.team.isNotBlank()
