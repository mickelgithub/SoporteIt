package es.samiralkalii.myapps.soporteit.framework.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IUserStorage
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

class UserStorage(val fstorage: FirebaseStorage): IUserStorage {

    val logger= LoggerFactory.getLogger(UserStorage::class.java)

    override suspend fun saveProfileImage(user: User, profileImage: File): String {

        val mStorageRef = FirebaseStorage.getInstance().getReference("${user.id}${File.separator}${PROFILE_BASE_DIR}${File.separator}${PROFILE_IMAGE_NAME}.${profileImage.name.fileExtension()}")
        val result= mStorageRef.putFile(Uri.fromFile(profileImage)).await()
        if (result.task.isComplete) {
            val uri= mStorageRef.downloadUrl.await()
            val url= uri.toString()
            return url
        }
        return ""
    }

    override suspend fun getProfileImage(user: User): InputStream? {
        val mStorageRef = FirebaseStorage.getInstance().getReference("${user.id}${File.separator}${PROFILE_BASE_DIR}${File.separator}")
        val listFiles= mStorageRef.list(1).await()
        if (!listFiles.items.isEmpty()) {
            val imageReference= listFiles.items[0]
            val streamResult= imageReference.stream.await()
            return streamResult.stream
        }
        return null
    }

}