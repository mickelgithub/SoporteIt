package es.samiralkalii.myapps.soporteit.framework.remotestorage.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IRemoteUserStorageDataSource
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

fun String.fileExtension(): String {
    return substringAfterLast(".", "")
}

private val PROFILE_BASE_DIR= "profile"
private val PROFILE_IMAGE_NAME= "profile"

class RemoteUserStorageDataSourceManager(val fstorage: FirebaseStorage): IRemoteUserStorageDataSource {

    val logger= LoggerFactory.getLogger(RemoteUserStorageDataSourceManager::class.java)

    override suspend fun deleleProfileImage(user: String, fileName: String) {
        val mStorageRef = fstorage.getReference("${user}${File.separator}${PROFILE_BASE_DIR}${File.separator}${PROFILE_IMAGE_NAME}.${fileName.fileExtension()}")
        mStorageRef.delete().await()
    }

    override suspend fun saveProfileImage(user: String, profileImage: File): String {

        val mStorageRef = fstorage.getReference("${user}${File.separator}${PROFILE_BASE_DIR}${File.separator}${PROFILE_IMAGE_NAME}.${profileImage.name.fileExtension()}")
        val result= mStorageRef.putFile(Uri.fromFile(profileImage)).await()
        if (result.task.isComplete) {
            val url= mStorageRef.downloadUrl.await()
            return url.toString()
        }
        return ""
    }

    override suspend fun getProfileImage(user: String, profileImage: String): InputStream? {
        val mStorageRef = fstorage.getReference("${user}${File.separator}${PROFILE_BASE_DIR}${File.separator}${PROFILE_IMAGE_NAME}.${profileImage.fileExtension()}")
        val streamResult= mStorageRef.stream.await()
        return streamResult.stream
    }

}