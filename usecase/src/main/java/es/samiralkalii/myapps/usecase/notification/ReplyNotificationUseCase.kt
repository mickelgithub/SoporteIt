package es.samiralkalii.myapps.usecase.notification

import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import es.samiralkalii.myapps.domain.notification.Reply
import org.slf4j.LoggerFactory

class ReplyNotificationUseCase(private val remoteNotificationsRepository: RemoteNotificationsRepository) {

    private val logger = LoggerFactory.getLogger(ReplyNotificationUseCase::class.java)

    suspend operator fun invoke(userid: String, notification: String, reply: Reply, reasonKo: String) {
        remoteNotificationsRepository.replyNotification(userid, notification, reply, reasonKo)
    }
}