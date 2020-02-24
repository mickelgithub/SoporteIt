package es.samiralkalii.myapps.notification

class NotificationRepository(val notificationManager: INotification) {

    fun showNotificationBossUpdated(isBoss: Boolean)= notificationManager.showNotificationBossUpdated(isBoss)
    fun showNotificationInvitationToTeam(bossName: String, bossMail: String, team: String, largeIconUrl: String, notificationId: String)= notificationManager.showNotificationInvitationToTeam(
        bossName, bossMail, team, largeIconUrl, notificationId)
    fun cancelNotification()= notificationManager.cancelNotification()
}

interface INotification {

    fun showNotificationBossUpdated(isBoss: Boolean)
    fun showNotificationInvitationToTeam(bossName: String, bossMail: String, team: String, largeIconUrl: String, notificationId: String)
    fun cancelNotification()
}