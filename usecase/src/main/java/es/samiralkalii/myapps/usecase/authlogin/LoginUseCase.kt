package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.AuthLoginRepository

class LoginUseCase(val authLoginRepository: AuthLoginRepository) {


    fun signInUser(mail: String, pass: String)= authLoginRepository.signInUser(mail, pass)
}