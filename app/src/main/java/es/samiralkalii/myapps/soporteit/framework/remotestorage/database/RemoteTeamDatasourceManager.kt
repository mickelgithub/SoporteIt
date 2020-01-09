package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import es.samiralkalii.myapps.data.teammanagement.IRemoteTeamManagementDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.ui.util.KEY_BOSS_VERIFICATION
import es.samiralkalii.myapps.soporteit.ui.util.KEY_MESSAGING_TOKEN
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList


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



class RemoteTeamDatasourceManager(val fstore: FirebaseFirestore): IRemoteTeamManagementDatasource {

    private val logger= LoggerFactory.getLogger(RemoteTeamDatasourceManager::class.java)

    override suspend fun addTeam(team: Team, boss: String) {
        fstore.collection(USERS_REF).document(boss).update(TEAM_DOCUMENT_REF, team).await()
    }

    override suspend fun getAllUsersButBosesAndNoTeam(): List<User> {
        val result= fstore.collection(USERS_REF).whereEqualTo(KEY_BOSS_VERIFICATION, "").get(Source.SERVER).await()
        val methodResult= ArrayList<User>()
        result.forEach { document ->
            val user: User= document.toObject(User::class.java)
            logger.debug(user.toString())
            methodResult.add(user)
        }
        return methodResult
    }

    override suspend fun sendTeamInvitationToUser(sender: User, destination: User) {

        val notifRef= fstore.collection(NOTIFICATION_REF).document()
        notifRef.set(mapOf<String, Any>(
            KEY_TYPE to KEY_INVITATION_NOTIF_TYPE,
            KEY_NOTIF_SENDER to mapOf<String, String>(
                KEY_USER to sender.id,
                KEY_MESSAGING_TOKEN to sender.messagingToken,
                KEY_DATE to Calendar.getInstance().time.time.toString()
            ),
            KEY_DESTINATION to mapOf<String, String>(
                KEY_TYPE to VALUE_USER,
                KEY_VALUE to destination.id,
                KEY_MESSAGING_TOKEN to destination.messagingToken
            )
        ))
    }


}