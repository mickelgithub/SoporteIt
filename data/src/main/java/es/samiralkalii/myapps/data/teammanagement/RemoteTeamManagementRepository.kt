package es.samiralkalii.myapps.data.teammanagement

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.*

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

    suspend fun getMyGroups(user: User)= remoteTeamManagementDatasource.getMyGroups(user)
    suspend fun confirmDenyMember(user: String, isConfirmed: Boolean,profile: String,
                                  profileId: String, holidayDays: Int, internal: Boolean,
                                  area: String, department: String)= remoteTeamManagementDatasource.confirmDenyMember(
        user, isConfirmed, profile, profileId, holidayDays, internal, area, department)
    suspend fun getMemberConfirmationData(user: String)= remoteTeamManagementDatasource.getMemberConfirmationData(user)

    suspend fun getProfiles(area: String)= remoteTeamManagementDatasource.getProfiles(area)

    suspend fun getDepartmentUsers(area: String, deparment: String)= remoteTeamManagementDatasource.getDepartmentUsers(area, deparment)

    suspend fun createNewGroup(group: MutableMap<String, Any>)= remoteTeamManagementDatasource.createNewGroup(group)

    suspend fun updateGroup(groupId: String, group: MutableMap<String, Any>)= remoteTeamManagementDatasource.updateGroup(groupId, group)

    suspend fun deleteGroup(group: String)= remoteTeamManagementDatasource.deleteGroup(group)


    suspend fun deleteUsers(users: List<String>)= remoteTeamManagementDatasource.deleteUsers(users)

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
    suspend fun getMyGroups(user: User): GroupList
    suspend fun confirmDenyMember(user: String, isConfirmed: Boolean,profile: String,
                                  profileId: String, holidayDays: Int, internal: Boolean,
                                  area: String, department: String)
    suspend fun getMemberConfirmationData(user: String): Map<String, Any?>
    suspend fun getProfiles(area: String): Profiles
    suspend fun getDepartmentUsers(area: String, deparment: String): List<User>

    suspend fun createNewGroup(group: MutableMap<String, Any>)

    suspend fun updateGroup(groupId: String, group: MutableMap<String, Any>)

    suspend fun deleteGroup(group: String)

    suspend fun deleteUsers(users: List<String>)


}