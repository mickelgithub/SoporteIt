package es.samiralkalii.myapps.filesystem

import java.io.File
import java.io.InputStream

class FileSystemRepository(val fileSystem: IFileSystemManager) {

    suspend fun copyFileFromExternalToInternal(externalFile: String)=
        fileSystem.copyFileFromExternalToInternal(externalFile)

    suspend fun copyFileFromStreamToInternal(inputStream: InputStream, name: String)=
        fileSystem.copyFileFromStreamToInternal(inputStream, name)

}

interface IFileSystemManager {

    suspend fun copyFileFromExternalToInternal(externalFile: String): File
    suspend fun copyFileFromStreamToInternal(inputStream: InputStream, name: String): File

}

