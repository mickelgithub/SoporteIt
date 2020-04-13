package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import java.io.File
import java.io.InputStream

class RemoteUserStorageRepository(val remoteUserStorageDataSourceDataSource: IRemoteUserStorageDataSource) {

    suspend fun saveProfileImage(user: String, profileImage: File)= remoteUserStorageDataSourceDataSource.saveProfileImage(user, profileImage)
    suspend fun getProfileImage(user: User)= remoteUserStorageDataSourceDataSource.getProfileImage(user)
    suspend fun deleleProfileImage(user: String, fileName: String)= remoteUserStorageDataSourceDataSource.deleleProfileImage(user, fileName)
}

interface IRemoteUserStorageDataSource {
    suspend fun saveProfileImage(user: String, profileImage: File): String
    suspend fun getProfileImage(user: User): InputStream?
    suspend fun deleleProfileImage(user: String, fileName: String): Unit
}