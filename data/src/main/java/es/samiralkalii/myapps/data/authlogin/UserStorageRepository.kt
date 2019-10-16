package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User
import java.io.File

class UserStorageRepository(val userStorage: IUserStorage) {

    suspend fun saveProfileImage(user: User, profileImage: File)= userStorage.saveProfileImage(user, profileImage)

}

interface IUserStorage {
    suspend fun saveProfileImage(user: User, profileImage: File): String
}