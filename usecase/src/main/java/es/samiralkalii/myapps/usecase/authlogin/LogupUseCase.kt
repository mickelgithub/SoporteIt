package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.database.LocalUserDatabaseRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import org.slf4j.LoggerFactory

class LogupUseCase(private val remoteUserAuthRepository: RemoteUserAuthRepository,
                   private val remoteUserRepository: RemoteUserRepository,
                   private val  localUserDatabaseRepository: LocalUserDatabaseRepository,
                   private val remoteUserStorageRepository: RemoteUserStorageRepository,
                   private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(LogupUseCase::class.java)

    sealed class Result() {
        class RegisteredOk(val user: User): Result()
    }

    suspend operator fun invoke(user: User, profileImage: String= ""): Result {
        logger.debug("Vamos a registar el usuario ${user.email}")

        //we get user.id and user.creationDate
        remoteUserAuthRepository.logupUser(user)
        //registration OK
        //we have to add the user profile image de local and remote storage
        if (profileImage.isNotBlank()) {
            //we get user.localProfileImage
            val profileImageFile = fileSystemRepository.copyFileFromExternalToInternal(user, profileImage)
            //we get user.remoteProfileImage
            remoteUserStorageRepository.saveProfileImage(user, profileImageFile)
        }
        //we have to add the user to the database
        remoteUserRepository.addUser(user)
        localUserDatabaseRepository.addOrUpdateUser(user)
        remoteUserAuthRepository.sendEmailVerification(user)

        return Result.RegisteredOk(user)
    }

}