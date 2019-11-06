package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRemoteUserDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.KEY_EMAIL_VERIFIED
import es.samiralkalii.myapps.soporteit.ui.util.KEY_LOCAL_PROFILE_IMAGE
import es.samiralkalii.myapps.soporteit.ui.util.KEY_NAME
import es.samiralkalii.myapps.soporteit.ui.util.KEY_REMOTE_PROFILE_IMAGE
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private const val USERS_REF= "users"

class RemoteUserDatasourceManager(val fstore: FirebaseFirestore): IRemoteUserDatasource {

    override suspend fun updateEmailVerified(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_EMAIL_VERIFIED to true)).await()
    }

    override suspend fun updateImageProfile(user: User) {
        fstore.collection(USERS_REF).document(user.id).update(mapOf( KEY_LOCAL_PROFILE_IMAGE to user.localProfileImage,
            KEY_REMOTE_PROFILE_IMAGE to user.remoteProfileImage)).await()
    }

    val logger= LoggerFactory.getLogger(RemoteUserDatasourceManager::class.java)

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