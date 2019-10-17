package es.samiralkalii.myapps.soporteit.framework.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IUserStorage
import es.samiralkalii.myapps.domain.User
import kotlinx.coroutines.tasks.await
import java.io.File

fun String.fileExtension(): String {
    return substringAfterLast(".", "")
}

private val PROFILE_BASE_DIR= "profile"
private val PROFILE_IMAGE_NAME= "profile"

class UserStorage(val fstorage: FirebaseStorage): IUserStorage {

    override suspend fun saveProfileImage(user: User, imagefile: File): String {

        val mStorageRef = FirebaseStorage.getInstance().getReference("${user.id}${File.separator}${PROFILE_BASE_DIR}${File.separator}${PROFILE_IMAGE_NAME}.${imagefile.name.fileExtension()}")
        val result= mStorageRef.putFile(Uri.fromFile(imagefile)).await()
        if (result.task.isComplete) {
            val uri= mStorageRef.downloadUrl.await()
            val url= uri.toString()
            return url
        }
        return ""
    }

}