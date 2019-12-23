package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import org.slf4j.LoggerFactory

class InviteUserUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    private val logger = LoggerFactory.getLogger(InviteUserUseCase::class.java)

    suspend operator fun invoke() {

    }

}