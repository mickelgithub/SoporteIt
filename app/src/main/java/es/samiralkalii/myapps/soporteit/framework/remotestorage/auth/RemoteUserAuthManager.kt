package es.samiralkalii.myapps.soporteit.framework.remotestorage.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.samiralkalii.myapps.data.authlogin.IRemoteUserAuthDataSource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.formatDate
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class RemoteUserAuthManager(val fbAuth: FirebaseAuth): IRemoteUserAuthDataSource {

    private val logger= LoggerFactory.getLogger(RemoteUserAuthManager::class.java)

    override suspend fun sendEmailVerification(user: User) {
        fbAuth.currentUser?.sendEmailVerification()?.await()
        logger.debug("Hemos enviado el correo de verificacion a "+ user.email)
    }

    override suspend fun checkUserLoggedIn(isEmailAlreadyVerified: Boolean): Pair<Boolean, Boolean> {
        if (fbAuth.currentUser!= null) {
            val currentUser= fbAuth.currentUser as FirebaseUser
            if (!isEmailAlreadyVerified) {
                currentUser.reload().await()
                if (currentUser.isEmailVerified) {
                    return (true to true)
                }
            }
            return (true to false)
        }
        return (false to false)
    }

    override suspend fun logupUser(user: String, pass: String): Pair<String, String> {
        val authResult = fbAuth.createUserWithEmailAndPassword(user, pass).await()
        val createdAtMilis= authResult.user?.metadata?.creationTimestamp ?: 0L
        val createdAt= formatDate(createdAtMilis)
        val userId= authResult.user?.uid ?: ""
        return (userId to createdAt)

    }

    override suspend fun signInUser(user: String, pass: String, firstTime: Boolean): Pair<Boolean, String> {
        val authResult= fbAuth.signInWithEmailAndPassword(user, pass).await()
        var id= ""
        if (authResult.user!= null) {
            val firebaseUser = authResult.user as FirebaseUser
            if (firstTime) {
                id = firebaseUser.uid
                //user.createdAt = firebaseUser.metadata?.creationTimestamp ?: 0L*/
            }
            return firebaseUser.isEmailVerified to id
        }
        return false to ""
    }

}