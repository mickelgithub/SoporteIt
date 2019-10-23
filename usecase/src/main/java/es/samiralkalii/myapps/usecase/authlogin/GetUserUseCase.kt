package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.preference.PreferenceRepository

class GetUserUseCase(private val preferenceRepository: PreferenceRepository) {

    suspend fun getUser()= preferenceRepository.getUserFromPreferences()
}