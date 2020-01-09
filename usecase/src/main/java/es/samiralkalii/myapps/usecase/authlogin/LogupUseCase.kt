package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

private const val TEAM_MANAGER_PROFILE= "Responsable de equipo";

class LogupUseCase(private val remoteUserAuthRepository: RemoteUserAuthRepository,
                   private val remoteUserRepository: RemoteUserRepository,
                   private val remoteUserStorageRepository: RemoteUserStorageRepository,
                   private val fileSystemRepository: FileSystemRepository,
                   private val preferenceRepository: PreferenceRepository) {

    private val logger = LoggerFactory.getLogger(LogupUseCase::class.java)

    sealed class Result() {
        class LoggedUpOk(val user: User): Result()
        class LoggedUpAsManagerTeamOk(val user: User): Result()

    }

    suspend operator fun invoke(user: User, profileImage: String= ""): Result {
        logger.debug("Vamos a registar el usuario ${user.email}")

        if (TEAM_MANAGER_PROFILE== user.profile) {
            user.bossVerification= "P"
        }

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
        user.messagingToken= preferenceRepository.getMessagingToken()
        remoteUserRepository.updateMessagingToken(user.messagingToken)
        preferenceRepository.saveUser(user)
        remoteUserAuthRepository.sendEmailVerification(user)
        if (TEAM_MANAGER_PROFILE== user.profile) {
            return Result.LoggedUpAsManagerTeamOk(user)
        }
        return Result.LoggedUpOk(user)
    }

}