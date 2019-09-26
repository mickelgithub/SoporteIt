package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.domain.User

class UserAccessUseCase(val userAccessRepository: UserAccessRepository, val userDatabaseRepository: UserDatabaseRepository) {

    fun checkUserLoggedIn()= userAccessRepository.checkUserLoggedIn()

    suspend fun registerUser(user: User): Boolean {
        val uid= userAccessRepository.registerUser(user)
        if (uid.isNotBlank()) {
            //user registered correctly
            //we have to add the user to the database
            user.id= uid
            userDatabaseRepository.addUser(user)
            //we have to add the user profile image de local and remote storage
            if (user.profileImage.isNotBlank()) {

            }

        }
        return false
    }
}