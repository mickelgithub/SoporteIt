package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.soporteit.framework.firebase.auth.UserAccess
import es.samiralkalii.myapps.soporteit.framework.firebase.database.UserDatabase
import es.samiralkalii.myapps.usecase.authlogin.UserAccessUseCase

class SplashViewModel() : ViewModel() {


    private val _userLogged= MutableLiveData<Boolean>()
    val userLogged: LiveData<Boolean>
        get() = _userLogged

    val userAccessFramework: IUserAccess = UserAccess(FirebaseAuth.getInstance())
    val userDatabaseFramework: IUserDatabase= UserDatabase(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    private val userAccessRepository= UserAccessRepository(userAccessFramework)
    private val userDatabaseRepository= UserDatabaseRepository(userDatabaseFramework)

    private val userAccessUseCase= UserAccessUseCase(userAccessRepository, userDatabaseRepository)

    fun checkUserLoggedIn() {
        _userLogged.value= userAccessUseCase.checkUserLoggedIn()
    }

}