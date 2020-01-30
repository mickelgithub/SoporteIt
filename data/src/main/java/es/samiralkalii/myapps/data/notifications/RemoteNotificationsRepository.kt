package es.samiralkalii.myapps.data.notifications

import es.samiralkalii.myapps.domain.notification.Notification

class RemoteNotificationsRepository(val remoteNotificationsDatasource: IRemoteNotificationsDatasource) {

    suspend fun getNotificationReceivedById(userid: String, notifid: String)= remoteNotificationsDatasource.getNotificationReceivedById(userid, notifid)
    suspend fun getReceivedNotifications(userid: String)= remoteNotificationsDatasource.getReceivedNotifications(userid)
    suspend fun getSentNotifications(userid: String)= remoteNotificationsDatasource.getSentNotifications(userid)

}

interface IRemoteNotificationsDatasource {

    suspend fun getNotificationReceivedById(userid: String, notifid: String): Notification
    suspend fun getReceivedNotifications(userid: String): List<Notification>
    suspend fun getSentNotifications(userid: String): List<Notification>

}