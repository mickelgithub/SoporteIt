package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository

class CheckUserEmailValidatedUseCase(private val userAccessRepository: UserAccessRepository) {

    fun checkEmailValidation()
}