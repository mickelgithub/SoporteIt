package es.samiralkalii.myapps.soporteit.framework

import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IRegisterService
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await

private val TAG: String= "FirebaseRegisterService"

class FirebaseRegisterService(val fbAuth: FirebaseAuth, val fstore: FirebaseFirestore): IRegisterService {



    override suspend fun registerUser(name: String, mail: String, pass: String, profileImage: String): Boolean {
        try {

            val authResult = fbAuth.createUserWithEmailAndPassword(mail, pass).await()
            if (authResult.user != null) {
                //registracion ha ido OK
                //tenemos que dar de alta el usuario en la base de datos
                val connectedUser = fbAuth.currentUser
                connectedUser ?: return false
                if (!TextUtils.isEmpty(profileImage)) {
                    //we have to upload first the user image profile
                }
                connectedUser.let {
                    val user = User(name, mail, pass)
                    user.id = connectedUser.uid
                    fstore.collection("users").document(user.id).set(user).await()
                    return true
                    /*addOnSuccessListener { documentReference ->
                    Log.d(TAG, "documentSnapshot added ${documentReference}")
                    loginSuccessfull.value= true
                }.addOnFailureListener{ e ->
                    Log.d(TAG, "error adding user $e")
                    loginSuccessfull.value= false
                }*/
                }
                //return true
            }
        } catch (e: FirebaseAuthException) {

            when(e) {
                is FirebaseAuthWeakPasswordException ->
                    Log.d(TAG, "Contraseña debil.....");
                is FirebaseAuthInvalidCredentialsException ->
                    Log.d(TAG, "credenciales invalidas")
                is FirebaseAuthUserCollisionException ->
                    Log.d(TAG, "Usuario ya existe")
            }

        } catch (e: FirebaseException) {
            //val errorCode= (e as FirebaseAuthException).errorCode

            Log.e(TAG, "", e)
        } catch (e: Throwable) {
            Log.e(TAG, "", e)
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