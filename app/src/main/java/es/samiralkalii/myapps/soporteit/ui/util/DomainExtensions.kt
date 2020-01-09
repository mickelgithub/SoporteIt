package es.samiralkalii.myapps.soporteit.ui.util

import android.os.Bundle
import es.samiralkalii.myapps.domain.User



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
const val KEY_BOSS_VERIFICATION= "bossVerification";
const val KEY_TEAM_CREATED= "teamCreated"
const val KEY_TEAM= "team"


fun User.toBundle()= Bundle().apply {
    putString(KEY_EMAIL, email)
    putString(KEY_NAME, name)
    putString(KEY_ID, id)
    putString(KEY_LOCAL_PROFILE_IMAGE, localProfileImage)
    putString(KEY_REMOTE_PROFILE_IMAGE, remoteProfileImage)
    putLong(KEY_CREATION_DATE, creationDate)
    putBoolean(KEY_EMAIL_VERIFIED, emailVerified)
    putString(KEY_PROFILE, profile)
    putString(KEY_BOSS_VERIFICATION, bossVerification)
    putString(KEY_TEAM, team)
}

fun Bundle.toUser()= User(
    email= getString(KEY_EMAIL, ""),
    name = getString(KEY_NAME, ""),
    id= getString(KEY_ID, ""),
    localProfileImage = getString(KEY_LOCAL_PROFILE_IMAGE, ""),
    remoteProfileImage = getString(KEY_REMOTE_PROFILE_IMAGE, ""),
    creationDate = getLong(KEY_CREATION_DATE, 0L),
    emailVerified = getBoolean(KEY_EMAIL_VERIFIED, false),
    profile = getString(KEY_PROFILE, ""),
    bossVerification = getString(KEY_BOSS_VERIFICATION, ""),
    team = getString(KEY_TEAM, "")
)

val User.teamCreated
    get() = this.team.isNotBlank()
