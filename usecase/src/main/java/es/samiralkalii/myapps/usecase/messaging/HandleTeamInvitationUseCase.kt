package es.samiralkalii.myapps.usecase.messaging

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

private const val OK= "S"

class HandleTeamInvitationUseCase(private val notificationRepository: NotificationRepository,
                                  private val preferenceRepository: PreferenceRepository,
                                  private val remoteUserRepository: RemoteUserRepository,
                                  private val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    private val logger = LoggerFactory.getLogger(HandleTeamInvitationUseCase::class.java)

    suspend operator fun invoke(user: User, reply: String, replyDescription: String) {
        notificationRepository.cancelNotification()
        if (OK== reply) {
            user.teamInvitationState= OK
            remoteUserRepository.updateTeamInvitationState(user, OK)
            remoteTeamManagementRepository.addUserToTeam(user)
            preferenceRepository.updateTeamInvitationState(OK)
            //Falta por actualizar el estado de la notificacion
        } else {
            //no se ha aceptado la invitacion
            user.teamInvitationState= ""
            remoteTeamManagementRepository.addUserToTeam(user)
            preferenceRepository.updateTeamInvitationState(OK)

        }
    }
}