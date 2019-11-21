package es.samiralkalii.myapps.notification

class NotificationRepository(val notificationManager: INotification) {

    fun showNotificationBossUpdated(title: String, body: String)= notificationManager.showNotificationBossUpdated(title, body)
}

interface INotification {

    fun showNotificationBossUpdated(title: String, body: String)
}