package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class GetDeparmentUsersUseCase(private val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(area: String, deparment: String)= remoteTeamManagementRepository.getDepartmentUsers(area, deparment)
}