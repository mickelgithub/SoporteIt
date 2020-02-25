package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.notifications.IRemoteNotificationsDatasource
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.domain.notification.Reply
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory
import java.util.*


private const val KEY_NOTIFICATION_DELETED= "deleted"
private const val KEY_NOTIFICATION_DELETION_DATE= "deletionDate"
private const val KEY_NOTIFICATION_STATE= "state"
private const val KEY_NOTIFICATION_REPLY= "reply"
private const val KEY_NOTIFICATION_REPLY_DATE= "replyDate"
private const val KEY_NOTIFICATION_REASON_KO= "reasonKo"

class RemoteNotificationsDatasourceManager(val fstore: FirebaseFirestore): IRemoteNotificationsDatasource {

    private val logger= LoggerFactory.getLogger(RemoteNotificationsDatasourceManager::class.java)

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
        val mutableList= mutableListOf<Notification>()
        for (document in result) {
            val notif= document.toObject(Notification::class.java)
            mutableList.add(notif)
        }
        logger.debug("el valor de resut ....")
        return mutableList.toList()
    }

    override suspend fun getSentNotifications(userid: String): List<Notification> {
        val result= fstore.collection(USERS_REF).document(userid).collection(NOTIFS_SENT).whereEqualTo(
            KEY_NOTIFICATION_DELETED, false).get().await()
        val mutableList= mutableListOf<Notification>()
        for (document in result) {
            val notif= document.toObject(Notification::class.java)
            mutableList.add(notif)
        }
        logger.debug("el valor de resut ....")
        return mutableList.toList()
    }

    override suspend fun updateNotificationState(userid: String, notification: String, state: NotifState) {
        fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).document(notification).update(mapOf( KEY_NOTIFICATION_STATE to state)).await()
    }

    override suspend fun deleteNotification(userid: String, notification: String) {
        fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).document(notification).update(mapOf( KEY_NOTIFICATION_DELETED to true,
            KEY_NOTIFICATION_DELETION_DATE to Date().time
        )).await()
    }

    override suspend fun replyNotification(
        userid: String,
        notification: String,
        reply: Reply,
        reasonKo: String
    ) {
        fstore.collection(USERS_REF).document(userid).collection(NOTIFS_RECEIVED).document(notification).update(mapOf( KEY_NOTIFICATION_REPLY to reply,
            KEY_NOTIFICATION_REPLY_DATE to Date().time, KEY_NOTIFICATION_REASON_KO to reasonKo, KEY_NOTIFICATION_STATE to NotifState.READ
        ))
    }


}