package es.samiralkalii.myapps.notification

class NotificationRepository(val notificationManager: INotification) {

    fun showNotification(title: String, body: String)= notificationManager.showNotification(title, body)
}

interface INotification {

    fun showNotification(title: String, body: String)
}