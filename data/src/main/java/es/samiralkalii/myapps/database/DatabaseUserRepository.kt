package es.samiralkalii.myapps.database

import es.samiralkalii.myapps.domain.User

class DatabaseUserRepository(val databaseHandler: IUserDatabase) {

    suspend fun addOrUpdateUser(user: User)= databaseHandler.addOrUpdateUser(user)
    suspend fun getUser()= databaseHandler.getUser()

}

interface IUserDatabase {
    suspend fun addOrUpdateUser(user: User)
    suspend fun getUser(): Object
}