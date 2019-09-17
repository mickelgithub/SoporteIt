package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RegisterRepository
import es.samiralkalii.myapps.domain.User

class RegisterUseCase(val registerRepository: RegisterRepository) {

    fun registerUser(user: User) {
        registerRepository.registerUser(user.name, user.email, user.password, user.profileImage)

    }
}