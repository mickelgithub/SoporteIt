package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRemoteUserDatasource
import es.samiralkalii.myapps.domain.STATE_SUBSCRIBED
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
                    .update(mapOf(KEY_BOSS_VERIFIED_AT to dateEnLocalTimeZone,
                    KEY_MEMBERSHIP_CONFIRMATION to SI,
                    KEY_MEMBERSHIP_CONFIRMED_AT to dateEnLocalTimeZone))

            }
        }
        return dateEnLocalTimeZone
    }

    override suspend fun signOut() {
        fbAuth.signOut()
    }

    override suspend fun updateEmailVerifiedOrProfileImage(user: String, emailVerified: Boolean?, profileImage: String?) {
        if (emailVerified!= null && emailVerified && !profileImage.isNullOrBlank()) {
            fstore.collection(USERS_REF).document(user).update(mapOf( KEY_IS_EMAIL_VERIFIED to emailVerified,
            KEY_PROFILE_IMAGE to profileImage)).await()
        } else if (emailVerified!= null && emailVerified) {
            fstore.collection(USERS_REF).document(user).update(mapOf( KEY_IS_EMAIL_VERIFIED to emailVerified)).await()
        } else if (!profileImage.isNullOrBlank()) {
            fstore.collection(USERS_REF).document(user).update(mapOf(KEY_PROFILE_IMAGE to profileImage)).await()
        }
    }

    override suspend fun updateprofileImage(user: String, profileImage: String, remoteProfileImage: String) {
        fstore.collection(USERS_REF).document(user).update(mapOf( KEY_PROFILE_IMAGE to profileImage,
            KEY_REMOTE_PROFILE_IMAGE to remoteProfileImage)).await()
    }

    override suspend fun getUserInfo(user: String): User {

        val result= fstore.collection(USERS_REF).document(user).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            return data.toUser()
        }
        return User.EMPTY
    }

    /*private fun createUser(user: String, data: Map<String, Any>)= User(
        id = user,
        email = data[KEY_EMAIL] as String? ?: "",
        password = data[KEY_PASS] as String? ?: "",
        name= data[KEY_NAME] as String? ?: "",
        profileImage = data[KEY_PROFILE_IMAGE] as String? ?: "",
        remoteProfileImage = data[KEY_REMOTE_PROFILE_IMAGE] as String? ?: "",
        profileBackColor = (data[KEY_PROFILE_BACK_COLOR] as Long? ?: Integer.MIN_VALUE.toLong()).toInt(),
        profileTextColor= (data[KEY_PROFILE_TEXT_COLOR] as Long? ?: Integer.MIN_VALUE.toLong()).toInt(),
        createdAt = data[KEY_CREATED_AT] as String? ?: "",
        isEmailVerified= data[KEY_IS_EMAIL_VERIFIED] as Boolean? ?: false,
        profile = data[KEY_PROFILE] as String? ?: "",
        profileId = data[KEY_PROFILE_ID] as String? ?: "",
        bossCategory = data[KEY_BOSS_CATEGORY] as String? ?: "",
        bossCategoryId = data[KEY_BOSS_CATEGORY_ID] as String? ?: "",
        bossLevel = (data[KEY_BOSS_LEVEL] as Long? ?: 0L).toInt(),
        bossConfirmation = data[KEY_BOSS_CONFIRMATION] as String? ?: "",
        isBoss = data[KEY_BOSS] as Boolean? ?: false,
        bossVerifiedAt = data[KEY_BOSS_VERIFIED_AT] as String? ?: "",
        holidayDays = (data[KEY_HOLIDAY_DAYS] as Long? ?: User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS.toLong()).toInt(),
        internalEmployee = data[KEY_INTERNAL_EMPLOYEE] as Boolean? ?: false,
        area= data[KEY_AREA] as String? ?: "",
        areaId = data[KEY_AREA_ID] as String? ?: "",
        department = data[KEY_DEPARTMENT] as String? ?: "",
        departmentId = data[KEY_DEPARTMENT_ID] as String? ?: "",
        stateChangedAt = data[KEY_STATE_CHANGED_AT] as String? ?: "",
        state = data[KEY_STATE] as String? ?: STATE_SUBSCRIBED,
        membershipConfirmation = data[KEY_MEMBERSHIP_CONFIRMATION] as String? ?: "",
        membershipConfirmedAt = data[KEY_MEMBERSHIP_CONFIRMED_AT] as String? ?: ""

    )*/

    override suspend fun addUser(user: User) {
        fstore.runTransaction { _ ->
            fstore.collection(USERS_REF).document(user.id).set(user)
            if (user.isBoss) {
                fstore.collection(REF_MANAGERS).document(user.id).set(mapOf(
                    KEY_BOSS to user.id as Object, KEY_AREA_ID to user.areaId as Object,
                    KEY_DEPARTMENT_ID to user.departmentId as Object,
                    KEY_BOSS_LEVEL to user.bossLevel as Object
                ))
            }
        }.await()
    }



}