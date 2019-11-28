package es.samiralkalii.myapps.usecase.messaging

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

const val RESULT_OK_VALUE= "OK"
const val RESULT_KO_VALUE= "KO"
const val MESSAGE_ID_KEY= "messageId"
const val RESULT_KEY= "result"
const val MESSAGE_BODY_KEY= "body"
const val MESSAGE_ID_BOSS_VERIFICATION= "boss_verification"
const val BOSS_VERIFICATION_TITLE=  "Verification Jefe de Equipo"
const val BOSS_VERIFICATION_DESC_OK= "Hemos verificado que eres responsable de equipo\n \uD83D\uDE42\uD83D\uDE42\uD83D\uDE42"
const val BOSS_VERIFICATION_DESC_KO= "No hemos podido verificar que eres responsable de Equipo\n \uD83D\uDE1F\uD83D\uDE1F\uD83D\uDE1F"
const val BOSS_VERIFICATION_OK= "S"
const val BOSS_VERIFICATION_KO= "N"

class NotifyMessagingUseCase(val notificationRepository: NotificationRepository, val preferenceRepository: PreferenceRepository,
                             val remoteUserRepository: RemoteUserRepository) {

    private val logger = LoggerFactory.getLogger(NotifyMessagingUseCase::class.java)

    suspend operator fun invoke(messageId: String, result:String, body: String) {

        var title= ""
        var notifBody= ""
        var userId= ""

        if (messageId== MESSAGE_ID_BOSS_VERIFICATION) {
            title = BOSS_VERIFICATION_TITLE
            userId= body
            if (result == RESULT_OK_VALUE) {
                notifBody= BOSS_VERIFICATION_DESC_OK
                preferenceRepository.updateBossVerification(BOSS_VERIFICATION_OK)
            } else {
                notifBody=BOSS_VERIFICATION_DESC_KO
                remoteUserRepository.updateProfile("", userId)
                preferenceRepository.updateProfile("")
                preferenceRepository.updateBossVerification(BOSS_VERIFICATION_KO)
            }
            notificationRepository.showNotificationBossUpdated(title, notifBody)
        }
    }
}