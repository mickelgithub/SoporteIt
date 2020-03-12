package es.samiralkalii.myapps.domain.common

class AreasDepartments(val areasDepartments: Map<String, List<String>>) {

    fun getDepartments(area: String)= areasDepartments[area]

    fun isEmpty()= areasDepartments.isEmpty()

}