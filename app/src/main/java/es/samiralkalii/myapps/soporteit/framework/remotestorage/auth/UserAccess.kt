package es.samiralkalii.myapps.soporteit.framework.remotestorage.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

class UserAccess(val fbAuth: FirebaseAuth): IUserAccess {

    override suspend fun sendEmailVerification(user: User) {
        fbAuth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun checkUserLoggedIn(user: User): Boolean {
        if (fbAuth.currentUser!= null) {
            val currentUser= fbAuth.currentUser as FirebaseUser
            if (user!= User.Empty) {
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