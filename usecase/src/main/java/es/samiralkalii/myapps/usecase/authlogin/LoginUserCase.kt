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
        userAccessRepository.signInUser(user)
        //login correcto
        //we have to retrieve userImageProfile and his name
        userDatabaseRepository.getUserInfo(user)
        val imageInputStream= userStorageRepository.getProfileImage(user)
        imageInputStream?.let {
            val imageFile= fileSystemRepository.copyFileFromStreamToInternal(imageInputStream, user.localProfileImage)
            logger.debug("imagefile length"+ imageFile.length())
            user.localProfileImage= imageFile.absolutePath
        }
        preferenceRepository.saveUserToPreferences(user)


        return Result.LoginOk()
    }


}