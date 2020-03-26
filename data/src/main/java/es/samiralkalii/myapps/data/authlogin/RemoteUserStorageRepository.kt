package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import java.io.File
import java.io.InputStream

class RemoteUserStorageRepository(val remoteUserStorageDataSourceDataSource: IRemoteUserStorageDataSource) {

    suspend fun saveProfileImage(userId: String, profileImage: File)= remoteUserStorageDataSourceDataSource.saveProfileImage(userId, profileImage)
    suspend fun getProfileImage(user: User)= remoteUserStorageDataSourceDataSource.getProfileImage(user)
    suspend fun deleleProfileImage(user: User, fileName: String)= remoteUserStorageDataSourceDataSource.deleleProfileImage(user, fileName)
}

interface IRemoteUserStorageDataSource {
    suspend fun saveProfileImage(userId: String, profileImage: File): String
    suspend fun getProfileImage(user: User): InputStream?
    suspend fun deleleProfileImage(user: User, fileName: String): Unit
}