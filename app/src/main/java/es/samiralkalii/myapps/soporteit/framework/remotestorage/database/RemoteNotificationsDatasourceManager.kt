package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.notifications.IRemoteNotificationsDatasource
import es.samiralkalii.myapps.domain.notification.Notification
import kotlinx.coroutines.tasks.await

class RemoteNotificationsDatasourceManager(val fstore: FirebaseFirestore): IRemoteNotificationsDatasource {

    override suspend fun getNotificationReceivedById(
        userid: String,
        notifid: String
    ): Notification {
        val result= fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).document(notifid).get().await()
        return result?.toObject(Notification::class.java) ?: Notification.EMPTY
    }

}