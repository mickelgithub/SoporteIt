package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class RemoteUserAuthRepository(val remoteUserAuthDataSource: IRemoteUserAuthDataSource) {

    suspend fun logupUser(user: User)= remoteUserAuthDataSource.logupUser(user)
    suspend fun signInUser(user: User, firstTime: Boolean)= remoteUserAuthDataSource.signInUser(user, firstTime)
    suspend fun sendEmailVerification(user: User)= remoteUserAuthDataSource.sendEmailVerification(user)
    suspend fun checkUserLoggedIn(user: User)= remoteUserAuthDataSource.checkUserLoggedIn(user)

}

interface IRemoteUserAuthDataSource {
    suspend fun logupUser(user: User)
    suspend fun signInUser(user: User, firstTime: Boolean)
    suspend fun checkUserLoggedIn(user: User): Boolean
    suspend fun sendEmailVerification(user: User)
}