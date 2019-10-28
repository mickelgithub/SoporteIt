package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import java.io.File
import java.io.InputStream

class UserStorageRepository(val userStorage: IUserStorage) {

    suspend fun saveProfileImage(user: User, profileImage: File)= userStorage.saveProfileImage(user, profileImage)
    suspend fun getProfileImage(user: User)= userStorage.getProfileImage(user)
    suspend fun deleleProfileImage(user: User, fileName: String)= userStorage.deleleProfileImage(user, fileName)
}

interface IUserStorage {
    suspend fun saveProfileImage(user: User, profileImage: File)
    suspend fun getProfileImage(user: User): InputStream?
    suspend fun deleleProfileImage(user: User, fileName: String): Unit
}