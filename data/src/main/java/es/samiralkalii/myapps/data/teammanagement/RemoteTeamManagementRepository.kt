package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteTeamManagementRepository(val remoteTeamManagementDatasource: IRemoteTeamManagementDatasource) {


    suspend fun addTeam(team: Team)= remoteTeamManagementDatasource.addTeam(team)
    suspend fun getAllUsersButBosesAndNoTeam()= remoteTeamManagementDatasource.getAllUsersButBosesAndNoTeam()
    suspend fun inviteUser(sender: User, destination: User)= remoteTeamManagementDatasource.sendTeamInvitationToUser(sender, destination)

}

interface IRemoteTeamManagementDatasource {
    suspend fun addTeam(team: Team)
    suspend fun getAllUsersButBosesAndNoTeam(): List<User>
    suspend fun sendTeamInvitationToUser(sender: User, destination: User)
}