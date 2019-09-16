package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.LoginRepository

class LoginUseCase(val loginRepository: LoginRepository) {


    fun signInUser(mail: String, pass: String)= loginRepository.signInUser(mail, pass)
}