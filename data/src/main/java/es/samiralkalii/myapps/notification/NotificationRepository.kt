package es.samiralkalii.myapps.notification

import es.samiralkalii.myapps.domain.teammanagement.Department

class NotificationRepository(val notificationManager: INotification) {

    fun showNotificationBossUpdated(isBoss: Boolean)= notificationManager.showNotificationBossUpdated(isBoss)
    //fun showNotificationInvitationToTeam(bossName: String, bossMail: String, team: String, largeIconUrl: String, notificationId: String)= notificationManager.showNotificationInvitationToTeam(
        //bossName, bossMail, team, largeIconUrl, notificationId)
    fun cancelNotification()= notificationManager.cancelNotification()
    fun showNotificationMemberConfirmation(isBoss: Boolean, department: String)= notificationManager.showNotificationMemberConfirmation(isBoss, department)
    fun showNotificationNewMember(user: String, userEmail: String)= notificationManager.showNotificationNewMember(user, userEmail)
}

interface INotification {

    fun showNotificationBossUpdated(isBoss: Boolean)
    //fun showNotificationInvitationToTeam(bossName: String, bossMail: String, team: String, largeIconUrl: String, notificationId: String)
    fun cancelNotification()
    fun showNotificationMemberConfirmation(isBoss: Boolean, department: String)
    fun showNotificationNewMember(user: String, userEmail: String)
}