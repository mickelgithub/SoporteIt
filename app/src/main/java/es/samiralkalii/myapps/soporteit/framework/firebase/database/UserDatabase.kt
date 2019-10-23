package es.samiralkalii.myapps.soporteit.framework.firebase.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.EMAIL_VALIDATED_KEY
import es.samiralkalii.myapps.soporteit.ui.util.LOCAL_PROFILE_IMAGE_KEY
import es.samiralkalii.myapps.soporteit.ui.util.NAME_KEY
import es.samiralkalii.myapps.soporteit.ui.util.REMOTE_PROFILE_IMAGE_KEY
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory


private val USERS_REF= "users"

class UserDatabase(val fstore: FirebaseFirestore): IUserDatabase {

    val logger= LoggerFactory.getLogger(UserDatabase::class.java)

    override suspend fun getUserInfo(user: User) {
        val result= fstore.collection(USERS_REF).document(user.id).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            user.name= (data[NAME_KEY] as String?) ?: ""
            user.localProfileImage= (data[LOCAL_PROFILE_IMAGE_KEY] as String?) ?: ""
            user.remoteProfileImage= (data[REMOTE_PROFILE_IMAGE_KEY] as String?) ?: ""
            user.emailValidated= (data[EMAIL_VALIDATED_KEY] as Boolean?) ?: false
        }
    }

    override suspend fun addUser(user: User) {

        fstore.collection(USERS_REF).document(user.id).set(user).await()

    }



}