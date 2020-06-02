package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class NewGroupUseCase(private val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(group: MutableMap<String, Any>)= remoteTeamManagementRepository.createNewGroup(group)
}