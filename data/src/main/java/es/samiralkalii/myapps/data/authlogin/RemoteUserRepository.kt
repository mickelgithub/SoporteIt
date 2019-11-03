package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class RemoteUserRepository(val remoteUserDatasource: IRemoteUserDatasource) {

    suspend fun addUser(user: User)= remoteUserDatasource.addUser(user)
    suspend fun getUserInfo(user: User)= remoteUserDatasource.getUserInfo(user)
    suspend fun updateEmailVerified(user: User)= remoteUserDatasource.updateEmailVerified(user)
    suspend fun updateImageProfile(user: User)= remoteUserDatasource.updateImageProfile(user)
}

interface IRemoteUserDatasource {
    suspend fun addUser(user: User)
    suspend fun getUserInfo(user: User)
    suspend fun updateEmailVerified(user: User)
    suspend fun updateImageProfile(user: User)
}