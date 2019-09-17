package es.samiralkalii.myapps.data.authlogin

import kotlinx.coroutines.Deferred

class RegisterRepository(val registerService: IRegisterService) {

    fun registerUser(name: String, mail: String, pass: String, profileImage: String)= registerService.registerUser(name, mail, pass, profileImage)
}

interface IRegisterService {
    fun registerUser(name: String, mail: String, pass: String, profileImage: String): Deferred<Boolean>
}