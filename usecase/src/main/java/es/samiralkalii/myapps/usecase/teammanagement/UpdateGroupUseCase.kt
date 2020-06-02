package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class UpdateGroupUseCase(private val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(groupId: String, group: MutableMap<String, Any>)= remoteTeamManagementRepository.updateGroup(groupId, group)
}