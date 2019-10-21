package es.samiralkalii.myapps.soporteit.framework.filesystem

import android.content.Context
import android.net.Uri
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import java.io.File
import java.io.InputStream

private val PROFILE_IMAGE_NAME= "profile_image"

class FileSystemManager(val context: Context): IFileSystemManager {

    override suspend fun copyFileFromExternalToInternal(externalFile: String): File {
        val uri= Uri.parse(externalFile)
        val type= context.contentResolver.getType(uri)
        val inputStream= context.contentResolver.openInputStream(uri)
        val ext= type?.substringAfterLast(File.separator)

        val internalFile= File(context.filesDir, "${PROFILE_IMAGE_NAME}.${ext}")
        if (internalFile.exists()) {
            internalFile.delete()
        }
        context.openFileOutput(internalFile.name, Context.MODE_PRIVATE).use {
            it.write(inputStream?.readBytes())
            it.close()
        }
        inputStream?.close()
        return internalFile
    }

    override suspend fun copyFileFromStreamToInternal(inputStream: InputStream, name: String): File {
        val ext= name.substringAfterLast(".")
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