package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class UserAccessRepository(val userAccess: IUserAccess) {

    suspend fun logupUser(user: User)= userAccess.logupUser(user)
    suspend fun signInUser(user: User, firstTime: Boolean)= userAccess.signInUser(user, firstTime)
    suspend fun sendEmailVerification(user: User)= userAccess.sendEmailVerification(user)
    suspend fun checkUserLoggedIn(user: User)= userAccess.checkUserLoggedIn(user)

}

interface IUserAccess {
    suspend fun logupUser(user: User)
    suspend fun signInUser(user: User, firstTime: Boolean)
    suspend fun checkUserLoggedIn(user: User): Boolean
    suspend fun sendEmailVerification(user: User)
}