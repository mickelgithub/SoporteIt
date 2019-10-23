package es.samiralkalii.myapps.soporteit.ui.util

import android.os.Bundle
import es.samiralkalii.myapps.domain.User


const val EMAIL_KEY= "email"
const val NAME_KEY= "name"
const val ID_KEY= "id"
const val LOCAL_PROFILE_IMAGE_KEY= "local_profile_image"
const val REMOTE_PROFILE_IMAGE_KEY= "remote_profile_image"
const val CREATION_DATE_KEY= "creation_date"
const val EMAIL_VALIDATED_KEY= "email_validated"


fun User.toBundle()= Bundle().apply {
    putString(EMAIL_KEY, email)
    putString(NAME_KEY, name)
    putString(ID_KEY, id)
    putString(LOCAL_PROFILE_IMAGE_KEY, localProfileImage)
    putString(REMOTE_PROFILE_IMAGE_KEY, remoteProfileImage)
    putLong(CREATION_DATE_KEY, creationDate)
    putBoolean(EMAIL_VALIDATED_KEY, emailValidated)
}

fun Bundle.toUser()= User(
    email= getString(EMAIL_KEY, ""),
    name = getString(NAME_KEY, ""),
    id= getString(ID_KEY, ""),
    localProfileImage = getString(LOCAL_PROFILE_IMAGE_KEY, ""),
    remoteProfileImage = getString(REMOTE_PROFILE_IMAGE_KEY, ""),
    creationDate = getLong(CREATION_DATE_KEY, 0L),
    emailValidated = getBoolean(EMAIL_VALIDATED_KEY, false)
)
