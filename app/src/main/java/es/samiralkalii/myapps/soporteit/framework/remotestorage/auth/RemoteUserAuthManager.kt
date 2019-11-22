package es.samiralkalii.myapps.soporteit.framework.remotestorage.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.samiralkalii.myapps.data.authlogin.IRemoteUserAuthDataSource
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory

class RemoteUserAuthManager(val fbAuth: FirebaseAuth): IRemoteUserAuthDataSource {

    private val logger= LoggerFactory.getLogger(RemoteUserAuthManager::class.java)

    override suspend fun sendEmailVerification(user: User) {
        fbAuth.currentUser?.sendEmailVerification()?.await()
        logger.debug("Hemos enviado el correo de verificacion a "+ user.email)
    }

    override suspend fun checkUserLoggedIn(user: User): Boolean {
        if (fbAuth.currentUser!= null) {
            val currentUser= fbAuth.currentUser as FirebaseUser
            if (user!= User.EMPTY) {
                if (!user.emailVerified) {
                    currentUser.reload().await()
                    if (currentUser.isEmailVerified) {
                        user.emailVerified= true
                    }
                }
            }
            return true
        }
        return false
    }

    override suspend fun signInUser(user: User, firstTime: Boolean) {
        val authResult= fbAuth.signInWithEmailAndPassword(user.email, user.password).await()
        if (authResult.user!= null) {
            val firebaseUser = authResult.user as FirebaseUser
            if (firstTime) {
                user.id = firebaseUser.uid
                user.creationDate = firebaseUser.metadata?.creationTimestamp ?: 0L
            }
            if (!user.emailVerified) {
                user.emailVerified= firebaseUser.isEmailVerified
            }
        }
    }

    override suspend fun logupUser(user: User) {
        val authResult = fbAuth.createUserWithEmailAndPassword(user.email, user.password).await()
        user.creationDate= authResult.user?.metadata?.creationTimestamp ?: 0L
        user.id= authResult.user?.uid ?: ""
    }

}