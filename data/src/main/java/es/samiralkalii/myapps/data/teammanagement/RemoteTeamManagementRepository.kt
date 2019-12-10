package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteTeamManagementRepository(val remoteTeamManagementDatasource: IRemoteTeamManagementDatasource) {


    suspend fun addTeam(team: Team, boss: String)= remoteTeamManagementDatasource.addTeam(team, boss)
    suspend fun getAllUsersButBoses()= remoteTeamManagementDatasource.getAllUsersButBoses()

}

interface IRemoteTeamManagementDatasource {
    suspend fun addTeam(team: Team, boss: String)
    suspend fun getAllUsersButBoses(): List<User>
}