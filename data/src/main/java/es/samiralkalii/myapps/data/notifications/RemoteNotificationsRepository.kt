package es.samiralkalii.myapps.data.notifications

import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.domain.notification.Reply

class RemoteNotificationsRepository(val remoteNotificationsDatasource: IRemoteNotificationsDatasource) {

    suspend fun getNotificationReceivedById(userid: String, notifid: String)= remoteNotificationsDatasource.getNotificationReceivedById(userid, notifid)
    suspend fun getReceivedNotifications(userid: String)= remoteNotificationsDatasource.getReceivedNotifications(userid)
    suspend fun getSentNotifications(userid: String)= remoteNotificationsDatasource.getSentNotifications(userid)
    suspend fun updateNotificationState(userid: String, notification: String, state: NotifState)= remoteNotificationsDatasource.updateNotificationState(userid, notification, state)
    suspend fun deleteNotification(userid: String, notification: String)= remoteNotificationsDatasource.deleteNotification(userid, notification)
    suspend fun replyNotification(userid: String, notification: String, reply: Reply, reasonKo: String)= remoteNotificationsDatasource.replyNotification(userid, notification, reply, reasonKo)

}

interface IRemoteNotificationsDatasource {

    suspend fun getNotificationReceivedById(userid: String, notifid: String): Notification
    suspend fun getReceivedNotifications(userid: String): List<Notification>
    suspend fun getSentNotifications(userid: String): List<Notification>
    suspend fun updateNotificationState(userid: String, notification: String, state: NotifState)
    suspend fun deleteNotification(userid: String, notification: String)
    suspend fun replyNotification(userid: String, notification: String, reply: Reply, reasonKo: String)

}