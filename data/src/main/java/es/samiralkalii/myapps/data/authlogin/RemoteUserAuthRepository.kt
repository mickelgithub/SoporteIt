package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class RemoteUserAuthRepository(val userAuthManager: IRemoteUserAuthDataSource) {

    suspend fun logupUser(user: User)= userAuthManager.logupUser(user)
    suspend fun signInUser(user: User, firstTime: Boolean)= userAuthManager.signInUser(user, firstTime)
    suspend fun sendEmailVerification(user: User)= userAuthManager.sendEmailVerification(user)
    suspend fun checkUserLoggedIn(user: User)= userAuthManager.checkUserLoggedIn(user)

}

interface IRemoteUserAuthDataSource {
    suspend fun logupUser(user: User)
    suspend fun signInUser(user: User, firstTime: Boolean)
    suspend fun checkUserLoggedIn(user: User): Boolean
    suspend fun sendEmailVerification(user: User)
}