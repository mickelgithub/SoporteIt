package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class LoginUserCase(private val userAccessRepository: UserAccessRepository,
                    private val userDatabaseRepository: UserDatabaseRepository,
                    private val preferenceRepository: PreferenceRepository,
                    private val fileSystemRepository: FileSystemRepository,
                    private val userStorageRepository: UserStorageRepository) {

    private val logger = LoggerFactory.getLogger(LoginUserCase::class.java)

    sealed class Result() {
        class LoginOk(): Result()
    }

    suspend fun loginUser(user: User): Result {
        logger.debug("Vamos a login el usuario ${user.email}")
        //we get user.id and user.creationDate
        userAccessRepository.signInUser(user, true)
        //login correcto
        //we get user.name, user.localProfileImage and user.remoteProfileImage
        userDatabaseRepository.getUserInfo(user)
        if (user.remoteProfileImage.isNotBlank()) {
            val imageInputStream= userStorageRepository.getProfileImage(user)
            imageInputStream?.let {
                val imageFile= fileSystemRepository.copyFileFromStreamToInternal(imageInputStream, user.localProfileImage)
                user.localProfileImage= imageFile.absolutePath
            }
        }
        preferenceRepository.saveUserToPreferences(user)
        return Result.LoginOk()
    }


}