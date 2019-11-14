package messaging

import es.samiralkalii.myapps.notification.NotificationRepository
import org.slf4j.LoggerFactory

const val RESULT_OK_VALUE= "OK"
const val RESULT_KO_VALUE= "KO"
const val MESSAGE_ID_KEY= "messageId"
const val RESULT_KEY= "result"
const val MESSAGE_BODY_KEY= "body"
const val MESSAGE_ID_BOSS_VERIFICATION= "boss_verification"

class NotifyMessagingUseCase(val notificationRepository: NotificationRepository) {

    private val logger = LoggerFactory.getLogger(NotifyMessagingUseCase::class.java)

    operator fun invoke(messageId: String, result:String, body: String) {

        var title= ""
        var notifBody= ""

        if (messageId== MESSAGE_ID_BOSS_VERIFICATION) {
            title = "Verification Jefe de Equipo"
            notifBody =
                if (result == RESULT_OK_VALUE) "Hemos verificado que eres responsable de equipo\n \uD83D\uDE42\uD83D\uDE42\uD83D\uDE42"
                else "No hemos podido verificar que eres responsable de Equipo\n \uD83D\uDE1F\uD83D\uDE1F\uD83D\uDE1F"
        }
        notificationRepository.showNotification(title, notifBody)
    }
}