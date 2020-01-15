package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteTeamManagementRepository(val remoteTeamManagementDatasource: IRemoteTeamManagementDatasource) {

    suspend fun addTeam(team: Team)= remoteTeamManagementDatasource.addTeam(team)
    suspend fun getAllUsersButBosesAndNoTeam()= remoteTeamManagementDatasource.getAllUsersButBosesAndNoTeam()
    suspend fun isTeamAlreadyExists(team: Team): Boolean= remoteTeamManagementDatasource.isTeamAlreadyExists(team)
    suspend fun inviteUserToTeam(sender: User, destination: User)= remoteTeamManagementDatasource.inviteUserToTeam(sender, destination)

}

interface IRemoteTeamManagementDatasource {
    suspend fun addTeam(team: Team): String //return teamId
    suspend fun getAllUsersButBosesAndNoTeam(): List<User>
    suspend fun isTeamAlreadyExists(team: Team): Boolean
    suspend fun inviteUserToTeam(sender: User, destination: User)
}