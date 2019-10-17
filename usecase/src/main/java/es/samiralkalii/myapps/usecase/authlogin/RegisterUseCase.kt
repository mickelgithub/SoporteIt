package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class RegisterUseCase(private val userAccessRepository: UserAccessRepository,
                      private val userDatabaseRepository: UserDatabaseRepository,
                      private val  preferenceRepository: PreferenceRepository,
                      private val userStorageRepository: UserStorageRepository,
                      private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(RegisterUseCase::class.java!!)

    sealed class Result() {
        class RegisteredOk(): Result()
    }

    suspend fun registerUser(user: User, profileImage: String= ""): Result {
        logger.debug("HOLA; ESTAMOS REGISTRANDO EL USUARIO")
        val uid = userAccessRepository.registerUser(user)
        //registration OK
        user.id = uid
        //we have to add the user profile image de local and remote storage
        if (profileImage.isNotBlank()) {
            val profileImageFile= fileSystemRepository.copyFileFromExternalToInternal(profileImage)
            user.remoteProfileImage= userStorageRepository.saveProfileImage(user, profileImageFile)
            user.localProfileImage= profileImageFile.absolutePath
        }
        //we have to add the user to the database
        userDatabaseRepository.addUser(user)
        preferenceRepository.saveUserToPreferences(user)
        return Result.RegisteredOk()
    }

}