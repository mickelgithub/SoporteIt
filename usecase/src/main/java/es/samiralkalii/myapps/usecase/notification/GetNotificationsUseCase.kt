package es.samiralkalii.myapps.usecase.notification

import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import org.slf4j.LoggerFactory

enum class NotificationCategory {
    RECEIVED, SENT
}

class GetNotificationsUseCase(val remoteNotificationsRepository: RemoteNotificationsRepository) {

    private val logger = LoggerFactory.getLogger(GetNotificationsUseCase::class.java)

    suspend operator fun invoke(userid: String, notifCategory: NotificationCategory)= when(notifCategory) {

        NotificationCategory.RECEIVED -> remoteNotificationsRepository.getReceivedNotifications(userid)
        NotificationCategory.SENT -> remoteNotificationsRepository.getSentNotifications(userid)

    }


}