package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class DeleteGroupUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(group: String)= remoteTeamManagementRepository.deleteGroup(group)
}