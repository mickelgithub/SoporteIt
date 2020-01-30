package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.notifications.IRemoteNotificationsDatasource
import es.samiralkalii.myapps.domain.notification.Notification
import kotlinx.coroutines.tasks.await


private const val KEY_NOTIFICATION_DELETED= "deleted"

class RemoteNotificationsDatasourceManager(val fstore: FirebaseFirestore): IRemoteNotificationsDatasource {

    override suspend fun getNotificationReceivedById(
        userid: String,
        notifid: String
    ): Notification {
        val result= fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).document(notifid).get().await()
        return result?.toObject(Notification::class.java) ?: Notification.EMPTY
    }

    override suspend fun getReceivedNotifications(userid: String): List<Notification> {
        val result= fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).whereEqualTo(
            KEY_NOTIFICATION_DELETED, false).get().await()
        return result?.toObjects(List::class.java) as List<Notification>
    }

    override suspend fun getSentNotifications(userid: String): List<Notification> {
        val result= fstore.collection(USERS_REF).document(userid).collection(NOTIFS_SENT).whereEqualTo(
            KEY_NOTIFICATION_DELETED, false).get().await()
        return result?.toObjects(List::class.java) as List<Notification>
    }

}