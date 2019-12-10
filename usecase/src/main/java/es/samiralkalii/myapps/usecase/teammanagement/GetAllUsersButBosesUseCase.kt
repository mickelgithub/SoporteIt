package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.domain.User
import org.slf4j.LoggerFactory

class GetAllUsersButBosesUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    private val logger = LoggerFactory.getLogger(GetAllUsersButBosesUseCase::class.java)

    suspend operator fun invoke(): List<User> {
        return remoteTeamManagementRepository.getAllUsersButBoses()
    }


}