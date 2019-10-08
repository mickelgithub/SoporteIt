package es.samiralkalii.myapps.preference

import es.samiralkalii.myapps.domain.User

class PreferenceRepository(val preferences: IPreferences) {

    suspend fun getUserFromPreferences()= preferences.getUserFromPreferences()
}

interface IPreferences {
    suspend fun getUserFromPreferences(): User?
}