package es.samiralkalii.myapps.data.common

import es.samiralkalii.myapps.domain.common.AreasDepartments

class RemoteCommonDataRepository(private val remoteCommonDataDataSource: IRemoteCommonDataDataSource) {

    suspend fun getAreasDepartments()= remoteCommonDataDataSource.getAreasDepartments()

}

interface IRemoteCommonDataDataSource {

    suspend fun getAreasDepartments(): AreasDepartments
}