package es.samiralkalii.myapps.data.notifications

import es.samiralkalii.myapps.domain.notification.Notification

class RemoteNotificationsRepository(val remoteNotificationsDatasource: IRemoteNotificationsDatasource) {

    suspend fun getNotificationReceivedById(userid: String, notifid: String)= remoteNotificationsDatasource.getNotificationReceivedById(userid, notifid)

}

interface IRemoteNotificationsDatasource {

    suspend fun getNotificationReceivedById(userid: String, notifid: String): Notification

}