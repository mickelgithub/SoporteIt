package es.samiralkalii.myapps.usecase.notification

import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import es.samiralkalii.myapps.domain.notification.NotifState
import org.slf4j.LoggerFactory

class UpdateNotificationStateUseCase(val remoteNotificationsRepository: RemoteNotificationsRepository) {

    private val logger = LoggerFactory.getLogger(UpdateNotificationStateUseCase::class.java)

    suspend operator fun invoke(userid: String, notification: String, state: NotifState) {
        remoteNotificationsRepository.updateNotificationState(userid, notification, state)
    }
}