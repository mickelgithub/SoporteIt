package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import java.io.File
import java.io.InputStream

class UserStorageRepository(val userStorage: IUserStorage) {

    suspend fun saveProfileImage(user: User, profileImage: File)= userStorage.saveProfileImage(user, profileImage)
    suspend fun getProfileImage(user: User)= userStorage.getProfileImage(user)

}

interface IUserStorage {
    suspend fun saveProfileImage(user: User, profileImage: File): String
    suspend fun getProfileImage(user: User): InputStream?
}