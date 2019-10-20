package es.samiralkalii.myapps.soporteit.framework.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

class UserAccess(val fbAuth: FirebaseAuth): IUserAccess {

    override fun checkUserLoggedIn()= fbAuth.currentUser!= null

    override suspend fun signInUser(user: User): Boolean {
        val authResult= fbAuth.signInWithEmailAndPassword(user.email, user.password).await()
        if (authResult.user!= null) {
            user.id= (authResult.user as FirebaseUser).uid
        }
        return authResult.user!= null
    }

    override suspend fun registerUser(user: User) : String {
        val authResult = fbAuth.createUserWithEmailAndPassword(user.email, user.password).await()
        return authResult.user!!.uid;
    }

}