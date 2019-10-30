package es.samiralkalii.myapps.database

import es.samiralkalii.myapps.domain.User

class LocalUserDatabaseRepository<T>(val localUserDatabase: ILocalUserDatabase<T>) {

    suspend fun addOrUpdateUser(user: User)= localUserDatabase.addOrUpdateUser(user)
    suspend fun getUser()= localUserDatabase.getUser()

}

interface ILocalUserDatabase<T> {
    suspend fun addOrUpdateUser(user: User)
    suspend fun getUser(): T
}