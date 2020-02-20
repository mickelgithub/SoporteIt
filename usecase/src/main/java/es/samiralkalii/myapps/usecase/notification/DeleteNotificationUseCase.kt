package es.samiralkalii.myapps.usecase.notification

import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import org.slf4j.LoggerFactory

class DeleteNotificationUseCase(private val remoteNotificationsRepository: RemoteNotificationsRepository) {

    private val logger = LoggerFactory.getLogger(DeleteNotificationUseCase::class.java)

    suspend operator fun invoke(userid: String, notification: String) {
        remoteNotificationsRepository.deleteNotification(userid, notification)
    }
}