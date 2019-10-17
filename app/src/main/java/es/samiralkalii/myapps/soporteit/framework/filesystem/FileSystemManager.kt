package es.samiralkalii.myapps.soporteit.framework.filesystem

import android.content.Context
import android.net.Uri
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import java.io.File

private val PROFILE_IMAGE_NAME= "profile_image"

class FileSystemManager(val context: Context): IFileSystemManager {

    override suspend fun copyFileFromExternalToInternal(externalFileStr: String): File {
        val uri= Uri.parse(externalFileStr)
        val type= context.contentResolver.getType(uri)
        val inputStream= context.contentResolver.openInputStream(uri)
        val ext= type.substringAfterLast(File.separator)

        val internalFile= File(context.filesDir, "${PROFILE_IMAGE_NAME}.${ext}")
        if (internalFile.exists()) {
            internalFile.delete()
        }
        context.openFileOutput(internalFile.name, Context.MODE_PRIVATE).use {
            it.write(inputStream.readBytes())
            it.close()
        }
        inputStream.close()
        return internalFile
    }
}