package es.samiralkalii.myapps.data.splash

class SplashRepository(val authService: AuthService ) {

    fun checkUserLoggedIn()= authService.checkUserLoggedIn()

    fun signInUser(mail:String, pass: String)= authService.signInUser(mail, pass)

}

interface AuthService {

    fun checkUserLoggedIn()
    fun signInUser(mail: String, pass: String)

}