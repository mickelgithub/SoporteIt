package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.domain.User
import org.slf4j.LoggerFactory

class InviteUserUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository,
                        val remoteUserRepository: RemoteUserRepository) {

    private val logger = LoggerFactory.getLogger(InviteUserUseCase::class.java)

    suspend operator fun invoke(sender: User, destination: User, internalEmployee: Boolean, holidayDays: Int) {

        remoteTeamManagementRepository.inviteUserToTeam(sender, destination)
        remoteUserRepository.updateHolidayDaysAndInternalState(destination.id, holidayDays, internalEmployee)

    }

}