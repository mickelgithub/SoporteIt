package es.samiralkalii.myapps.database

class LocalUserDatabaseRepository(val localUserDatabase: ILocalUserDatabase) {

    //suspend fun addOrUpdateUser(user: User)= localUserDatabase.addOrUpdateUser(user)
    //suspend fun getUser()= localUserDatabase.getUser()

}

interface ILocalUserDatabase {
    //suspend fun addOrUpdateUser(user: User)
    //suspend fun getUser(): User
}