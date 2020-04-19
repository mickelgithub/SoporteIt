package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class ConfirmDenyMemberUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(user: String, isConfirmed: Boolean)= remoteTeamManagementRepository.confirmDenyMember(user, isConfirmed)
}