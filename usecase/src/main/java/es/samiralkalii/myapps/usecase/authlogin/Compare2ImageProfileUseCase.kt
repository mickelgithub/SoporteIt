package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.filesystem.FileSystemRepository
import org.slf4j.LoggerFactory

class Compare2ImageProfileUseCase(private val fileSystemRepository: FileSystemRepository) {

    private val logger = LoggerFactory.getLogger(Compare2ImageProfileUseCase::class.java)

    suspend fun compare2Images(externalImage: String, internalImage: String)=
        fileSystemRepository.compare2Images(externalImage, internalImage)

}