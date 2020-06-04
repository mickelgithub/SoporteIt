package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class DeleteUsersUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(users: List<String>)= remoteTeamManagementRepository.deleteUsers(users)

}