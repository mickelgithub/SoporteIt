package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.AuthRepository

class AuthUseCase(val authRepository: AuthRepository) {

    fun checkUserLoggedIn()= authRepository.checkUserLoggedIn()
}