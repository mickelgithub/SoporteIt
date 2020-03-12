package es.samiralkalii.myapps.usecase.common

import es.samiralkalii.myapps.data.common.RemoteCommonDataRepository
import org.slf4j.LoggerFactory

class GetAreasDepartmentsUseCase(private val remoteCommonDataRepository: RemoteCommonDataRepository) {

    private val logger = LoggerFactory.getLogger(GetAreasDepartmentsUseCase::class.java)

    suspend operator fun invoke()= remoteCommonDataRepository.getAreasDepartments()
}