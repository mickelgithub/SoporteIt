package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.PreferenceRepository

class RegisterUseCase(private val userAccessRepository: UserAccessRepository,
                      private val userDatabaseRepository: UserDatabaseRepository,
                      private val  preferenceRepository: PreferenceRepository) {

    sealed class Result() {
        class RegisteredOk(): Result()
    }

    suspend fun registerUser(user: User): Result {
        val uid = userAccessRepository.registerUser(user)
        //user registered correctly
        //we have to add the user to the database
        user.id = uid
        userDatabaseRepository.addUser(user)
        //we have to add the user profile image de local and remote storage
        if (user.profileImage.isNotBlank()) {
        }
        preferenceRepository.saveUserToPreferences(user)
        return Result.RegisteredOk()
    }

}