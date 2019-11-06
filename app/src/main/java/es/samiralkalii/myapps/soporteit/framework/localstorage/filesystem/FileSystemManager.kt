package es.samiralkalii.myapps.soporteit.framework.localstorage.filesystem

import android.content.Context
import android.net.Uri
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.storage.fileExtension
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

private val PROFILE_IMAGE_NAME= "profile_image"

class FileSystemManager(val context: Context): IFileSystemManager {

    private val logger = LoggerFactory.getLogger(FileSystemManager::class.java)

    override suspend fun deleteImageProfile(user: User) {
        val internalFile= File(context.filesDir, "${PROFILE_IMAGE_NAME}.${user.localProfileImage.fileExtension()}")
        if (internalFile.exists()) {
            internalFile.delete()
        }
    }

    override suspend fun copyFileFromExternalToInternal(user: User, externalFile: String): File {
        val uri= Uri.parse(externalFile)
        val type= context.contentResolver.getType(uri)
        val inputStream= context.contentResolver.openInputStream(uri)
        val ext= type?.substringAfterLast(File.separator)

        val internalFile= File(context.filesDir, "${PROFILE_IMAGE_NAME}.${ext}")
        if (internalFile.exists()) {
            internalFile.delete()
        }
        inputStream?.use { input ->
            context.openFileOutput(internalFile.name, Context.MODE_PRIVATE).use {
                it.write(input.readBytes())
            }
            user.localProfileImage= internalFile.absolutePath
        }
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
        }
        return internalFile
    }

    override suspend fun compare2Images(externalImage: String, internalImage: String): Boolean {
        logger.debug("Comparando imagenes.....")

        val uri= Uri.parse(externalImage)

        val externalImageInputStream= context.contentResolver.openInputStream(uri)!!

        var equals= true
        val inputBloque= IntArray(128)
        context.openFileInput(internalImage.substringAfterLast(File.separator)).use { internalImageInputStream ->
            externalImageInputStream.use { externalImageInputStream ->

                var externalByte= externalImageInputStream.read()
                var internalByte= internalImageInputStream.read()
                while (externalByte!= -1 && internalByte!= -1) {
                    if (externalByte!= internalByte) {
                        equals= false
                        break
                    }
                    externalByte= externalImageInputStream.read()
                    internalByte= internalImageInputStream.read()
                }
                if (equals) {
                    if (externalByte!= internalByte) {
                        equals= false
                    }
                }
            }
        }
        logger.debug("El valor de equals es ${equals}")
        return equals
    }
}