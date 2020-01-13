package es.samiralkalii.myapps.soporteit.framework.sharedpreferences

import android.content.Context
import androidx.core.content.edit
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.*


class SharedPreferencesManager(val context: Context): IPreferences {

    override suspend fun getMessagingToken(): String=
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).getString(KEY_MESSAGING_TOKEN, "") ?: ""

    override suspend fun updateProfile(profile: String) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_PROFILE, profile)
        }
    }

    override suspend fun updateBossVerification(bossVerification: String) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_BOSS_VERIFICATION, bossVerification)
        }
    }

    override suspend fun updateTeamCreated(team: Team) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_TEAM, team.name)
            putString(KEY_TEAM_ID, team.id)
        }
    }


    override suspend fun updateMessagingToken(token: String) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_MESSAGING_TOKEN, token)
        }
    }

    override suspend fun updateImageProfile(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_LOCAL_PROFILE_IMAGE, user.localProfileImage)
            putString(KEY_REMOTE_PROFILE_IMAGE, user.remoteProfileImage)
        }
    }

    override suspend fun updateEmailVerified() {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_EMAIL_VERIFIED, true)
        }
    }

    override suspend fun saveUser(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_ID, user.id)
            putString(KEY_NAME, user.name);
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASS, user.password)
            putString(KEY_LOCAL_PROFILE_IMAGE, user.localProfileImage)
            putString(KEY_REMOTE_PROFILE_IMAGE, user.remoteProfileImage)
            putLong(KEY_CREATION_DATE, user.creationDate)
            putBoolean(KEY_EMAIL_VERIFIED, user.emailVerified)
            putString(KEY_PROFILE, user.profile)
            putString(KEY_BOSS_VERIFICATION, user.bossVerification)
            putString(KEY_TEAM, user.team)
            putString(KEY_TEAM_ID, user.teamId)
        }
    }

    override suspend fun getUser(): User= context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).run {
        val email= getString(KEY_EMAIL, "") ?: ""
        val pass= getString(KEY_PASS, "") ?: ""

        if (email.isBlank() || pass.isBlank()) {
            return User.EMPTY
        } else {
            val id= getString(KEY_ID, "") ?: ""
            val name= getString(KEY_NAME, "") ?: ""
            val imageProfilePath= getString(KEY_LOCAL_PROFILE_IMAGE, "") ?: ""
            val imageProfileUrl= getString(KEY_REMOTE_PROFILE_IMAGE, "") ?: ""
            val creationDate= getLong(KEY_CREATION_DATE, 0L)
            val emailValidated= getBoolean(KEY_EMAIL_VERIFIED, false)
            val profile= getString(KEY_PROFILE, "") ?: ""
            val bossVerification= getString(KEY_BOSS_VERIFICATION, "") ?: ""
            val team= getString(KEY_TEAM, "") ?: ""
            val teamId= getString(KEY_TEAM_ID, "") ?: ""

            return User(email, pass, name, localProfileImage = imageProfilePath,
                id= id, remoteProfileImage = imageProfileUrl, creationDate = creationDate, emailVerified = emailValidated,
                profile = profile, bossVerification = bossVerification, team = team, teamId = teamId)
        }
    }
}