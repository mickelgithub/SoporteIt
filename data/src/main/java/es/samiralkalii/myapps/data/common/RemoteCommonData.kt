package es.samiralkalii.myapps.data.common

class RemoteCommonData(private val remoteCommonData: IRemoteCommonData) {

    suspend fun getAreasDepartments()= remoteCommonData.getAreasDepartments()

}

interface IRemoteCommonData {

    suspend fun getAreasDepartments(): Map<String, List<String>>
}