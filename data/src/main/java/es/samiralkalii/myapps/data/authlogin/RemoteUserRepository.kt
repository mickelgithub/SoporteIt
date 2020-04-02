package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteUserRepository(val remoteUserDatasource: IRemoteUserDatasource) {

    suspend fun addUser(user: User) = remoteUserDatasource.addUser(user)
    suspend fun getUserInfo(user: User) = remoteUserDatasource.getUserInfo(user)
    suspend fun updateEmailVerified(user: User) = remoteUserDatasource.updateEmailVerified(user)
    suspend fun updateImageProfile(user: User) = remoteUserDatasource.updateImageProfile(user)
    suspend fun updateMessagingToken(token: String) = remoteUserDatasource.updateMessagingToken(token)
    suspend fun updateProfile(profile: String, userId: String)= remoteUserDatasource.updateProfile(profile, userId)
    suspend fun updateTeamCreated(team: Team)= remoteUserDatasource.updateTeamCreated(team)
    suspend fun updateTeamInvitationState(user: User, teamInvitationState: Reply)= remoteUserDatasource.updateTeamInvitationState(user, teamInvitationState)
    suspend fun updateHolidayDaysAndInternalState(userId: String, holidayDays: Int, internal: Boolean)= remoteUserDatasource.updateHolidayDaysAndInternalState(
        userId, holidayDays, internal)
    suspend fun denyInvitationToTeam(user: User)= remoteUserDatasource.denyInvitationToTeam(user)
    suspend fun updateBossVerifiedAt(user: String)= remoteUserDatasource.updateBossVerifiedAt(user)
    suspend fun signOut()= remoteUserDatasource.signOut()

}

interface IRemoteUserDatasource {
    suspend fun addUser(user: User)
    suspend fun getUserInfo(user: User)
    suspend fun updateEmailVerified(user: User)
    suspend fun updateImageProfile(user: User)
    suspend fun updateMessagingToken(token: String)
    suspend fun updateProfile(profile: String, userId: String)
    suspend fun updateTeamCreated(team: Team)
    suspend fun updateTeamInvitationState(user: User, teamInvitationState: Reply)
    suspend fun updateHolidayDaysAndInternalState(userId: String, holidayDays: Int, internal: Boolean)
    suspend fun denyInvitationToTeam(user: User)
    suspend fun updateBossVerifiedAt(user: String): String
    suspend fun signOut()


}