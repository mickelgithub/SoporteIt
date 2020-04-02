package es.samiralkalii.myapps.preference

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team

class PreferenceRepository(val preferences: IPreferences) {

    suspend fun getUser()= preferences.getUser()
    suspend fun saveUser(user: User)= preferences.saveUser(user)
    suspend fun updateEmailVerified()= preferences.updateEmailVerified()
    suspend fun updateImageProfile(user: User)= preferences.updateImageProfile(user)
    suspend fun updateMessagingToken(token: String)= preferences.updateMessagingToken(token)
    suspend fun getMessagingToken()= preferences.getMessagingToken()
    suspend fun updateProfile(profile: String)= preferences.updateProfile(profile)
    suspend fun updateBossVerification(bossVerifiedAt: String)= preferences.updateBossVerification(bossVerifiedAt)
    suspend fun updateTeamCreated(team: Team, teamInvitationState: Reply)= preferences.updateTeamCreated(team, teamInvitationState)
    suspend fun updateTeamInvitationState(teamInvitationState: Reply)= preferences.updateTeamInvitationState(teamInvitationState)
    suspend fun updateHolidayDaysAndInternalState(holidayDays: Int, internal: Boolean)= preferences.updateHolidayDaysAndInternalState(holidayDays, internal)
    suspend fun denyInvitationToTeam(user: User)= preferences.denyInvitationToTeam(user)
    suspend fun deleteUserData()= preferences.deleteUserData()
}

interface IPreferences {
    suspend fun getUser(): User
    suspend fun saveUser(user: User)
    suspend fun updateEmailVerified()
    suspend fun updateImageProfile(user: User)
    suspend fun updateMessagingToken(token: String)
    suspend fun getMessagingToken(): String
    suspend fun updateProfile(profile: String)
    suspend fun updateBossVerification(bossVerifiedAt: String)
    suspend fun updateTeamCreated(team: Team, teamInvitationState: Reply)
    suspend fun updateTeamInvitationState(teamInvitationState: Reply)
    suspend fun updateHolidayDaysAndInternalState(holidayDays: Int, internal: Boolean)
    suspend fun denyInvitationToTeam(user: User)
    suspend fun deleteUserData()
}
