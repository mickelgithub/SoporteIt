package es.samiralkalii.myapps.data.authlogin

class RegisterRepository(val registerService: IRegisterService) {

    fun registerUser(name: String, mail: String, pass: String, profileImage: String)= registerService.registerUser(name, mail, pass, profileImage)
}

interface IRegisterService {
    fun registerUser(name: String, mail: String, pass: String, profileImage: String)
}