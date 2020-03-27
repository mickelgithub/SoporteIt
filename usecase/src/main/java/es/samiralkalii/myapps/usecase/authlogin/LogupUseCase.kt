package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

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

    suspend operator fun invoke(user: User): Result {
        logger.debug("Vamos a registar el usuario ${user.email}")

        var holidaysDay= -1
        var internalEmployee= false
        var profileImage= ""
        var remoteProfileImage= ""

        if (user.isBoss) {
            holidaysDay= User.DEFAULT_HOLIDAY_DAYS_FOR_INTERNALS
            internalEmployee= true
        }

        //we get user.id and user.creationDate
        val (userId,createdAt)= remoteUserAuthRepository.logupUser(user.email, user.password)
        //registration OK
        //we have to add the user profile image de local and remote storage
        if (user.profileImage.isNotBlank()) {
            //we get user.localProfileImage
            val profileImageFile = fileSystemRepository.copyFileFromExternalToInternal(user.profileImage)
            profileImage= profileImageFile.absolutePath
            //we get user.remoteProfileImage
            remoteProfileImage= remoteUserStorageRepository.saveProfileImage(userId, profileImageFile)
        }
        //we have to add the user to the database
        val messagingToken= preferenceRepository.getMessagingToken()
        var userUpdated= updateUser(user, userId, holidaysDay, internalEmployee,
            createdAt, profileImage, remoteProfileImage, messagingToken)
        remoteUserRepository.addUser(userUpdated)

//        remoteUserRepository.updateMessagingToken(user.messagingToken)
        preferenceRepository.saveUser(userUpdated)
        //remoteUserAuthRepository.sendEmailVerification(user)
        if (user.isBoss) {
            return Result.LoggedUpAsManagerTeamOk(user)
        }
        return Result.LoggedUpOk(userUpdated)
    }

    private fun updateUser(user: User, id: String, holidaysDay: Int, internalEmployee: Boolean,
                           createdAt: String, profileImage: String,
                           remoteProfileImage: String, messagingToken: String)= user.copy(id= id, holidayDays = holidaysDay,
        internalEmployee = internalEmployee, createdAt = createdAt, profileImage = profileImage,
    remoteProfileImage = remoteProfileImage, messagingToken = messagingToken)

}