package es.samiralkalii.myapps.usecase.messaging

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class HandleTeamInvitationUseCase(private val notificationRepository: NotificationRepository,
                                  private val preferenceRepository: PreferenceRepository,
                                  private val remoteUserRepository: RemoteUserRepository,
                                  private val remoteTeamManagementRepository: RemoteTeamManagementRepository,
                                  private val remoteNotificationsRepository: RemoteNotificationsRepository
) {

    private val logger = LoggerFactory.getLogger(HandleTeamInvitationUseCase::class.java)

    suspend operator fun invoke(user: User, reply: Reply, replyDescription: String, notifId: String) {
        notificationRepository.cancelNotification()
        if (reply== Reply.OK) {
            user.teamInvitationState= reply
            remoteUserRepository.updateTeamInvitationState(user, reply)
            remoteTeamManagementRepository.addUserToTeam(user)
            remoteNotificationsRepository.replyNotification(user.id, notifId, reply, "")
            preferenceRepository.updateTeamInvitationState(reply)
        } else {
            //no se ha aceptado la invitacion
            user.teamInvitationState= Reply.NONE
            user.boss= ""
            user.team= ""
            user.teamId= ""
            remoteUserRepository.denyInvitationToTeam(user)
            logger.debug("........................${Thread.currentThread().name}")
            remoteNotificationsRepository.replyNotification(user.id, notifId, reply, replyDescription)
            preferenceRepository.denyInvitationToTeam(user)
        }
    }
}