package es.samiralkalii.myapps.data.authlogin

class AuthLoginRepository(val authLoginService: AuthLoginService ) {

    fun checkUserLoggedIn()= authLoginService.checkUserLoggedIn()

    fun signInUser(mail:String, pass: String)= authLoginService.signInUser(mail, pass)

}

interface AuthLoginService {

    fun checkUserLoggedIn()
    fun signInUser(mail: String, pass: String)

}