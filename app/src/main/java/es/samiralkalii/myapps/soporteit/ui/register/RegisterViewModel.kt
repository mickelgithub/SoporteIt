package es.samiralkalii.myapps.soporteit.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.framework.firebase.auth.UserAccess
import es.samiralkalii.myapps.soporteit.framework.firebase.database.UserDatabase
import es.samiralkalii.myapps.usecase.authlogin.UserAccessUseCase
import kotlinx.coroutines.launch


private val TAG= "RegisterViewModel"

class RegisterViewModel:  ViewModel() {

    private val _registerSuccessfull= MutableLiveData<Boolean>()
    val registerSuccessfull
        get() = _registerSuccessfull

    private val _progressbarVisible= MutableLiveData<Boolean>()
    val progressVisible
        get() = _progressbarVisible

    val user= User()

    val userAccessFramework: IUserAccess = UserAccess(FirebaseAuth.getInstance())
    val userDatabaseFramework: IUserDatabase = UserDatabase(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    private val userAccessRepository= UserAccessRepository(userAccessFramework)
    private val userDatabaseRepository= UserDatabaseRepository(userDatabaseFramework)

    private val userAccessUseCase= UserAccessUseCase(userAccessRepository, userDatabaseRepository)

    fun registerUser() {
        _progressbarVisible.value= true
        viewModelScope.launch {
            _registerSuccessfull.value= userAccessUseCase.registerUser(user)
            _progressbarVisible.value= false

            //Log.d(TAG, "$name $mail $pass $profileImage")
        }
    }


    fun onRegister()= registerUser()


}