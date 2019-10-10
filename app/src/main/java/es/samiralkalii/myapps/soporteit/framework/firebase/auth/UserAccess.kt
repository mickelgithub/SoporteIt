package es.samiralkalii.myapps.soporteit.framework.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

class UserAccess(val fbAuth: FirebaseAuth): IUserAccess {

    override fun checkUserLoggedIn()= fbAuth.currentUser!= null

    override suspend fun signInUser(user: User): Boolean {
        val authResult= fbAuth.signInWithEmailAndPassword(user.email, user.password).await()
        return authResult.user!= null
    }

    override suspend fun registerUser(user: User) : String {
        val authResult = fbAuth.createUserWithEmailAndPassword(user.email, user.password).await()
        return authResult.user!!.uid;
        //return (authResult.user != null)
    }

}