package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import es.samiralkalii.myapps.data.teammanagement.IRemoteTeamManagementDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.usecase.messaging.PENDING
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private const val TEAM_DOCUMENT_REF= "team"
private const val NOTIFICATION_REF= "notifications"
private const val KEY_NOTIF_SENDER= "sender"
private const val KEY_TYPE= "type"
private const val KEY_VALUE= "value"
private const val KEY_DATE= "date"
private const val KEY_DESTINATION= "destination"
private const val KEY_INVITATION_NOTIF_TYPE= "invitation_to_team"
private const val KEY_USER= "user"
private const val VALUE_USER= "user"
private const val VALUE_GROUP= "group"
private const val TEAM_REF= "teams"
const val NOTIFS_SENT= "notifsSent"
const val NOTIFS_RECEIVED= "notifsReceived"
private const val IN_PROGRESS= "in_progress"
private const val MEMBERS_FIELD= "members"



class RemoteTeamDatasourceManager(val fstore: FirebaseFirestore): IRemoteTeamManagementDatasource {

    private val logger= LoggerFactory.getLogger(RemoteTeamDatasourceManager::class.java)

    override suspend fun addTeam(team: Team): String {
        team.members?.add(team.boss)
        val newTeamRef= fstore.collection(TEAM_REF).document()
        team.id= newTeamRef.id
        newTeamRef.set(team)
        return newTeamRef.id
    }

    override suspend fun isTeamAlreadyExists(team: Team): Boolean {
        val result= fstore.collection(TEAM_REF).whereEqualTo(KEY_TEAM_NAME_INSENSITIVE, team.nameInsensitive).get().await()
        return result!= null && result.size()>= 1
    }

    override suspend fun getAllUsersButBosesAndNoTeam(): List<User> {
        val result= fstore.collection(USERS_REF).whereEqualTo(KEY_BOSS_VERIFICATION, "")
            .whereEqualTo(KEY_EMAIL_VERIFIED, true)
            .whereGreaterThanOrEqualTo(KEY_TEAM_ID, "")
            .whereLessThanOrEqualTo(KEY_TEAM_ID, "")
            .get(Source.SERVER).await()
        val methodResult= ArrayList<User>()
        result.forEach { document ->
            val user: User= document.toObject(User::class.java)
            logger.debug(user.toString())
            methodResult.add(user)
        }
        return methodResult
    }

    override suspend fun inviteUserToTeam(sender: User, destination: User) {

        val notification= Notification(
            type= NotifType.ACTION_INVITE_TEAM,
            sender = sender.id,
            senderName = sender.name,
            senderEmail = sender.email,
            senderProfileImage = sender.remoteProfileImage,
            destination = destination.id
            //team = sender.team,
            //teamId = sender.teamId
        )
        fstore.runTransaction { _ ->
            val notifRef= fstore.collection(NOTIFICATION_REF).document()
            //we add a notification to top level notifications
            notification.id= notifRef.id
            notifRef.set(notification)
            fstore.collection(USERS_REF).document(sender.id)
                .collection(NOTIFS_SENT).document(notification.id).set(notification)
            fstore.collection(USERS_REF).document(destination.id)
                .collection(NOTIFS_RECEIVED).document(notification.id).set(notification)
            fstore.collection(USERS_REF).document(destination.id).update(mapOf(
                KEY_TEAM to notification.team,
                KEY_TEAM_ID to notification.teamId,
                KEY_BOSS to notification.sender,
                KEY_TEAM_INVITATION_STATE to Reply.PENDING
            ))

        }.await()

    }

    override suspend fun addUserToTeam(user: User) {
        /*val refMembers= fstore.collection(TEAM_REF).document(user.teamId)
        refMembers.update(MEMBERS_FIELD, FieldValue.arrayUnion(user.id))*/
    }

}