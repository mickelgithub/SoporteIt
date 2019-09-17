package es.samiralkalii.myapps.soporteit.framework

import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.IAuthService

class FirebaseAuthService(val fbAuth: FirebaseAuth): IAuthService {

    override fun checkUserLoggedIn()= fbAuth.currentUser!= null
}


