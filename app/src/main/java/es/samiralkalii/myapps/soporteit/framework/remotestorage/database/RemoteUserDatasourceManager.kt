package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private const val USERS_REF= "users"

class RemoteUserDatasourceManager(val fstore: FirebaseFirestore, val fbAuth: FirebaseAuth):
    RemoteUserRepository.IRemoteUserDatasource {

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

    override suspend fun updateEmailVerified(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_EMAIL_VERIFIED to true)).await()
    }

    override suspend fun updateImageProfile(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_LOCAL_PROFILE_IMAGE to user.localProfileImage,
            KEY_REMOTE_PROFILE_IMAGE to user.remoteProfileImage)).await()
    }

    override suspend fun getUserInfo(user: User) {
        val result= fstore.collection(USERS_REF).document(user.id).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            user.name= (data[KEY_NAME] as String?) ?: ""
            user.localProfileImage= (data[KEY_LOCAL_PROFILE_IMAGE] as String?) ?: ""
            user.remoteProfileImage= (data[KEY_REMOTE_PROFILE_IMAGE] as String?) ?: ""
            user.emailVerified= ((data[KEY_EMAIL_VERIFIED] as Boolean?) ?: false)
        }
    }

    override suspend fun addUser(user: User) {

        fstore.collection(USERS_REF).document(user.id).set(user).await()

    }

}