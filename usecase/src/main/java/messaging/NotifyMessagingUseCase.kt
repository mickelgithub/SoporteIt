package messaging

import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

const val RESULT_OK_VALUE= "OK"
const val RESULT_KO_VALUE= "KO"
const val MESSAGE_ID_KEY= "messageId"
const val RESULT_KEY= "result"
const val MESSAGE_BODY_KEY= "body"
const val MESSAGE_ID_BOSS_VERIFICATION= "boss_verification"

class NotifyMessagingUseCase(val notificationRepository: NotificationRepository, val preferenceRepository: PreferenceRepository) {

    private val logger = LoggerFactory.getLogger(NotifyMessagingUseCase::class.java)

    suspend operator fun invoke(messageId: String, result:String, body: String) {

        var title= ""
        var notifBody= ""

        if (messageId== MESSAGE_ID_BOSS_VERIFICATION) {
            title = "Verification Jefe de Equipo"
            if (result == RESULT_OK_VALUE) {
                notifBody= "Hemos verificado que eres responsable de equipo\n \uD83D\uDE42\uD83D\uDE42\uD83D\uDE42"
            } else {
                notifBody= "No hemos podido verificar que eres responsable de Equipo\n \uD83D\uDE1F\uD83D\uDE1F\uD83D\uDE1F"
                preferenceRepository.updateProfile("")
            }
            notificationRepository.showNotificationBossUpdated(title, notifBody)
        }
    }
}