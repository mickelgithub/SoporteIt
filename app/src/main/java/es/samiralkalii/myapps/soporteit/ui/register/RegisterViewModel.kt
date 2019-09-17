package es.samiralkalii.myapps.soporteit.ui.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.RegisterRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.framework.FirebaseRegisterService
import es.samiralkalii.myapps.usecase.authlogin.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val TAG= "RegisterViewModel"

class RegisterViewModel:  ViewModel() {

    private val _registerSuccessfull= MutableLiveData<Boolean>()
    val registerSuccessFull
        get() = _registerSuccessfull

    var name= ""
    var mail= ""
    var pass= ""
    var profileImage= ""


    val registerService= FirebaseRegisterService(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        _registerSuccessfull)

    val registerRepository= RegisterRepository(registerService)

    val registerUseCase= RegisterUseCase(registerRepository)

    fun registerUser(name: String, mail: String, pass: String, profileImage: String) {

        viewModelScope.launch {
            _registerSuccessfull.value= withContext(Dispatchers.IO) {
                registerUseCase.registerUser(
                    User(name, mail, pass, profileImage)
                )}
                Log.d(TAG, "$name $mail $pass $profileImage")
        }
    }


    fun onRegister()= registerUser(name, mail, pass, profileImage)


}