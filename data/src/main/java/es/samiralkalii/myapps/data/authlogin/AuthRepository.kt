package es.samiralkalii.myapps.data.authlogin

class AuthRepository(val authService: IAuthService) {

    fun checkUserLoggedIn()= authService.checkUserLoggedIn()



}

interface IAuthService {
    fun checkUserLoggedIn(): Boolean
}

