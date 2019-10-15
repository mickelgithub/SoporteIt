package es.samiralkalii.myapps.soporteit.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.CheckUserAuthUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch



private val TAG= "SplashViewModel"

class SplashViewModel(val checkUserAuthUseCase: CheckUserAuthUseCase) : ViewModel() {


    private val _splashState= MutableLiveData<ScreenState<SplashState>>()
    val splashState: LiveData<ScreenState<SplashState>>
        get() = _splashState

    /*private val userAccessFramework: IUserAccess = UserAccess(FirebaseAuth.getInstance())
    private val sharedPreferencesFramework: IPreferences= MySharedPreferences(context)

    private val userAccessRepository= UserAccessRepository(userAccessFramework)
    private val preferencesRepository= PreferenceRepository(sharedPreferencesFramework)

    private val checkUserAuthUseCase= CheckUserAuthUseCase(userAccessRepository, preferencesRepository)*/

    fun checkUserAuth() {

        val errorHandler = CoroutineExceptionHandler { _, error ->
            Log.e(TAG, error.toString())
            when (error) {
                is FirebaseNetworkException -> {
                    _splashState.postValue(ScreenState.Render(SplashState.ShowMessage("Error de conexion, comprueba el acceso a Internet y intentalo de nuevo")))
                }
                else -> {
                    _splashState.postValue(ScreenState.Render(SplashState.ShowMessage("Error no controlado!!!")))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            val result = async(Dispatchers.IO) {
                checkUserAuthUseCase.checkUserAuth()
            }.await()
            when (result) {
                CheckUserAuthUseCase.Result.Logged -> {
                    _splashState.value= ScreenState.Render(SplashState.LoggedIn)
                }
                CheckUserAuthUseCase.Result.Relogged -> {
                    _splashState.value= ScreenState.Render(SplashState.Relogged)
                }
                CheckUserAuthUseCase.Result.FirstAccess -> {
                    _splashState.value= ScreenState.Render(SplashState.FirstAccess)
                }
                is CheckUserAuthUseCase.Result.Error -> {
                    _splashState.value= ScreenState.Render(SplashState.ShowMessage(result.message))
                }
            }
        }
    }

}