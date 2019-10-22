package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class LogupUseCase(private val userAccessRepository: UserAccessRepository,
                   private val userDatabaseRepository: UserDatabaseRepository,
                   private val  preferenceRepository: PreferenceRepository,
                   private val userStorageRepository: UserStorageRepository,
                   private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(LogupUseCase::class.java!!)

    sealed class Result() {
        class RegisteredOk(): Result()
    }

    suspend fun logupUser(user: User, profileImage: String= ""): Result {
        logger.debug("Vamos a registar el usuario ${user.email}")

        //we get user.id and user.creationDate
        userAccessRepository.logupUser(user)
        //registration OK
        //we have to add the user profile image de local and remote storage
        if (profileImage.isNotBlank()) {
            //we get user.localProfileImage
            val profileImageFile = fileSystemRepository.copyFileFromExternalToInternal(user, profileImage)
            //we get user.remoteProfileImage
            userStorageRepository.saveProfileImage(user, profileImageFile)
        }
        //we have to add the user to the database
        userDatabaseRepository.addUser(user)
        preferenceRepository.saveUserToPreferences(user)
        return Result.RegisteredOk()
    }

}