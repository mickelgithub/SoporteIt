package es.samiralkalii.myapps.soporteit.framework

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.AuthLoginService

class FirebaseAuthLoginService(val fbAuth: FirebaseAuth, val userLoginSuccess: MutableLiveData<Boolean>, val userLogged: MutableLiveData<Boolean>): AuthLoginService {

    override fun checkUserLoggedIn() {
        userLogged.value= (fbAuth.currentUser!= null)
    }

    override fun signInUser(mail: String, pass: String) {
        fbAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(OnCompleteListener<AuthResult>() {
            if (it.isSuccessful()) {
                userLoginSuccess.value= true
            } else {
                userLoginSuccess.value= false
            }
        })
    }
}


