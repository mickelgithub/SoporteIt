package es.samiralkalii.myapps.data.authlogin

import es.samiralkalii.myapps.domain.User

class UserStorageRepository(val userStorage: IUserStorage) {

    suspend fun saveProfileImage(user: User)= userStorage.saveProfileImage(user)

}

interface IUserStorage {
    suspend fun saveProfileImage(user: User): String
}