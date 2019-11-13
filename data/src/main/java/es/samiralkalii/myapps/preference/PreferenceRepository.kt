package es.samiralkalii.myapps.preference

import es.samiralkalii.myapps.domain.User

class PreferenceRepository(val preferences: IPreferences) {

    suspend fun getUserFromPreferences()= preferences.getUserFromPreferences()
    suspend fun saveUserToPreferences(user: User)= preferences.saveUser(user)
    suspend fun updateEmailVerified()= preferences.updateEmailVerified()
    suspend fun updateImageProfile(user: User)= preferences.updateImageProfile(user)
    suspend fun updateMessagingToken(token: String)= preferences.updateMessagingToken(token)
    suspend fun getMessaginToken()= preferences.getMessaginToken()
}

interface IPreferences {
    suspend fun getUserFromPreferences(): User
    suspend fun saveUser(user: User)
    suspend fun updateEmailVerified()
    suspend fun updateImageProfile(user: User)
    suspend fun updateMessagingToken(token: String)
    suspend fun getMessaginToken(): String
}