package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class UserAccessRepository(val userAccess: IUserAccess) {

    suspend fun registerUser(user: User)= userAccess.registerUser(user)
    suspend fun signInUser(user: User)= userAccess.signInUser(user)
    fun checkUserLoggedIn()= userAccess.checkUserLoggedIn()

}

interface IUserAccess {
    suspend fun registerUser(user: User): Boolean
    fun signInUser(user: User)
    fun checkUserLoggedIn(): Boolean
}