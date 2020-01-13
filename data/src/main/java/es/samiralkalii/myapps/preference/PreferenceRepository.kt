package es.samiralkalii.myapps.preference

import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team

class PreferenceRepository(val preferences: IPreferences) {

    suspend fun getUser()= preferences.getUser()
    suspend fun saveUser(user: User)= preferences.saveUser(user)
    suspend fun updateEmailVerified()= preferences.updateEmailVerified()
    suspend fun updateImageProfile(user: User)= preferences.updateImageProfile(user)
    suspend fun updateMessagingToken(token: String)= preferences.updateMessagingToken(token)
    suspend fun getMessagingToken()= preferences.getMessagingToken()
    suspend fun updateProfile(profile: String)= preferences.updateProfile(profile)
    suspend fun updateBossVerification(bossVerification: String)= preferences.updateBossVerification(bossVerification)
    suspend fun updateTeamCreated(team: Team)= preferences.updateTeamCreated(team)
}

interface IPreferences {
    suspend fun getUser(): User
    suspend fun saveUser(user: User)
    suspend fun updateEmailVerified()
    suspend fun updateImageProfile(user: User)
    suspend fun updateMessagingToken(token: String)
    suspend fun getMessagingToken(): String
    suspend fun updateProfile(profile: String)
    suspend fun updateBossVerification(bossVerification: String)
    suspend fun updateTeamCreated(team: Team)
}