package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRemoteUserDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


const val USERS_REF= "users"
private const val KEY_USER= ""

class RemoteUserDatasourceManager(val fstore: FirebaseFirestore, val fbAuth: FirebaseAuth):
    IRemoteUserDatasource {

    private val logger= LoggerFactory.getLogger(RemoteUserDatasourceManager::class.java)

    override suspend fun updateMessagingToken(token: String) {

        if (fbAuth.currentUser!= null) {
            val firebaseUser= fbAuth.currentUser as FirebaseUser
            fstore.collection(USERS_REF).document(firebaseUser.uid).update(mapOf( KEY_MESSAGING_TOKEN to token)).await()
            logger.debug("updated messaging token $token en remote")
        } else {
            logger.debug("user is not logedUp, no is posible to register the token...")
        }
    }

    override suspend fun updateProfile(profile: String, userId: String) {
        fstore.collection(USERS_REF).document(userId).update(mapOf( KEY_PROFILE to profile)).await()
    }

    /*override suspend fun updateBossVerification(bossVerification: String, userId: String) {
        fstore.collection(USERS_REF).document(userId).update(mapOf( KEY_BOSS_VERIFIED to bossVerification)).await()
    }*/

    override suspend fun updateTeamCreated(team: Team) {
        fstore.collection(USERS_REF).document(team.boss).update(
            mapOf( KEY_TEAM to team.name, KEY_TEAM_ID to team.id)).await()
    }

    override suspend fun updateTeamInvitationState(user: User, teamInvitationState: Reply) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_TEAM_INVITATION_STATE to teamInvitationState))
    }

    override suspend fun updateHolidayDaysAndInternalState(
        userId: String,
        holidayDays: Int,
        internal: Boolean
    ) {
        fstore.collection(USERS_REF).document(userId).update(mapOf(KEY_HOLIDAY_DAYS to holidayDays,
            KEY_INTERNAL_EMPLOYEE to internal)).await()
    }

    override suspend fun denyInvitationToTeam(user: User) {
        logger.debug("${Thread.currentThread().name}--------------")
        fstore.collection(USERS_REF).document(user.id).update(mapOf(KEY_TEAM_INVITATION_STATE to "",
            KEY_TEAM to "", KEY_TEAM_ID to "", KEY_BOSS to ""))
        logger.debug("${Thread.currentThread().name}++++++++++++++")
    }

    override suspend fun updateBossVerifiedAt(user: String): String {
        var dateEnLocalTimeZone= ""
        val result= fstore.collection(USERS_REF).document(user).get().await()
        if (result!= null && result.data!= null) {
            val data = result.data!!
            val bossVerifiedAt = (data[KEY_BOSS_VERIFIED_AT] as String?) ?: ""
            if (bossVerifiedAt.isNotBlank()) {
                dateEnLocalTimeZone = formatDate(bossVerifiedAt.toLong())
                fstore.collection(USERS_REF).document(user)
                    .update(mapOf(KEY_BOSS_VERIFIED_AT to dateEnLocalTimeZone))

            }
        }
        return dateEnLocalTimeZone
    }

    override suspend fun signOut() {
        fbAuth.signOut()
    }

    override suspend fun updateEmailVerified(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_IS_EMAIL_VERIFIED to true)).await()
    }

    override suspend fun updateImageProfile(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_PROFILE_IMAGE to user.profileImage,
            KEY_REMOTE_PROFILE_IMAGE to user.remoteProfileImage)).await()
    }

    override suspend fun getUserInfo(user: User) {

        val result= fstore.collection(USERS_REF).document(user.id).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            /*user.name= (data[KEY_NAME] as String?) ?: ""
            user.profileImage= (data[KEY_LOCAL_PROFILE_IMAGE] as String?) ?: ""
            user.remoteProfileImage= (data[KEY_REMOTE_PROFILE_IMAGE] as String?) ?: ""
            user.isEmailVerified= ((data[KEY_EMAIL_VERIFIED] as Boolean?) ?: false)
            user.profile= (data[KEY_PROFILE] as String?) ?: ""
            user.bossVerified= (data[KEY_BOSS_VERIFICATION] as String?) ?: ""
            user.team= (data[KEY_TEAM] as String?) ?: ""
            user.teamId= (data[KEY_TEAM_ID] as String?) ?: ""
            user.teamInvitationState= Reply.valueOf(data[KEY_TEAM_INVITATION_STATE] as String)
            user.holidayDaysPerYear= (data[KEY_HOLIDAY_DAYS_PER_YEAR] as Long?) ?: 22L
            user.internalEmployee= (data[KEY_INTERNAL_EMPLOYEE] as Boolean?) ?: false
            user.isBoss= (data[KEY_BOSS] as String?) ?: ""*/
        }
    }

    override suspend fun addUser(user: User) {
        fstore.runTransaction { _ ->
            fstore.collection(USERS_REF).document(user.id).set(user)
            if (user.isBoss) {
                fstore.collection(REF_MANAGERS).document(user.id).set(mapOf<String, Object>(
                    KEY_BOSS to user.id as Object, KEY_AREA_ID to user.areaId as Object,
                    KEY_DEPARTMENT_ID to user.departmentId as Object,
                    KEY_BOSS_LEVEL to user.bossLevel as Object
                ))
            }
        }.await()
    }



}