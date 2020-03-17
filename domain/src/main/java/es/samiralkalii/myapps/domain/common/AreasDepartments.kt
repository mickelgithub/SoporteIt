package es.samiralkalii.myapps.domain.common

class AreasDepartments(val areasDepartments: Map<Area, List<Department>>) {

    fun getDepartmentsName(area: String): List<String> {
        var areaEntry= areasDepartments.keys.filter { it.name== area }.first()
        return areasDepartments[areaEntry]!!.map { it.name }
    }



    fun isEmpty()= areasDepartments.isEmpty()

}

data class Area(val id: String, val name: String)

data class Department(val id: String, val name: String)
