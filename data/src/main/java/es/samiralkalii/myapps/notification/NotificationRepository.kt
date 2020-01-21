package es.samiralkalii.myapps.notification

class NotificationRepository(val notificationManager: INotification) {

    fun showNotificationBossUpdated(title: String, body: String)= notificationManager.showNotificationBossUpdated(title, body)
    fun showNotificationInvitationToTeam(title: String, body: String)= notificationManager.showNotificationInvitationToTeam(title, body)
    fun cancelNotification()= notificationManager.cancelNotification()
}

interface INotification {

    fun showNotificationBossUpdated(title: String, body: String)
    fun showNotificationInvitationToTeam(title: String, body: String)
    fun cancelNotification()
}