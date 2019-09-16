package es.samiralkalii.myapps.soporteit.framework

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.ILoginService

class FirebaseLoginService(val fbAuth: FirebaseAuth,
                           val loginSuccessfull: MutableLiveData<Boolean>): ILoginService {

    override fun signInUser(mail: String, pass: String) {
        fbAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(OnCompleteListener<AuthResult>() {
            if (it.isSuccessful()) {
                loginSuccessfull.value= true
            } else {
                loginSuccessfull.value= false
            }
        })
    }
}