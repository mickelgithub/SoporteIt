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
            putString(KEY_BOSS_VERIFIED, bossVerification)
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

    override suspend fun updateHolidayDaysAndInternalState(holidayDays: Int, internal: Boolean) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putInt(KEY_HOLIDAY_DAYS, holidayDays)
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
            putString(KEY_PROFILE_IMAGE, user.profileImage)
            putString(KEY_REMOTE_PROFILE_IMAGE, user.remoteProfileImage)
        }
    }

    override suspend fun updateEmailVerified() {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_IS_EMAIL_VERIFIED, true)
        }
    }

    override suspend fun saveUser(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(KEY_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASS, user.password)
            putString(KEY_PROFILE_IMAGE, user.profileImage)
            putString(KEY_REMOTE_PROFILE_IMAGE, user.remoteProfileImage)
            putInt(KEY_PROFILE_BACK_COLOR, user.profileBackColor)
            putInt(KEY_PROFILE_TEXT_COLOR, user.profileTextColor)
            putString(KEY_CREATED_AT, user.createdAt)
            putBoolean(KEY_IS_EMAIL_VERIFIED, user.isEmailVerified)
            putString(KEY_PROFILE, user.profile)
            putString(KEY_PROFILE_ID, user.profileId)
            putString(KEY_BOSS_CATEGORY, user.bossCategory)
            putString(KEY_BOSS_CATEGORY_ID, user.bossCategoryId)
            putInt(KEY_BOSS_LEVEL, user.bossLevel)
            putBoolean(KEY_IS_BOSS, user.isBoss)
            putBoolean(KEY_BOSS_VERIFIED, user.bossVerified)
            putInt(KEY_HOLIDAY_DAYS, user.holidayDays)
            putBoolean(KEY_INTERNAL_EMPLOYEE, user.internalEmployee)
            putString(KEY_MESSAGING_TOKEN, user.messagingToken)
            putString(KEY_AREA, user.area)
            putString(KEY_AREA_ID, user.areaId)
            putString(KEY_DEPARTMENT, user.department)
            putString(KEY_DEPARTMENT_ID, user.departmentId)
        }
    }

    override suspend fun getUser(): User= context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).run {
        val email= getString(KEY_EMAIL, "") ?: ""
        val pass= getString(KEY_PASS, "") ?: ""
        if (email.isBlank() || pass.isBlank()) {
            return User.EMPTY
        } else {
            val id= getString(KEY_ID, "")
            val name= getString(KEY_NAME, "")
            val imageProfilePath= getString(KEY_PROFILE_IMAGE, "")
            val imageProfileUrl= getString(KEY_REMOTE_PROFILE_IMAGE, "")
            val profileBackColor= getInt(KEY_PROFILE_BACK_COLOR, -1)
            val profileTextColor= getInt(KEY_PROFILE_TEXT_COLOR, -1)
            val createdAt= getString(KEY_CREATED_AT, "")
            val isEmailVerified= getBoolean(KEY_IS_EMAIL_VERIFIED, false)
            val profile= getString(KEY_PROFILE, "")
            val profileId= getString(KEY_PROFILE_ID, "")
            val isBoss= getBoolean(KEY_BOSS, false)
            val isBossVerified= getBoolean(KEY_BOSS_VERIFIED, false)
            val bossCategory= getString(KEY_BOSS_CATEGORY, "")
            val bossCategoryId= getString(KEY_BOSS_CATEGORY_ID, "")
            val bossLevel= getInt(KEY_BOSS_LEVEL, 0)
            val holidayDays= getInt(KEY_HOLIDAY_DAYS, -1)
            val isInternalEmployee= getBoolean(KEY_INTERNAL_EMPLOYEE, false)
            val messagingToken= getString(KEY_MESSAGING_TOKEN, "")
            val area= getString(KEY_AREA, "")
            val areaId= getString(KEY_AREA_ID, "")
            val department= getString(KEY_DEPARTMENT, "")
            val departmentId= getString(KEY_DEPARTMENT_ID, "")

            return User(email= email, password = pass, id= id, name = name, profileImage = imageProfilePath,
                remoteProfileImage = imageProfileUrl, profileBackColor = profileBackColor,
                profileTextColor = profileTextColor, createdAt = createdAt, isEmailVerified = isEmailVerified,
                profile = profile, profileId = profileId, isBoss = isBoss, bossVerified = isBossVerified,
                bossCategory = bossCategory, bossCategoryId = bossCategoryId, bossLevel = bossLevel,
                holidayDays = holidayDays, internalEmployee = isInternalEmployee, messagingToken = messagingToken,
                area = area, areaId = areaId, department = department, departmentId = departmentId

            )
        }
    }
}