package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.AreasDepartments
import es.samiralkalii.myapps.domain.teammanagement.BossCategories
import es.samiralkalii.myapps.domain.teammanagement.Holidays
import es.samiralkalii.myapps.domain.teammanagement.Team

class RemoteTeamManagementRepository(val remoteTeamManagementDatasource: IRemoteTeamManagementDatasource) {

    suspend fun addTeam(team: Team)= remoteTeamManagementDatasource.addTeam(team)
    suspend fun getAllUsersButBosesAndNoTeam()= remoteTeamManagementDatasource.getAllUsersButBosesAndNoTeam()
    suspend fun isTeamAlreadyExists(team: Team): Boolean= remoteTeamManagementDatasource.isTeamAlreadyExists(team)
    suspend fun inviteUserToTeam(sender: User, destination: User)= remoteTeamManagementDatasource.inviteUserToTeam(sender, destination)
    suspend fun addUserToTeam(user: User)= remoteTeamManagementDatasource.addUserToTeam(user)

    suspend fun isBossAlreadyExist(areaId: String, departmentId: String, bossLevel: Int)= remoteTeamManagementDatasource.isBossAlreadyExist(areaId, departmentId, bossLevel)

    suspend fun getAreasDepartments()= remoteTeamManagementDatasource.getAreasDepartments()
    suspend fun getBossCategorties()= remoteTeamManagementDatasource.getBossCategorties()
    suspend fun getHolidayDays()= remoteTeamManagementDatasource.getHolidayDays()


}

interface IRemoteTeamManagementDatasource {
    suspend fun addTeam(team: Team): String //return teamId
    suspend fun getAllUsersButBosesAndNoTeam(): List<User>
    suspend fun isTeamAlreadyExists(team: Team): Boolean
    suspend fun inviteUserToTeam(sender: User, destination: User)
    suspend fun addUserToTeam(user: User)

    suspend fun getAreasDepartments(): AreasDepartments
    suspend fun getBossCategorties(): BossCategories
    suspend fun isBossAlreadyExist(areaId: String, departmentId: String, bossLevel: Int): Boolean
    suspend fun getHolidayDays(): Holidays


}