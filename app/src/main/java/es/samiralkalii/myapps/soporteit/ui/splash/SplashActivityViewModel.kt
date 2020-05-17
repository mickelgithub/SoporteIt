package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseViewModel
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.CheckUserAuthUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class SplashActivityViewModel(private val checkUserAuthUseCase: CheckUserAuthUseCase) : BaseViewModel() {

    private val logger= LoggerFactory.getLogger(SplashActivityViewModel::class.java)

    override val uiModel= null

    private val _splashState= MutableLiveData<ScreenState<SplashState>>()
    val splashState: LiveData<ScreenState<SplashState>>
        get() = _splashState

    override fun init(data: Bundle?) {
        checkUserAuth()
    }

    private fun checkUserAuth() {

        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.debug(".........................${error}")
            when (error) {
                is FirebaseNetworkException -> {
                    _splashState.postValue(ScreenState.Render(SplashState.ShowMessage(R.string.no_internet_connection)))
                }
                else -> {
                    _splashState.postValue(ScreenState.Render(SplashState.ShowMessage(R.string.not_controled_error)))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            val result = async(Dispatchers.IO) {
                checkUserAuthUseCase()
            }.await()
            when (result) {
                is CheckUserAuthUseCase.Result.Logged -> {
                    _splashState.value= ScreenState.Render(SplashState.LoggedIn(result.user))
                }
                is CheckUserAuthUseCase.Result.Relogged -> {
                    _splashState.value= ScreenState.Render(SplashState.Relogged(result.user))
                }
                CheckUserAuthUseCase.Result.FirstAccess -> {
                    _splashState.value= ScreenState.Render(SplashState.FirstAccess)
                }
                is CheckUserAuthUseCase.Result.Error -> {
                    _splashState.value= ScreenState.Render(SplashState.ShowMessage((R.string.no_internet_connection)))
                }
            }
        }
    }
}