package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class RemoteUserDatabaseRepository(val userDatabase: IUserDatabase) {

    suspend fun addUser(user: User)= userDatabase.addUser(user)
    suspend fun getUserInfo(user: User)= userDatabase.getUserInfo(user)
    suspend fun updateEmailVerified(user: User)= userDatabase.updateEmailVerified(user)
    suspend fun updateImageProfile(user: User)= userDatabase.updateImageProfile(user)
}

interface IUserDatabase {
    suspend fun addUser(user: User)
    suspend fun getUserInfo(user: User)
    suspend fun updateEmailVerified(user: User)
    suspend fun updateImageProfile(user: User)
}