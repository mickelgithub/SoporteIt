package es.samiralkalii.myapps.usecase.messaging

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

const val RESULT_OK_VALUE= "OK"
const val RESULT_KO_VALUE= "KO"
const val MESSAGE_ID_KEY= "messageId"
const val RESULT_KEY= "result"
const val MESSAGE_BODY_KEY= "body"
const val MESSAGE_TO_KEY= "to"

const val PENDING= "P"

//const val BOSS_VERIFICATION_TITLE=  "Verification Jefe de Equipo"
//const val BOSS_VERIFICATION_DESC_OK= "Hemos verificado que eres responsable de equipo\n \uD83D\uDE42\uD83D\uDE42\uD83D\uDE42"
//const val BOSS_VERIFICATION_DESC_KO= "No hemos podido verificar que eres responsable de Equipo\n \uD83D\uDE1F\uD83D\uDE1F\uD83D\uDE1F"
const val BOSS_VERIFICATION_OK= "S"
const val BOSS_VERIFICATION_KO= "N"
const val MESSAGE_ID_BOSS_VERIFICATION= "boss_verification"
const val MESSAGE_ID_INVITATION_TO_BE_PART_OF_TEAM= "invitation_to_be_part_of_team"

class NotifyMessagingUseCase(val notificationRepository: NotificationRepository, val preferenceRepository: PreferenceRepository,
                             val remoteUserRepository: RemoteUserRepository, val remoteNotificationsRepository: RemoteNotificationsRepository) {

    private val logger = LoggerFactory.getLogger(NotifyMessagingUseCase::class.java)

    suspend operator fun invoke(messageId: String, result:String, to: String, body: String) {

        val userId= to
        when (messageId) {
            MESSAGE_ID_BOSS_VERIFICATION -> {
                logger.debug("boss verification notification...")
                if (result == RESULT_OK_VALUE) {
                    //Is Boss OK
                    val bossVerifiedAt= remoteUserRepository.updateBossVerifiedAt(userId)
                    preferenceRepository.updateBossVerification(bossVerifiedAt)
                } else {
                    //Is not Boss
                    //remoteUserRepository.updateProfile("", userId)
                    remoteUserRepository.signOut()
                    preferenceRepository.deleteUserData()
                    //preferenceRepository.updateBossVerification(BOSS_VERIFICATION_KO)
                }
                notificationRepository.showNotificationBossUpdated(result== RESULT_OK_VALUE)
            }
            MESSAGE_ID_INVITATION_TO_BE_PART_OF_TEAM -> {
                logger.debug("invitation notification...")
                val notifId= body
                val notification= remoteNotificationsRepository.getNotificationReceivedById(userId, notifId)
                preferenceRepository.updateTeamCreated(Team(id = notification.teamId, name = notification.team,
                    boss = notification.sender, nameInsensitive = notification.team.toUpperCase()), teamInvitationState = Reply.PENDING)
                val remoteUser= User.EMPTY.apply {
                    //id= notification.destination
                }
                //remoteUserRepository.getUserInfo(remoteUser)
                preferenceRepository.updateHolidayDaysAndInternalState(remoteUser.holidayDays, remoteUser.internalEmployee)
                notificationRepository.showNotificationInvitationToTeam(notification.senderName, notification.senderEmail, notification.team,
                    notification.senderProfileImage, notifId)
            }
        }
    }
}
