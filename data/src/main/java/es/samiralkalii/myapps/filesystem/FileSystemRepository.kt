package es.samiralkalii.myapps.filesystem

class FileSystemRepository(val fileSystem: IFileSystemManager) {

    suspend fun copyFileFromExternalToInternal(externalFile: String, internalFile: String)=
        fileSystem.copyFileFromExternalToInternal(externalFile, internalFile)

}

interface IFileSystemManager {

    suspend fun copyFileFromExternalToInternal(externalFile: String, internalFile: String)

}

