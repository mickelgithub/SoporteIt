package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository

class RegisterUseCase(private val userAccessRepository: UserAccessRepository,
                      private val userDatabaseRepository: UserDatabaseRepository,
                      private val  preferenceRepository: PreferenceRepository,
                      private val userStorageRepository: UserStorageRepository,
                      private val fileSystemRepository: FileSystemRepository) {

    sealed class Result() {
        class RegisteredOk(): Result()
    }

    suspend fun registerUser(user: User): Result {
        val uid = userAccessRepository.registerUser(user)
        //user registered correctly
        //we have to add the user to the database
        user.id = uid
        //we have to add the user profile image de local and remote storage
        if (user.externalProfileImageUri.isNotBlank()) {
            fileSystemRepository.copyFileFromExternalToInternal(user.externalProfileImageUri, user.externalProfileImageUri.substringAfterLast("/"))
            userStorageRepository.saveProfileImage(user)
        }
        userDatabaseRepository.addUser(user)
        preferenceRepository.saveUserToPreferences(user)
        return Result.RegisteredOk()
    }

}