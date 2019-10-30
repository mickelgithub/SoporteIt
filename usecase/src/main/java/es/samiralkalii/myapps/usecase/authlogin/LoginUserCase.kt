package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class LoginUserCase(private val remoteUserAuthRepository: RemoteUserAuthRepository,
                    private val remoteUserDatabaseRepository: RemoteUserDatabaseRepository,
                    private val preferenceRepository: PreferenceRepository,
                    private val fileSystemRepository: FileSystemRepository,
                    private val remoteUserStorageRepository: RemoteUserStorageRepository) {

    private val logger = LoggerFactory.getLogger(LoginUserCase::class.java)

    sealed class Result() {
        class LoginOk(val user: User): Result()
    }

    suspend fun loginUser(user: User): Result {
        logger.debug("Vamos a login el usuario ${user.email}")
        //we get user.id and user.creationDate
        remoteUserAuthRepository.signInUser(user, true)
        //login correcto
        //we get user.name, user.localProfileImage and user.remoteProfileImage
        val emailVerified= user.emailVerified
        remoteUserDatabaseRepository.getUserInfo(user)
        if (user.remoteProfileImage.isNotBlank()) {
            val imageInputStream= remoteUserStorageRepository.getProfileImage(user)
            imageInputStream?.use {
                val imageFile= fileSystemRepository.copyFileFromStreamToInternal(it, user.localProfileImage)
                user.localProfileImage= imageFile.absolutePath
            }
        }
        //if the mail is verified and is not updated en firebase databaase, weh have to do it
        var updateDatabase= false
        if (emailVerified && !user.emailVerified) {
            user.emailVerified= emailVerified
            updateDatabase= true
        }
        preferenceRepository.saveUserToPreferences(user)
        if (updateDatabase) {
            remoteUserDatabaseRepository.updateEmailVerified(user)
        }
        return Result.LoginOk(user)
    }

}