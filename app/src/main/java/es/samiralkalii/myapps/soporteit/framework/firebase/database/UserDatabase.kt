package es.samiralkalii.myapps.soporteit.framework.firebase.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

class UserDatabase(val fbAuth: FirebaseAuth, val fstore: FirebaseFirestore): IUserDatabase {

    override suspend fun addUser(user: User): Boolean {

        val connectedUser= fbAuth.currentUser!!
        user.id= connectedUser.uid
        fstore.collection("users").document(user.id).set(user).await()
        fbAuth.signOut()
        return true

    }

}