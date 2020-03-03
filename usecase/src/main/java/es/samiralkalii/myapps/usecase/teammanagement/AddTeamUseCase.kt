package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class AddTeamUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository,
                     val remoteUserRepository: RemoteUserRepository,
                     val preferenceRepository: PreferenceRepository) {

    private val logger = LoggerFactory.getLogger(AddTeamUseCase::class.java)

    suspend operator fun invoke(team: Team): String {
        if (!remoteTeamManagementRepository.isTeamAlreadyExists(team)) {
            val teamId= remoteTeamManagementRepository.addTeam(team)
            remoteUserRepository.updateTeamCreated(team)
            preferenceRepository.updateTeamCreated(team, Reply.NONE)
            return teamId
        }
        return ""
    }
}