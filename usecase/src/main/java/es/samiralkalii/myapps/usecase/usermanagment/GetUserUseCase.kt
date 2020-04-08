package es.samiralkalii.myapps.usecase.usermanagment

import es.samiralkalii.myapps.preference.PreferenceRepository

class GetUserUseCase(private val preferenceRepository: PreferenceRepository) {

    suspend operator fun invoke()= preferenceRepository.getUser()
}