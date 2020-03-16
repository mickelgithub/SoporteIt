package es.samiralkalii.myapps.soporteit.framework.sharedpreferences

import android.content.Context
import androidx.core.content.edit
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
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

    override suspend fun updateTeamCreated(team: Team, teamInvitationState: Reply) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_TEAM, team.name)
            putString(KEY_TEAM_ID, team.id)
            putString(KEY_BOSS, team.boss)
            putString(KEY_TEAM_INVITATION_STATE, teamInvitationState.toString())
        }
    }

    override suspend fun updateTeamInvitationState(teamInvitationState: Reply) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_TEAM_INVITATION_STATE, teamInvitationState.toString())
        }
    }

    override suspend fun updateHolidayDaysAndInternalState(holidayDays: Long, internal: Boolean) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putLong(KEY_HOLIDAY_DAYS_PER_YEAR, holidayDays)
            putBoolean(KEY_INTERNAL_EMPLOYEE, internal)
        }
    }

    override suspend fun denyInvitationToTeam(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_TEAM, "")
            putString(KEY_TEAM_ID, "")
            putString(KEY_BOSS, "")
            //putString(KEY_TEAM_INVITATION_STATE, user.teamInvitationState.toString())
        }
    }


    override suspend fun updateMessagingToken(token: String) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_MESSAGING_TOKEN, token)
        }
    }

    override suspend fun updateImageProfile(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_LOCAL_PROFILE_IMAGE, user.profileImage)
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
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASS, user.password)
            putString(KEY_LOCAL_PROFILE_IMAGE, user.profileImage)
            putString(KEY_REMOTE_PROFILE_IMAGE, user.remoteProfileImage)
            putLong(KEY_CREATION_DATE, user.createdAt)
            putBoolean(KEY_EMAIL_VERIFIED, user.isEmailVerified)
            putString(KEY_PROFILE, user.profile)
            //putString(KEY_BOSS_VERIFICATION, user.bossVerified)
            //putString(KEY_TEAM, user.team)
            //putString(KEY_TEAM_ID, user.teamId)
            //putString(KEY_TEAM_INVITATION_STATE, user.teamInvitationState.toString())
            putLong(KEY_HOLIDAY_DAYS_PER_YEAR, user.holidayDaysPerYear)
            putBoolean(KEY_INTERNAL_EMPLOYEE, user.internalEmployee)
            //putString(KEY_BOSS, user.isBoss)
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
            val bossVerification= getBoolean(KEY_BOSS_VERIFICATION, false) ?: false
            val team= getString(KEY_TEAM, "") ?: ""
            val teamId= getString(KEY_TEAM_ID, "") ?: ""
            val teamInvitationState= Reply.valueOf(getString(KEY_TEAM_INVITATION_STATE, Reply.NONE.toString()) ?: Reply.NONE.toString())
            val holidayDays= getLong(KEY_HOLIDAY_DAYS_PER_YEAR, 22L)
            val internalEmployee= getBoolean(KEY_INTERNAL_EMPLOYEE, false)
            val messaginToken= getString(KEY_MESSAGING_TOKEN, "") ?: ""
            val boss= getBoolean(KEY_BOSS, false) ?: false

            return User(email, pass, name, profileImage = imageProfilePath,
                id= id, remoteProfileImage = imageProfileUrl, createdAt = creationDate,
                isEmailVerified = emailValidated, profile = profile, bossVerified = bossVerification,
                holidayDaysPerYear = holidayDays, internalEmployee = internalEmployee,
                messagingToken = messaginToken, isBoss = boss)
        }
    }
}