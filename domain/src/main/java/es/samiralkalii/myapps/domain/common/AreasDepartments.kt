package es.samiralkalii.myapps.domain.common

class AreasDepartments(val areasDepartments: Map<Area, List<Department>>) {

    fun getDepartmentsName(area: String): List<String> {
        var areaEntry= areasDepartments.keys.filter { it.name== area }.first()
        return areasDepartments[areaEntry]!!.map { it.name }
    }

    fun getArea(area: String)= areasDepartments.keys.filter { it.name.equals(area, true) }.first()

    fun getDepartment(area: String, department: String)= areasDepartments.get(getArea(area))!!.filter { it.name.equals(department, true) }.first()

    fun isEmpty()= areasDepartments.isEmpty()

}

data class Area(val id: String, val name: String)

data class Department(val id: String, val name: String)
