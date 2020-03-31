package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class GetBossCategoriesUseCase(private val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke()= remoteTeamManagementRepository.getBossCategorties()
}