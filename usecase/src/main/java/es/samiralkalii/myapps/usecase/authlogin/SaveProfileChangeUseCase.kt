package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class SaveProfileChangeUseCase(private val preferenceRepository: PreferenceRepository,
                               private val remoteUserRepository: RemoteUserRepository,
                               private val remoteUserStorageRepository: RemoteUserStorageRepository,
                               private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(SaveProfileChangeUseCase::class.java)


    suspend operator fun invoke(
        user: User,
        imageUri: String,
        imageChanged: Boolean,
        chooseYourProfile: String
    ) {
        if (imageChanged) {
            if (user.profileImage.isBlank() && imageUri.isNotBlank()) {
                //profileImage added
                val imageAdded = fileSystemRepository.copyFileFromExternalToInternal(imageUri)
                //**user.profileImage= imageAdded.absolutePath
                remoteUserStorageRepository.saveProfileImage(user.id, imageAdded)
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            } else if (user.profileImage.isNotBlank() && imageUri.isBlank()) {
                //profileImage deleted
                remoteUserStorageRepository.deleleProfileImage(user, user.profileImage)
                fileSystemRepository.deleteImageProfile(user)
                //**user.profileImage= ""
                //**user.remoteProfileImage= ""
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            } else {
                //profileImage changed
                val imageAdded = fileSystemRepository.copyFileFromExternalToInternal(imageUri)
                val oldFileName = user.profileImage
                //**user.profileImage= imageAdded.absolutePath
                remoteUserStorageRepository.deleleProfileImage(user, oldFileName)
                remoteUserStorageRepository.saveProfileImage(user.id, imageAdded)
                remoteUserRepository.updateImageProfile(user)
                preferenceRepository.updateImageProfile(user)
            }
        }
    }
}
        //we check if the profile has been introduced
        /*if (user.profile.isNotBlank() && user.profile!= chooseYourProfile && user.bossVerified== "N") {
            //**user.bossVerified= ""
            remoteUserRepository.updateProfile(user.profile, user.id)
            //**remoteUserRepository.updateBossVerification(user.bossVerified, user.id)
            //**preferenceRepository.updateBossVerification(user.bossVerified)
            preferenceRepository.updateProfile(user.profile)

        }
    }
}*/