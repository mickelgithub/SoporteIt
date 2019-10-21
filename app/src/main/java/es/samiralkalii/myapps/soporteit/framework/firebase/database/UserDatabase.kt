package es.samiralkalii.myapps.soporteit.framework.firebase.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private val USERS_REF= "users"
private val USER_NAME_KEY= "name"
private val USER_LOCAL_PROFILE_IMAGE_KEY= "localProfileImage"
private val USER_REMOTE_PROFILE_IMAGE_KEY= "remoteProfileImage"

class UserDatabase(val fstore: FirebaseFirestore): IUserDatabase {

    val logger= LoggerFactory.getLogger(UserDatabase::class.java)

    override suspend fun getUserInfo(user: User) {
        val result= fstore.collection(USERS_REF).document(user.id).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            user.name= (data[USER_NAME_KEY] as String?) ?: ""
            user.localProfileImage= (data[USER_LOCAL_PROFILE_IMAGE_KEY] as String?) ?: ""
            user.remoteProfileImage= (data[USER_REMOTE_PROFILE_IMAGE_KEY] as String?) ?: ""
        }
    }

    override suspend fun addUser(user: User): Boolean {

        fstore.collection(USERS_REF).document(user.id).set(user).await()
        return true

    }



}