package es.samiralkalii.myapps.notification

class NotificationRepository(val notificationManager: INotification) {

    fun showNotificationBossUpdated(title: String, body: String)= notificationManager.showNotificationBossUpdated(title, body)
    fun showNotificationInvitationToTeam(title: String, body: String, largeIconUrl: String)= notificationManager.showNotificationInvitationToTeam(title, body, largeIconUrl)
    fun cancelNotification()= notificationManager.cancelNotification()
}

interface INotification {

    fun showNotificationBossUpdated(title: String, body: String)
    fun showNotificationInvitationToTeam(title: String, body: String, largeIconUrl: String)
    fun cancelNotification()
}