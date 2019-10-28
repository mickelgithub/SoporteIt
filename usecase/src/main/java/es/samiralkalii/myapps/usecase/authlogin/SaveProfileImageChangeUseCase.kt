package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class SaveProfileImageChangeUseCase(private val preferenceRepository: PreferenceRepository,
                                    private val userDatabaseRepository: UserDatabaseRepository,
                                    private val userStorageRepository: UserStorageRepository,
                                    private val fileSystemRepository: FileSystemRepository) {

    private val logger= LoggerFactory.getLogger(SaveProfileImageChangeUseCase::class.java)


    suspend fun saveProfileImageChange(user: User, imageUri: String) {

        if (user.localProfileImage.isBlank() && imageUri.isNotBlank()) {
            //profileImage added
            val imageAdded= fileSystemRepository.copyFileFromExternalToInternal(user, imageUri)
            user.localProfileImage= imageAdded.absolutePath
            userStorageRepository.saveProfileImage(user, imageAdded)
            userDatabaseRepository.updateImageProfile(user)
            preferenceRepository.updateImageProfile(user)
        } else if (user.localProfileImage.isNotBlank() && imageUri.isBlank()) {
            //profileImage deleted
            userStorageRepository.deleleProfileImage(user, user.localProfileImage)
            fileSystemRepository.deleteImageProfile(user)
            user.localProfileImage= ""
            userDatabaseRepository.updateImageProfile(user)
            preferenceRepository.updateImageProfile(user)
        } else {
            //profileImage changed
            val imageAdded= fileSystemRepository.copyFileFromExternalToInternal(user, imageUri)
            val oldFileName= user.localProfileImage
            user.localProfileImage= imageAdded.absolutePath
            userStorageRepository.deleleProfileImage(user, oldFileName)
            userStorageRepository.saveProfileImage(user, imageAdded)
            userDatabaseRepository.updateImageProfile(user)
            preferenceRepository.updateImageProfile(user)
        }
    }
}