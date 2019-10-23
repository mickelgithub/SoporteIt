package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class UserAccessRepository(val userAccess: IUserAccess) {

    suspend fun logupUser(user: User)= userAccess.logupUser(user)
    suspend fun signInUser(user: User, firstTime: Boolean)= userAccess.signInUser(user, firstTime)
    suspend fun sendEmailVerification(user: User)= userAccess.sendEmailVerification(user)
    fun checkUserLoggedIn()= userAccess.checkUserLoggedIn()

}

interface IUserAccess {
    suspend fun logupUser(user: User)
    suspend fun signInUser(user: User, firstTime: Boolean)
    fun checkUserLoggedIn(): Boolean
    suspend fun sendEmailVerification(user: User)
}