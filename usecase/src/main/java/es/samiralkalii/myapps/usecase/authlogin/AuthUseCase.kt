package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.AuthLoginRepository

class AuthUseCase(val authLoginRepository: AuthLoginRepository) {

    fun checkUserLoggedIn()= authLoginRepository.checkUserLoggedIn()
}