package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class LoginUserCase(private val remoteUserAuthRepository: RemoteUserAuthRepository,
                    private val remoteUserRepository: RemoteUserRepository,
                    private val preferenceRepository: PreferenceRepository,
                    private val fileSystemRepository: FileSystemRepository,
                    private val remoteUserStorageRepository: RemoteUserStorageRepository) {

    private val logger = LoggerFactory.getLogger(LoginUserCase::class.java)

    sealed class Result() {
        class LoginOk(val user: User): Result()
    }

    suspend operator fun invoke(user: User): Result {
        logger.debug("Vamos a login el usuario ${user.email}")
        //we get user.id and user.creationDate
        remoteUserAuthRepository.signInUser(user, true)
        //login correcto
        //we get user.name, user.localProfileImage and user.remoteProfileImage
        val isEmailVerified= user.emailVerified
        remoteUserRepository.getUserInfo(user)
        if (user.remoteProfileImage.isNotBlank()) {
            val imageInputStream= remoteUserStorageRepository.getProfileImage(user)
            imageInputStream?.use {
                val imageFile= fileSystemRepository.copyFileFromStreamToInternal(it, user.localProfileImage)
                user.localProfileImage= imageFile.absolutePath
            }
        }
        //if the mail is verified and is not updated en firebase databaase, we have to do it
        var updateDatabase= false
        if (isEmailVerified && !user.emailVerified) {
            user.emailVerified= isEmailVerified
            updateDatabase= true
        }
        preferenceRepository.saveUser(user)
        if (updateDatabase) {
            remoteUserRepository.updateEmailVerified(user)
        }
        user.messagingToken= preferenceRepository.getMessagingToken()
        remoteUserRepository.updateMessagingToken(user.messagingToken)
        return Result.LoginOk(user)
    }

}