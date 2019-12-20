package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteTeamManagementRepository(val remoteTeamManagementDatasource: IRemoteTeamManagementDatasource) {


    suspend fun addTeam(team: Team, boss: String)= remoteTeamManagementDatasource.addTeam(team, boss)
    suspend fun getAllUsersButBosesAndNoTeam()= remoteTeamManagementDatasource.getAllUsersButBosesAndNoTeam()

}

interface IRemoteTeamManagementDatasource {
    suspend fun addTeam(team: Team, boss: String)
    suspend fun getAllUsersButBosesAndNoTeam(): List<User>
}