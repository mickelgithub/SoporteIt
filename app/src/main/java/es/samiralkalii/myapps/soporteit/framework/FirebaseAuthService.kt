package es.samiralkalii.myapps.soporteit.framework

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.IAuthService

class FirebaseAuthService(val fbAuth: FirebaseAuth, val userLogged: MutableLiveData<Boolean>): IAuthService {

    override fun checkUserLoggedIn() {
        userLogged.value= (fbAuth.currentUser!= null)
    }
}


