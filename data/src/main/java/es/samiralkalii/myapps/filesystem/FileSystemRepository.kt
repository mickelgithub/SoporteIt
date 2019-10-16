package es.samiralkalii.myapps.filesystem

import java.io.File

class FileSystemRepository(val fileSystem: IFileSystemManager) {

    suspend fun copyFileFromExternalToInternal(externalFile: String)=
        fileSystem.copyFileFromExternalToInternal(externalFile)

}

interface IFileSystemManager {

    suspend fun copyFileFromExternalToInternal(externalFile: String): File

}

