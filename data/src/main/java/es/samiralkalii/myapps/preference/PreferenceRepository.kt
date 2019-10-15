package es.samiralkalii.myapps.preference

import es.samiralkalii.myapps.domain.User

class PreferenceRepository(val preferences: IPreferences) {

    suspend fun getUserFromPreferences()= preferences.getUserFromPreferences()
    suspend fun saveUserToPreferences(user: User)= preferences.saveUser(user)
}

interface IPreferences {
    suspend fun getUserFromPreferences(): User
    suspend fun saveUser(user: User): Unit
}