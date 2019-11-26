package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class SaveProfileImageChangeUseCase(private val preferenceRepository: PreferenceRepository,
                                    private val remoteUserRepository: RemoteUserRepository,
                                    private val remoteUserStorageRepository: RemoteUserStorageRepository,
                                    private val fileSystemRepository: FileSystemRepository) {

    private val logger= LoggerFactory.getLogger(SaveProfileImageChangeUseCase::class.java)


    suspend operator fun invoke(user: User, imageUri: String, imageChanged: Boolean) {
        if (imageChanged) {
            if (user.localProfileImage.isBlank() && imageUri.isNotBlank()) {
                //profileImage added
                val imageAdded= fileSystemRepository.copyFileFromExternalToInternal(user, imageUri)
                user.localProfileImage= imageAdded.absolutePath
                remoteUserStorageRepository.saveProfileImage(user, imageAdded)
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            } else if (user.localProfileImage.isNotBlank() && imageUri.isBlank()) {
                //profileImage deleted
                remoteUserStorageRepository.deleleProfileImage(user, user.localProfileImage)
                fileSystemRepository.deleteImageProfile(user)
                user.localProfileImage= ""
                user.remoteProfileImage= ""
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            } else {
                //profileImage changed
                val imageAdded= fileSystemRepository.copyFileFromExternalToInternal(user, imageUri)
                val oldFileName= user.localProfileImage
                user.localProfileImage= imageAdded.absolutePath
                remoteUserStorageRepository.deleleProfileImage(user, oldFileName)
                remoteUserStorageRepository.saveProfileImage(user, imageAdded)
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            }
        }
        //we check if the profile has been introduced
        if (user.profile.isNotBlank() && user.bossVerification== "N") {
            user.bossVerification= ""
            preferenceRepository.updateBossVerification(user.bossVerification)
            preferenceRepository.updateProfile(user.profile)
            remoteUserRepository.updateProfile(user.profile, user.id)
            remoteUserRepository.updateBossVerification(user.bossVerification, user.id)
        }
    }
}