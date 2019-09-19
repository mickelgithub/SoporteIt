package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.domain.User

class UserAccessUseCase(val userAccessRepository: UserAccessRepository, userDatabaseRepository: UserDatabaseRepository) {

    fun checkUserLoggedIn()= userAccessRepository.checkUserLoggedIn()

    suspend fun registerUser(user: User): Boolean {
        if (userAccessRepository.registerUser(user)) {
            //user registered correctly
            //we have to add the user profile image de local and remote storage
            TODO()
            //we have to add the user to the database
            TODO()

        }
        return false
    }
}