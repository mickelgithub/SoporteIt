package es.samiralkalii.myapps.soporteit.framework

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRegisterService
import es.samiralkalii.myapps.domain.User

private val TAG: String= "RegisterService"

class RegisterService(val fbAuth: FirebaseAuth, val fstore: FirebaseFirestore,
                      val loginSuccessfull: MutableLiveData<Boolean>): IRegisterService {

    override fun registerUser(name: String, mail: String, pass: String, profileImage: String) {
        fbAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //we have registered user successfully
                val user= fbAuth.currentUser
                if (!TextUtils.isEmpty(profileImage)) {
                    //we have to upload first the user image profile

                }
                user?.let {
                    val user= User(user.uid, name, mail, pass, "")
                    fstore.collection("users").document(user.id).set(user).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "documentSnapshot added ${documentReference}")
                    }.addOnFailureListener{ e ->
                        Log.d(TAG, "error adding user $e")
                    }
                }

            } else {
                //something wrong
            }
        }


    }
}