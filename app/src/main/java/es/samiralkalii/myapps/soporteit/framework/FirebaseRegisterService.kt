package es.samiralkalii.myapps.soporteit.framework

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRegisterService
import kotlinx.coroutines.tasks.await

private val TAG: String= "FirebaseRegisterService"

class FirebaseRegisterService(val fbAuth: FirebaseAuth, val fstore: FirebaseFirestore,
                              val loginSuccessfull: MutableLiveData<Boolean>): IRegisterService {



    override suspend fun registerUser(name: String, mail: String, pass: String, profileImage: String): Boolean {
        val authResult= fbAuth.createUserWithEmailAndPassword(mail, pass).await()
        if (authResult.user!= null) {
            //registracion ha ido OK
            return true
        }

        /*fbAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //we have registered user successfully

                val connectedUser= fbAuth.currentUser
                if (!TextUtils.isEmpty(profileImage)) {
                    //we have to upload first the user image profile

                }
                connectedUser?.let {
                    val user= User(name, mail, pass, "")
                    user.id= connectedUser.uid
                    fstore.collection("users").document(user.id).set(user).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "documentSnapshot added ${documentReference}")
                        loginSuccessfull.value= true
                    }.addOnFailureListener{ e ->
                        Log.d(TAG, "error adding user $e")
                        loginSuccessfull.value= false
                    }
                }

            } else {
                //something wrong
            }
        }
        return false*/
        return false

    }
}