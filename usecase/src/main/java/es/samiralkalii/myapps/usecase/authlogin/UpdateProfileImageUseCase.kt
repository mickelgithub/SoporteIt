package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class UpdateProfileImageUseCase(private val preferenceRepository: PreferenceRepository,
                                private val remoteUserRepository: RemoteUserRepository,
                                private val remoteUserStorageRepository: RemoteUserStorageRepository,
                                private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(UpdateProfileImageUseCase::class.java)


    suspend operator fun invoke(user: String, actualImageProfile: String, newImageProfile: String) {

        if (actualImageProfile.isBlank() && newImageProfile.isNotBlank()) {
            //profileImage added
            val imageAdded = fileSystemRepository.copyFileFromExternalToInternal(newImageProfile)
            val remoteProfileImage= remoteUserStorageRepository.saveProfileImage(user, imageAdded)
            remoteUserRepository.updateProfileImage(user, imageAdded.absolutePath, remoteProfileImage)
            preferenceRepository.updateProfileImage(imageAdded.absolutePath, remoteProfileImage)
        } else if (actualImageProfile.isNotBlank() && newImageProfile.isBlank()) {
            //profileImage deleted
            remoteUserStorageRepository.deleleProfileImage(user, actualImageProfile)
            fileSystemRepository.deleteImageProfile(actualImageProfile)
            remoteUserRepository.updateProfileImage(user, "", "")
            preferenceRepository.updateProfileImage("", "")
        } else {
            //profileImage changed
            val imageAdded = fileSystemRepository.copyFileFromExternalToInternal(newImageProfile)
            remoteUserStorageRepository.deleleProfileImage(user, actualImageProfile)
            val remoteProfileImage= remoteUserStorageRepository.saveProfileImage(user, imageAdded)
            remoteUserRepository.updateProfileImage(user, imageAdded.absolutePath, remoteProfileImage)
            preferenceRepository.updateProfileImage(imageAdded.absolutePath, remoteProfileImage)
        }
    }
}