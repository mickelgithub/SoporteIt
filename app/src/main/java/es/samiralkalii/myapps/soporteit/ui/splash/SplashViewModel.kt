package es.samiralkalii.myapps.soporteit.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.preference.PreferenceRepository
import es.samiralkalii.myapps.soporteit.framework.firebase.auth.UserAccess
import es.samiralkalii.myapps.soporteit.framework.firebase.database.UserDatabase
import es.samiralkalii.myapps.soporteit.framework.sharedpreferences.MySharedPreferences
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.UserAccessUseCase

class SplashViewModel(application: Application) : AndroidViewModel(application) {


    private val _splashState= MutableLiveData<ScreenState<SplashState>>()
    val splashState: LiveData<ScreenState<SplashState>>
        get() = _splashState

    val userAccessFramework: IUserAccess = UserAccess(FirebaseAuth.getInstance())
    val userDatabaseFramework: IUserDatabase= UserDatabase(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    val sharedPreferencesFramework: IPreferences= MySharedPreferences(application.applicationContext)

    private val userAccessRepository= UserAccessRepository(userAccessFramework)
    private val userDatabaseRepository= UserDatabaseRepository(userDatabaseFramework)
    private val preferencesRepository= PreferenceRepository(sharedPreferencesFramework)

    private val userAccessUseCase= UserAccessUseCase(userAccessRepository, userDatabaseRepository, preferencesRepository)

    fun checkUserLoggedIn() {
        val logged= userAccessUseCase.checkUserLoggedIn()
        if (logged) {
            _splashState.value= ScreenState.Render(SplashState.LoggedIn)
        } else {
            //check if the user has been logged sometime ago
        }
    }

}