package es.samiralkalii.myapps.filesystem

import es.samiralkalii.myapps.domain.User
import java.io.File
import java.io.InputStream

class FileSystemRepository(val fileSystem: IFileSystemManager) {

    suspend fun copyFileFromExternalToInternal(user: User, externalFile: String)=
        fileSystem.copyFileFromExternalToInternal(user, externalFile)

    suspend fun copyFileFromStreamToInternal(inputStream: InputStream, name: String)=
        fileSystem.copyFileFromStreamToInternal(inputStream, name)

}

interface IFileSystemManager {

    suspend fun copyFileFromExternalToInternal(user: User, externalFile: String): File
    suspend fun copyFileFromStreamToInternal(inputStream: InputStream, name: String): File

}

