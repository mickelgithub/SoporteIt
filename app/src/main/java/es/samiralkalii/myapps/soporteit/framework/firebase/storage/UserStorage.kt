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

class UserStorage(val fstorage: FirebaseStorage): IUserStorage {

    override suspend fun saveProfileImage(user: User): String {

        val fileUri = Uri.fromFile(File(user.localProfileImage))
        val mStorageRef = FirebaseStorage.getInstance().getReference("${user.id}/imageProfile/profile.${user.localProfileImage.fileExtension()}")
        val result= mStorageRef.putFile(fileUri).await()
        return result.storage.downloadUrl.result.toString()
    }



}