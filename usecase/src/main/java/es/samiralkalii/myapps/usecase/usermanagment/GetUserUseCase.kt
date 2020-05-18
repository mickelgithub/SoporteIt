package es.samiralkalii.myapps.usecase.usermanagment

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.PreferenceRepository

class GetUserUseCase(private val preferenceRepository: PreferenceRepository, private val remoteUserRepository: RemoteUserRepository) {

    suspend operator fun invoke(user: String?= null): User {
        if (user.isNullOrBlank()) {
            return preferenceRepository.getUser()
        } else {
            return remoteUserRepository.getUserInfo(user)
        }
    }
}