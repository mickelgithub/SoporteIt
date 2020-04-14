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

    suspend operator fun invoke(userEmail: String, pass: String): Result {
        logger.debug("Vamos a login el usuario ${userEmail}")
        var updateEmailVerified= false
        var updateProfileImage= false
        //we get user.id and user.creationDate
        val (isEmailVerified, userId)= remoteUserAuthRepository.signInUser(userEmail, pass, true)
        //login correcto
        //we get user.name, user.localProfileImage and user.remoteProfileImage
        var userObj= remoteUserRepository.getUserInfo(userId)
        if (userObj.remoteProfileImage.isNotBlank()) {
            val imageInputStream= remoteUserStorageRepository.getProfileImage(userId, userObj.profileImage)
            imageInputStream?.use {
                val imageFile= fileSystemRepository.copyFileFromStreamToInternal(it, userObj.profileImage)
                if (userObj.profileImage!= imageFile.absolutePath) {
                    userObj= userObj.copy(profileImage = imageFile.absolutePath)
                    updateProfileImage= true
                }

            }
        }
        //if the mail is verified and is not updated en firebase databaase, we have to do it

        if (isEmailVerified && !userObj.isEmailVerified) {
            updateEmailVerified= true
            userObj= userObj.copy(isEmailVerified = updateEmailVerified)
        }
        userObj= userObj.copy(messagingToken = preferenceRepository.getMessagingToken())
        preferenceRepository.saveUser(userObj)
        if (updateEmailVerified || updateProfileImage) {
            remoteUserRepository.updateEmailVerifiedOrProfileImage(userId, userObj.isEmailVerified, userObj.profileImage)
        }
        remoteUserRepository.updateMessagingToken(userObj.messagingToken)
        return Result.LoginOk(userObj)
    }

}