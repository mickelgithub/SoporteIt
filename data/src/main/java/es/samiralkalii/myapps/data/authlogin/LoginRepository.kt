package es.samiralkalii.myapps.data.authlogin

class LoginRepository(val loginService: ILoginService) {

    fun signInUser(mail:String, pass: String)= loginService.signInUser(mail, pass)
}

interface ILoginService {
    fun signInUser(mail: String, pass: String)
}