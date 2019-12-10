package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.teammanagement.IRemoteTeamManagementDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.ui.util.KEY_BOSS_VERIFICATION
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private const val TEAM_COLLECTION_REF= "teams"
private const val TEAM_DOCUMENT_REF= "team"

class RemoteTeamDatasourceManager(val fstore: FirebaseFirestore): IRemoteTeamManagementDatasource {

    private val logger= LoggerFactory.getLogger(RemoteTeamDatasourceManager::class.java)

    override suspend fun addTeam(team: Team, boss: String) {
        fstore.collection(USERS_REF).document(boss).update(TEAM_DOCUMENT_REF, team).await()
    }

    override suspend fun getAllUsersButBoses(): List<User> {
        val result= fstore.collection(USERS_REF).whereEqualTo(KEY_BOSS_VERIFICATION, "").get().await()
        val methodResult= ArrayList<User>()
        result.forEach { document ->
            val user: User= document.toObject(User::class.java)
            logger.debug(user.toString())
            methodResult.add(user)
        }
        return methodResult
    }


}