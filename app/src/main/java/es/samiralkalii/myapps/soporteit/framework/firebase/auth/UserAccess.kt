package es.samiralkalii.myapps.soporteit.framework.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

class UserAccess(val fbAuth: FirebaseAuth): IUserAccess {

    override fun checkUserLoggedIn()= fbAuth.currentUser!= null

    override suspend fun signInUser(user: User, firstTime: Boolean) {
        val authResult= fbAuth.signInWithEmailAndPassword(user.email, user.password).await()
        if (firstTime && authResult.user!= null) {
            val firebaseUser= authResult.user as FirebaseUser
            user.id= firebaseUser.uid
            user.creationDate= firebaseUser.metadata?.creationTimestamp ?: 0L
        }
    }

    override suspend fun logupUser(user: User) {
        val authResult = fbAuth.createUserWithEmailAndPassword(user.email, user.password).await()
        user.creationDate= authResult.user?.metadata?.creationTimestamp ?: 0L
        user.id= authResult.user?.uid ?: ""
    }

}