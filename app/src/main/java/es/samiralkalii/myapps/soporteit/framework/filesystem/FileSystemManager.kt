package es.samiralkalii.myapps.soporteit.framework.filesystem

import android.content.Context
import es.samiralkalii.myapps.filesystem.IFileSystemManager


class FileSystemManager(val context: Context): IFileSystemManager {

    override suspend fun copyFileFromExternalToInternal(
        externalFileStr: String,
        internalFileStr: String
    ) {
        /*val externalFile= File(externalFileStr)
        val inputStream= FileInputStream(externalFile)

        val internalFile= File(context.filesDir, internalFileStr)
        context.openFileOutput(internalFileStr, Context.MODE_PRIVATE).use {
            it.write(inputStream.readBytes())
            it.close()
        }

        inputStream.close()*/

    }
}