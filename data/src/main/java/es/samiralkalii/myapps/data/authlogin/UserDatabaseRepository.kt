package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class UserDatabaseRepository(val userDatabase: IUserDatabase) {

    suspend fun addUser(user: User)= userDatabase.addUser(user)
}

interface IUserDatabase {
    suspend fun addUser(user: User): Boolean
}